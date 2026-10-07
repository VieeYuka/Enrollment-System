package oopSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * Everything the screens need from the "students" table.
 *
 * Every method talks to MySQL directly (no in-memory copy), so all screens always
 * see the same, current data.
 */
public final class StudentService {

	/** Drop-down choices. */
	public static final String[] YEAR_LEVELS = {
			"1st Year", "2nd Year", "3rd Year", "4th Year"
	};

	public static final String[] STATUSES = {
			EnrollmentService.PENDING,
			EnrollmentService.ENROLLED
	};

	private static final String SELECT_STUDENT =
			"SELECT s.student_number, u.userName, "
			+ "s.first_name, s.middle_name, s.last_name, s.univ_email, "
			+ "s.course, s.year_level, s.status, s.gender, s.address "
			+ "FROM students s "
			+ "LEFT JOIN userCreds u ON s.student_id = u.student_id ";

	public static final String EMAIL_DOMAIN = "@reyuniversity.edu.ph";

	private StudentService() {
	}

	/** lastname.firstname.studentnumber@reyuniversity.edu.ph  (lower case, letters/digits only in names). */
	public static String univEmail(String lastName, String firstName, String studentNumber) {
		return clean(lastName) + "." + clean(firstName) + "." + studentNumber.toLowerCase() + EMAIL_DOMAIN;
	}

