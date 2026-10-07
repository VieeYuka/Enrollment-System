package oopSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JOptionPane;

/**
 * The ONE place that knows how to reach MySQL.
 *
 * - Settings come from "db.properties" (next to the program) or environment
 *   variables, falling back to the defaults below.
 * - The database, every table, the sample subjects and the staff accounts are
 *   created ONCE per program run (not on every query).
 * - Every call to getConnection() returns a fresh connection; callers close it
 *   with try-with-resources.
 */
public class DBConnection {

    // ---- defaults (override in db.properties or with env vars DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD)
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DATABASE = "enrollDB";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "SQLang@246";

    private static final String OPTIONS =
            "?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8";

    private static String host;
    private static String port;
    private static String database;
    private static String user;
    private static String password;

    private static boolean initialized = false;
    private static boolean errorShown = false;

    private DBConnection() {
    }

    // =================================================================
    // PUBLIC API
    // =================================================================

    /**
     * Opens a connection to the enrollment database. The first call also creates
     * the database and tables if they do not exist yet.
     *
     * @throws SQLException when MySQL cannot be reached or the schema cannot be prepared
     */
    public static Connection getConnection() throws SQLException {
        initialize();
        return DriverManager.getConnection(serverUrl() + database + OPTIONS, user, password);
    }

    /** Same as getConnection() but tells the person (once) what went wrong instead of throwing. */
    public static Connection tryConnection() {
        try {
            return getConnection();
        } catch (SQLException e) {
            reportOnce(e);
            return null;
        }
    }

    /** Shows ONE friendly error dialog per run, so a polling screen cannot spam pop-ups. */
    public static void reportOnce(SQLException e) {
        e.printStackTrace();
        if (errorShown) {
            return;
        }
        errorShown = true;
        JOptionPane.showMessageDialog(null,
                "Cannot reach the MySQL database.\n\n" + e.getMessage()
                        + "\n\nCheck that MySQL is running and that the user/password in\n"
                        + "db.properties (or DBConnection.java) are correct.",
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Splits a search box text into lower-case words (commas ignored). */
    public static List<String> words(String query) {
        List<String> result = new ArrayList<String>();
        if (query == null) {
            return result;
        }
        for (String w : query.trim().replace(",", " ").split("\\s+")) {
            if (!w.isEmpty()) {
                result.add(w);
            }
        }
        return result;
    }

    /** Escapes % _ \ so a typed word is matched literally inside LIKE. */
    public static String like(String word) {
        return "%" + word.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
    }

    // =================================================================
    // SETTINGS
    // =================================================================

    private static void loadSettings() {
        Properties p = new Properties();
        File file = new File("db.properties");
        if (file.isFile()) {
            try (InputStream in = new FileInputStream(file)) {
                p.load(in);
            } catch (IOException e) {
                System.err.println("Could not read db.properties: " + e.getMessage());
            }
        }
        host = pick(p, "db.host", "DB_HOST", DEFAULT_HOST);
        port = pick(p, "db.port", "DB_PORT", DEFAULT_PORT);
        database = pick(p, "db.name", "DB_NAME", DEFAULT_DATABASE);
        user = pick(p, "db.user", "DB_USER", DEFAULT_USER);
        password = pick(p, "db.password", "DB_PASSWORD", DEFAULT_PASSWORD);
    }

    private static String pick(Properties p, String key, String env, String fallback) {
        String fromEnv = System.getenv(env);
        if (fromEnv != null && !fromEnv.isEmpty()) {
            return fromEnv;
        }
        String fromFile = p.getProperty(key);
        if (fromFile != null && !fromFile.trim().isEmpty()) {
            return fromFile.trim();
        }
        return fallback;
    }

    private static String serverUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/";
    }

    // =================================================================
    // SCHEMA (runs once)
    // =================================================================

    private static synchronized void initialize() throws SQLException {
        if (initialized) {
            return;
        }
        loadSettings();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL driver not found (add lib/mysql-connector-j to the classpath).", e);
        }

        // 1) the database itself
        try (Connection server = DriverManager.getConnection(serverUrl() + OPTIONS, user, password);
                Statement st = server.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + database
                    + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        }

        // 2) tables, migrations and seed data
        try (Connection conn = DriverManager.getConnection(serverUrl() + database + OPTIONS, user, password)) {
            createTables(conn);
            migrate(conn);
            seed(conn);
        }

        initialized = true;
        System.out.println("Database '" + database + "' is ready.");
    }

