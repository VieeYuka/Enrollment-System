package oopSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/";

    private static final String DATABASE = "enrollDB";

    private static final String USER = "root";

    private static final String PASSWORD = "SQLang@246"; // ur pass here


    public static Connection getConnection() {

        Connection conn = null;

        try {

            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("JDBC registered.");


            //connect to sqlServ
            conn = DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
            );

            System.out.println("Connected to MySQL server.");

            //create db if not existing
            Statement stmt = conn.createStatement();

            String createDatabase =
                "CREATE DATABASE IF NOT EXISTS " + DATABASE;

            stmt.executeUpdate(createDatabase);

            System.out.println(
                "Database " + DATABASE + " is ready."
            );

            stmt.close();


            //close server connnect
            conn.close();


            //connect to enrollDB
            conn = DriverManager.getConnection(
                URL + DATABASE,
                USER,
                PASSWORD
            );

            System.out.println(
                "Connected to " + DATABASE
            );

            //create all necessaryTables
            Statement tableStmt = conn.createStatement();

            String createTableCourses =
                    "CREATE TABLE IF NOT EXISTS courses ("
                  + "id INT AUTO_INCREMENT PRIMARY KEY, "
                  + "course_code VARCHAR(50) NOT NULL UNIQUE, "
                  + "course_name VARCHAR(150) NOT NULL UNIQUE, "
                  + "description VARCHAR(255) NOT NULL UNIQUE, "
                  + "quota INT NOT NULL, "
                  + "units INT NOT NULL"
                  + ");";

            tableStmt.executeUpdate(createTableCourses);

            String insertCourses =
                    "INSERT INTO courses(course_code, course_name, description, quota, units) "
                  + "VALUES (?, ?, ?, ?, ?), (?, ?, ?, ?, ?) "
                  + "ON DUPLICATE KEY UPDATE "
                  + "course_code = course_code, "
                  + "course_name = course_name, "
                  + "description = description, "
                  + "quota = quota, "
                  + "units = units";

            PreparedStatement pstate = conn.prepareStatement(insertCourses);

            pstate.setString(1, "BSIT");
            pstate.setString(2, "Bachelor of Science in Information Technology");
            pstate.setString(3, "Focuses on information technology, systems, networking, and technology management.");
            pstate.setInt(4, 430);
            pstate.setInt(5, 19);

            pstate.setString(6, "BSCS");
            pstate.setString(7, "Bachelor of Science in Computer Science");
            pstate.setString(8, "Focuses on computer science, programming, algorithms, and software development.");
            pstate.setInt(9, 400);
            pstate.setInt(10, 21);

            pstate.executeUpdate();
            pstate.close();

            String createTableStudents =
                    "CREATE TABLE IF NOT EXISTS students ("
                  + "student_id INT AUTO_INCREMENT PRIMARY KEY, "
                  + "student_number VARCHAR(12) NOT NULL UNIQUE, "
                  + "first_name VARCHAR(100) NOT NULL, "
                  + "last_name VARCHAR(100) NOT NULL, "
                  + "personal_email VARCHAR(150) NOT NULL UNIQUE, "
                  + "univ_email VARCHAR(150) NOT NULL UNIQUE, "
                  + "course VARCHAR(150) NOT NULL, "
                  + "year_level VARCHAR(50) NOT NULL, "
                  + "status VARCHAR(50) NOT NULL default 'Pending', "
                  + "date_registered DATE DEFAULT (CURRENT_DATE), "
                  + "FOREIGN KEY (course) REFERENCES courses(course_name) "
                  + "ON DELETE RESTRICT "
                  + "ON UPDATE CASCADE"
                  + ");";

            tableStmt.executeUpdate(createTableStudents);

           

            String createTableUserCreds =
                    "CREATE TABLE IF NOT EXISTS userCreds ("
                  + "userID INT AUTO_INCREMENT PRIMARY KEY, "
                  + "student_id INT UNIQUE, "
                  + "userName VARCHAR(50) NOT NULL UNIQUE, "
                  + "password VARCHAR(255) NOT NULL, "
                  + "role VARCHAR(20) NOT NULL default 'student', "
                  + "FOREIGN KEY (student_id) REFERENCES students(student_id) "
                  + "ON DELETE CASCADE "
                  + "ON UPDATE CASCADE"
                  + ");";

            tableStmt.executeUpdate(createTableUserCreds);

            String insertStaff =
                    "INSERT INTO userCreds(userName, password, role) "
                  + "VALUES (?, ?, ?), (?, ?, ?), (?, ?, ?) "
                  + "ON DUPLICATE KEY UPDATE "
                  + "userName = userName";

            pstate = conn.prepareStatement(insertStaff);

            pstate.setString(1, "root");
            pstate.setString(2, "$2a$10$oUEqQQozPH3wk/gqsHqtLe6LFFmpMwqY2ZeTLDGLDsdxs1udYEdiS");
            pstate.setString(3, "admin");

            pstate.setString(4, "cashier01");
            pstate.setString(5, "$2a$10$IUX78sKkkJA6.GLNKKpiW.2Tkr5GTAVc.X5e.du7dSmvdhsMKfYy2");
            pstate.setString(6, "cashier");

            pstate.setString(7, "registrar01");
            pstate.setString(8, "$2a$10$VJsHXuaVbgYBtAnFNBqXq..Sa6RgG8ossiYrliRkKRaZ22xtZXjE.");
            pstate.setString(9, "registrar");

            pstate.executeUpdate();
            pstate.close();

            
            pstate.close();
            tableStmt.close();


        } catch (ClassNotFoundException e) {

            JOptionPane.showMessageDialog(
                null,
                "MySQL Driver not found: " + e.getMessage(),
                "Driver Error",
                JOptionPane.ERROR_MESSAGE
            );

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                null,
                "Database Error: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }


        return conn;
    }
    
    public static void main(String[] args) {
    	Connection conn = DBConnection.getConnection();
    	
    }
}