	private static String clean(String name) {
		if (name == null) {
			return "";
		}
		// "María" -> "maria": drop accent marks, then keep only letters and digits
		String plain = java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD).replaceAll("\\p{M}", "");
		return plain.toLowerCase().replaceAll("[^a-z0-9]", "");
	}

	/** Get program codes (BSIT, BSCS ...) from the courses table. */
	public static String[] getCourseID() {

		ArrayList<String> courses = new ArrayList<>();
		String sql = "SELECT course_code FROM courses ORDER BY course_name";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			while (rs.next()) {
				courses.add(rs.getString("course_code"));
			}

		} catch (SQLException e) {
			DBConnection.reportOnce(e);
		}

		return courses.toArray(new String[0]);
	}

	/** One row of the students table. */
	public static class StudentRecord {

		public String studentId;     // student_number
		public String username;
		public String firstName;
		public String middleName;
		public String lastName;
		public String email;         // university email
		public String course;
		public String yearLevel;
		public String status;        // Pending / Enrolled
		public String gender;
		public String address;

		public StudentRecord(
				String studentId,
				String username,
				String firstName,
				String middleName,
				String lastName,
				String email,
				String course,
				String yearLevel,
				String status) {

			this.studentId = studentId;
			this.username = username;
			this.firstName = firstName;
			this.middleName = middleName;
			this.lastName = lastName;
			this.email = email;
			this.course = course;
			this.yearLevel = yearLevel;
			this.status = status;
		}

		/** Kevin Thomas Bondoc -> Kevin T. Bondoc */
		public String fullName() {

			String middleInitial = "";

			if (middleName != null && !middleName.trim().isEmpty()) {
				middleInitial = " " + middleName.trim().charAt(0) + ".";
			}

			return firstName + middleInitial + " " + lastName;
		}

		public StudentRecord copy() {

			StudentRecord c = new StudentRecord(
					studentId, username, firstName, middleName, lastName,
					email, course, yearLevel, status);
			c.gender = gender;
			c.address = address;
			return c;
		}
	}

	private static StudentRecord map(ResultSet rs) throws SQLException {
		StudentRecord s = new StudentRecord(
				rs.getString("student_number"),
				rs.getString("userName"),
				rs.getString("first_name"),
				rs.getString("middle_name"),
				rs.getString("last_name"),
				rs.getString("univ_email"),
				rs.getString("course"),
				rs.getString("year_level"),
				rs.getString("status"));
		s.gender = rs.getString("gender");
		s.address = rs.getString("address");
		return s;
	}

	/**
	 * Enrolled students matching the search text. Every word typed must match the
	 * student number, a name, the course or the email (any order).
	 * Empty query = every enrolled student.
	 */
	public static List<StudentRecord> search(String query) {

		List<StudentRecord> result = new ArrayList<>();
		List<String> words = DBConnection.words(query);

		StringBuilder sql = new StringBuilder(SELECT_STUDENT);
		sql.append("WHERE s.status = 'Enrolled' ");
		for (int i = 0; i < words.size(); i++) {
			sql.append("AND (s.student_number LIKE ? OR s.first_name LIKE ? OR s.middle_name LIKE ? "
					+ "OR s.last_name LIKE ? OR s.course LIKE ? OR s.univ_email LIKE ?) ");
		}
		sql.append("ORDER BY s.student_id DESC");

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql.toString())) {

			int index = 1;
			for (String w : words) {
				String like = DBConnection.like(w);
				for (int k = 0; k < 6; k++) {
					pst.setString(index++, like);
				}
			}

			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					result.add(map(rs));
				}
			}

		} catch (SQLException e) {
			DBConnection.reportOnce(e);
		}

		return result;
	}

	/** Finds a student of ANY status (Pending or Enrolled) by student number. */
	public static StudentRecord findById(String studentId) {

		String sql = SELECT_STUDENT + "WHERE s.student_number = ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, studentId);

			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					return map(rs);
				}
			}

		} catch (SQLException e) {
			DBConnection.reportOnce(e);
		}

		return null;
	}

	public static StudentRecord findByUsername(String username) {

		String sql = SELECT_STUDENT + "WHERE u.userName = ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, username);

			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					return map(rs);
				}
			}

		} catch (SQLException e) {
			DBConnection.reportOnce(e);
		}

		return null;
	}

	/** The students table has UNIQUE emails - check before adding. */
	public static boolean emailExists(String email) {

		String sql = "SELECT 1 FROM students WHERE LOWER(univ_email) = LOWER(?) "
				+ "OR LOWER(personal_email) = LOWER(?) LIMIT 1";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, email);
			pst.setString(2, email);

			try (ResultSet rs = pst.executeQuery()) {
				return rs.next();
			}

		} catch (SQLException e) {
			DBConnection.reportOnce(e);
			return false;
		}
	}

	public static StudentRecord addEnrollee(
			String firstName,
			String middleName,
			String lastName,
			String email,
			String course,
			String yearLevel) {

		return addEnrollee(firstName, middleName, lastName, email, course, yearLevel, null, null);
	}

	/**
	 * Creates a new student with status "Pending".
	 *
	 * @return the saved student, or null when it could not be saved
	 *         (the person has already been told why).
	 */
	public static StudentRecord addEnrollee(
			String firstName,
			String middleName,
			String lastName,
			String email,
			String course,
			String yearLevel,
			String gender,
			String address) {

		String insert =
				"INSERT INTO students "
				+ "(student_number, first_name, middle_name, last_name, gender, address, "
				+ "personal_email, univ_email, course, year_level, status) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Pending')";

		try (Connection conn = DBConnection.getConnection()) {

			// Program quota
			if (programIsFull(conn, course)) {
				JOptionPane.showMessageDialog(null,
						"The " + course + " program has reached its quota and is not accepting new students.",
						"Program Full", JOptionPane.WARNING_MESSAGE);
				return null;
			}

			// Two people saving at the same moment may pick the same number - retry a few times.
			for (int attempt = 0; attempt < 5; attempt++) {

				String studentNumber = nextStudentNumber(conn);

				try (PreparedStatement pst = conn.prepareStatement(insert)) {

					pst.setString(1, studentNumber);
					pst.setString(2, firstName);
					pst.setString(3, middleName);
					pst.setString(4, lastName);
					pst.setString(5, gender);
					pst.setString(6, address);
					String univEmail = univEmail(lastName, firstName, studentNumber);
					pst.setString(7, email);          // personal email = what the person typed
					pst.setString(8, univEmail);      // university email = generated
					pst.setString(9, course);
					pst.setString(10, yearLevel);

					pst.executeUpdate();

					StudentRecord s = new StudentRecord(
							studentNumber, null, firstName, middleName, lastName,
							univEmail, course, yearLevel, EnrollmentService.PENDING);
					s.gender = gender;
					s.address = address;
					return s;

				} catch (SQLException e) {

					if (e.getErrorCode() == 1062) {
						String msg = String.valueOf(e.getMessage());
						if (msg.contains("student_number")) {
							continue; // number was taken a moment ago - try the next one
						}
						JOptionPane.showMessageDialog(null,
								"A student with this email is already registered.",
								"Duplicate Email", JOptionPane.WARNING_MESSAGE);
						return null;
					}
					throw e;
				}
			}

			JOptionPane.showMessageDialog(null,
					"Could not generate a free student number. Please try again.",
					"Database Error", JOptionPane.ERROR_MESSAGE);
			return null;

		} catch (SQLException e) {

			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Failed to save the student:\n" + e.getMessage(),
					"Database Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	/** Saves the edited fields of a student (and keeps the latest enrollment status in step). */
	public static boolean update(StudentRecord edited) {

		String sql =
				"UPDATE students SET first_name = ?, middle_name = ?, last_name = ?, "
				+ "univ_email = ?, course = ?, year_level = ?, status = ? "
				+ "WHERE student_number = ?";

		try (Connection conn = DBConnection.getConnection()) {

			conn.setAutoCommit(false);

			try (PreparedStatement pst = conn.prepareStatement(sql)) {

				pst.setString(1, edited.firstName);
				pst.setString(2, edited.middleName);
				pst.setString(3, edited.lastName);
				// the university email always follows the name + student number
				edited.email = univEmail(edited.lastName, edited.firstName, edited.studentId);
				pst.setString(4, edited.email);
				pst.setString(5, edited.course);
				pst.setString(6, edited.yearLevel);
				pst.setString(7, edited.status);
				pst.setString(8, edited.studentId);

				if (pst.executeUpdate() == 0) {
					conn.rollback();
					return false;
				}

				EnrollmentService.syncLatestStatus(conn, edited.studentId, edited.status);

				conn.commit();
				return true;

			} catch (SQLException e) {
				conn.rollback();
				throw e;
			}

		} catch (SQLException e) {

			if (e.getErrorCode() == 1062) {
				JOptionPane.showMessageDialog(null,
						"That email is already used by another student.",
						"Duplicate Email", JOptionPane.WARNING_MESSAGE);
			} else {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Failed to update the student:\n" + e.getMessage(),
						"Database Error", JOptionPane.ERROR_MESSAGE);
			}
			return false;
		}
	}

	/** Deletes the student together with their enrollments, subjects, payments and login account. */
	public static boolean delete(String studentId) {

		try (Connection conn = DBConnection.getConnection()) {

			conn.setAutoCommit(false);

			try {
				try (PreparedStatement pst = conn.prepareStatement(
						"DELETE FROM enrollments WHERE student_number = ?")) {
					pst.setString(1, studentId);
					pst.executeUpdate(); // enrollment_subjects + payments go with it (ON DELETE CASCADE)
				}

				int removed;
				try (PreparedStatement pst = conn.prepareStatement(
						"DELETE FROM students WHERE student_number = ?")) {
					pst.setString(1, studentId);
					removed = pst.executeUpdate(); // userCreds row goes with it (ON DELETE CASCADE)
				}

				conn.commit();
				return removed > 0;

			} catch (SQLException e) {
				conn.rollback();
				throw e;
			}

		} catch (SQLException e) {

			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Failed to delete the student:\n" + e.getMessage(),
					"Database Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	public static void updateStatus(String studentId, String status) {

		String sql = "UPDATE students SET status = ? WHERE student_number = ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, status);
			pst.setString(2, studentId);
			pst.executeUpdate();

		} catch (SQLException e) {
			DBConnection.reportOnce(e);
		}
	}

	/** Number of enrolled students (the same people the Students screen lists). */
	public static int count() {

		String sql = "SELECT COUNT(*) FROM students WHERE status = 'Enrolled'";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			if (rs.next()) {
				return rs.getInt(1);
			}

		} catch (SQLException e) {
			DBConnection.reportOnce(e);
		}

		return 0;
	}

	// =================================================================
	// HELPERS
	// =================================================================

	private static boolean programIsFull(Connection conn, String course) throws SQLException {

		String sql = "SELECT c.quota, (SELECT COUNT(*) FROM students s WHERE s.course = c.course_code) AS taken "
				+ "FROM courses c WHERE c.course_code = ?";

		try (PreparedStatement pst = conn.prepareStatement(sql)) {
			pst.setString(1, course);
			try (ResultSet rs = pst.executeQuery()) {
				return rs.next() && rs.getInt("taken") >= rs.getInt("quota");
			}
		}
	}

	/** Next free number of the form YYYY-0001 for the current year. */
	private static String nextStudentNumber(Connection conn) throws SQLException {

		String prefix = Year.now().getValue() + "-";

		String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING_INDEX(student_number, '-', -1) AS UNSIGNED)), 0) "
				+ "FROM students WHERE student_number LIKE ?";

		try (PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, prefix + "%");

			try (ResultSet rs = pst.executeQuery()) {
				int last = rs.next() ? rs.getInt(1) : 0;
				return String.format("%s%04d", prefix, last + 1);
			}
		}
	}
}