    private static void createTables(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {

            // Degree PROGRAMS (BSIT, BSCS ...). Students belong to one of these.
            st.executeUpdate("CREATE TABLE IF NOT EXISTS courses ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "course_code VARCHAR(50) NOT NULL UNIQUE, "
                    + "course_name VARCHAR(150) NOT NULL UNIQUE, "
                    + "description VARCHAR(255) NOT NULL UNIQUE, "
                    + "quota INT NOT NULL, "
                    + "units INT NOT NULL)");

            // SUBJECTS that a student adds to an enrollment (the Courses & Schedules screen).
            st.executeUpdate("CREATE TABLE IF NOT EXISTS subjects ("
                    + "subject_code VARCHAR(50) PRIMARY KEY, "
                    + "title VARCHAR(150) NOT NULL, "
                    + "units INT NOT NULL, "
                    + "department VARCHAR(50) NOT NULL, "
                    + "sched_day VARCHAR(50) NULL, "
                    + "sched_time VARCHAR(50) NULL, "
                    + "room VARCHAR(50) NULL)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS students ("
                    + "student_id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "student_number VARCHAR(12) NOT NULL UNIQUE, "
                    + "first_name VARCHAR(100) NOT NULL, "
                    + "middle_name VARCHAR(100), "
                    + "last_name VARCHAR(100) NOT NULL, "
                    + "gender VARCHAR(20) NULL, "
                    + "address VARCHAR(255) NULL, "
                    + "personal_email VARCHAR(150) NOT NULL UNIQUE, "
                    + "univ_email VARCHAR(150) NOT NULL UNIQUE, "
                    + "course VARCHAR(150) NOT NULL, "
                    + "year_level VARCHAR(50) NOT NULL, "
                    + "status VARCHAR(50) NOT NULL DEFAULT 'Pending', "
                    + "date_registered DATE DEFAULT (CURRENT_DATE), "
                    + "FOREIGN KEY (course) REFERENCES courses(course_code) "
                    + "ON DELETE RESTRICT ON UPDATE CASCADE)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS userCreds ("
                    + "userID INT AUTO_INCREMENT PRIMARY KEY, "
                    + "student_id INT UNIQUE, "
                    + "userName VARCHAR(50) NOT NULL UNIQUE, "
                    + "password VARCHAR(255) NOT NULL, "
                    + "role VARCHAR(20) NOT NULL DEFAULT 'student', "
                    + "FOREIGN KEY (student_id) REFERENCES students(student_id) "
                    + "ON DELETE CASCADE ON UPDATE CASCADE)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS enrollments ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "student_number VARCHAR(12) NOT NULL, "
                    + "total_units INT NOT NULL, "
                    + "status VARCHAR(20) NOT NULL DEFAULT 'Pending', "
                    + "date_submitted DATE DEFAULT (CURRENT_DATE), "
                    + "date_enrolled DATE NULL, "
                    + "date_approved DATE NULL, "
                    + "deny_reason VARCHAR(255) NULL, "
                    + "FOREIGN KEY (student_number) REFERENCES students(student_number) "
                    + "ON DELETE RESTRICT ON UPDATE CASCADE)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS enrollment_subjects ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "enrollment_id INT NOT NULL, "
                    + "subject_code VARCHAR(50) NOT NULL, "
                    + "FOREIGN KEY (enrollment_id) REFERENCES enrollments(id) "
                    + "ON DELETE CASCADE ON UPDATE CASCADE, "
                    + "FOREIGN KEY (subject_code) REFERENCES subjects(subject_code) "
                    + "ON DELETE RESTRICT ON UPDATE CASCADE)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS payments ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "enrollment_id INT NOT NULL UNIQUE, "
                    + "amount_due DECIMAL(10,2) NOT NULL, "
                    + "payment_method VARCHAR(30) NOT NULL, "
                    + "amount_received DECIMAL(10,2) NOT NULL, "
                    + "change_amount DECIMAL(10,2) NOT NULL DEFAULT 0, "
                    + "date_paid DATE NOT NULL, "
                    + "received_by VARCHAR(50) NULL, "
                    + "FOREIGN KEY (enrollment_id) REFERENCES enrollments(id) "
                    + "ON DELETE CASCADE ON UPDATE CASCADE)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS activity_log ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "activity_type VARCHAR(20) NOT NULL, "
                    + "message VARCHAR(255) NOT NULL, "
                    + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                    + "INDEX idx_activity_created (created_at))");
        }
    }

    /** Brings a database created by an OLDER version of this program up to date. */
    private static void migrate(Connection conn) throws SQLException {

        // students: gender + address (the enroll form collects them)
        if (!columnExists(conn, "students", "gender")) {
            run(conn, "ALTER TABLE students ADD COLUMN gender VARCHAR(20) NULL AFTER last_name");
        }
        if (!columnExists(conn, "students", "address")) {
            run(conn, "ALTER TABLE students ADD COLUMN address VARCHAR(255) NULL AFTER gender");
        }

        // university emails must follow lastname.firstname.studentnumber@reyuniversity.edu.ph
        run(conn, "UPDATE students SET univ_email = CONCAT("
                + "LOWER(REGEXP_REPLACE(last_name, '[^A-Za-z0-9]', '')), '.', "
                + "LOWER(REGEXP_REPLACE(first_name, '[^A-Za-z0-9]', '')), '.', "
                + "LOWER(student_number), '@reyuniversity.edu.ph') "
                + "WHERE univ_email NOT LIKE '%@reyuniversity.edu.ph'");

        // enrollments: reason shown when an application is denied
        if (!columnExists(conn, "enrollments", "deny_reason")) {
            run(conn, "ALTER TABLE enrollments ADD COLUMN deny_reason VARCHAR(255) NULL");
        }

        // enrollment_subjects used to point at "courses" (programs) - it must point at "subjects"
        if (columnExists(conn, "enrollment_subjects", "course_code")) {
            // subjects must be seeded before orphan rows can be judged
            seedSubjects(conn);

            List<String> fkNames = new ArrayList<String>();
            String fkSql = "SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE "
                    + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'enrollment_subjects' "
                    + "AND COLUMN_NAME = 'course_code' AND REFERENCED_TABLE_NAME IS NOT NULL";
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(fkSql)) {
                while (rs.next()) {
                    fkNames.add(rs.getString(1));
                }
            }
            for (String fk : fkNames) {
                run(conn, "ALTER TABLE enrollment_subjects DROP FOREIGN KEY `" + fk + "`");
            }
            run(conn, "DELETE FROM enrollment_subjects WHERE course_code NOT IN (SELECT subject_code FROM subjects)");
            run(conn, "ALTER TABLE enrollment_subjects CHANGE course_code subject_code VARCHAR(50) NOT NULL");
            run(conn, "ALTER TABLE enrollment_subjects ADD CONSTRAINT fk_es_subject "
                    + "FOREIGN KEY (subject_code) REFERENCES subjects(subject_code) "
                    + "ON DELETE RESTRICT ON UPDATE CASCADE");
        }
    }

    // =================================================================
    // SEED DATA (only inserts what is missing)
    // =================================================================

    private static void seed(Connection conn) throws SQLException {
        seedPrograms(conn);
        seedSubjects(conn);
        seedStaff(conn);
    }

    private static void seedPrograms(Connection conn) throws SQLException {
        String sql = "INSERT IGNORE INTO courses(course_code, course_name, description, quota, units) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            Object[][] programs = {
                    { "BSIT", "Bachelor of Science in Information Technology",
                            "Focuses on information technology, systems, networking, and technology management.", 430, 19 },
                    { "BSCS", "Bachelor of Science in Computer Science",
                            "Focuses on computer science, programming, algorithms, and software development.", 400, 21 } };
            for (Object[] p : programs) {
                ps.setString(1, (String) p[0]);
                ps.setString(2, (String) p[1]);
                ps.setString(3, (String) p[2]);
                ps.setInt(4, (Integer) p[3]);
                ps.setInt(5, (Integer) p[4]);
                ps.executeUpdate();
            }
        }
    }

    private static void seedSubjects(Connection conn) throws SQLException {
        String sql = "INSERT IGNORE INTO subjects(subject_code, title, units, department, sched_day, sched_time, room) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Object[][] subjects = {
                { "CCS101", "Introduction to Computing", 3, "CS", "Mon / Wed", "8:00 - 9:30 AM", "Room 101" },
                { "IT102", "Web Development", 3, "IT", "Tue / Thu", "10:00 - 11:30 AM", "Lab 2" },
                { "MATH101", "College Algebra", 3, "Math", "Mon / Wed", "1:00 - 2:30 PM", "Room 204" },
                { "ENG101", "English for Academic Purposes", 3, "English", "Tue / Thu", "1:00 - 2:30 PM", "Room 110" },
                { "PE101", "Physical Education", 2, "PE", "Fri", "8:00 - 10:00 AM", "Gym" },
                { "CCS102", "Computer Programming 1", 3, "CS", null, null, null },
                { "IT103", "Networking Fundamentals", 3, "IT", null, null, null },
                { "MATH102", "Trigonometry", 3, "Math", null, null, null },
                { "ENG102", "Purposive Communication", 3, "English", null, null, null },
                { "PE102", "Physical Education 2", 2, "PE", null, null, null },
                { "CCS103", "Discrete Mathematics", 3, "CS", null, null, null },
                { "IT104", "Database Management", 3, "IT", null, null, null } };
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] s : subjects) {
                ps.setString(1, (String) s[0]);
                ps.setString(2, (String) s[1]);
                ps.setInt(3, (Integer) s[2]);
                ps.setString(4, (String) s[3]);
                ps.setString(5, (String) s[4]);
                ps.setString(6, (String) s[5]);
                ps.setString(7, (String) s[6]);
                ps.executeUpdate();
            }
        }
    }

    private static void seedStaff(Connection conn) throws SQLException {
        String sql = "INSERT IGNORE INTO userCreds(userName, password, role) VALUES (?, ?, ?)";
        String[][] staff = {
                { "root", "$2a$10$oUEqQQozPH3wk/gqsHqtLe6LFFmpMwqY2ZeTLDGLDsdxs1udYEdiS", "admin" },
                { "cashier01", "$2a$10$IUX78sKkkJA6.GLNKKpiW.2Tkr5GTAVc.X5e.du7dSmvdhsMKfYy2", "cashier" },
                { "registrar01", "$2a$10$VJsHXuaVbgYBtAnFNBqXq..Sa6RgG8ossiYrliRkKRaZ22xtZXjE.", "registrar" } };
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String[] s : staff) {
                ps.setString(1, s[0]);
                ps.setString(2, s[1]);
                ps.setString(3, s[2]);
                ps.executeUpdate();
            }
        }
    }

    // =================================================================
    // HELPERS
    // =================================================================

    private static void run(Connection conn, String sql) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
        }
    }

    private static boolean columnExists(Connection conn, String table, String column) throws SQLException {
        String sql = "SELECT COUNT(*) FROM information_schema.COLUMNS "
                + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, table);
            ps.setString(2, column);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /** Run this class directly to create / check the database without opening the app. */
    public static void main(String[] args) {
        try (Connection c = getConnection()) {
            System.out.println("OK - connected to " + c.getCatalog());
        } catch (SQLException e) {
            reportOnce(e);
        }
    }
}
