package oopSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * Enrollment requests and their status (Pending -> Enrolled).
 *
 * Tables used: students, subjects, enrollments, enrollment_subjects.
 */
public final class EnrollmentService {

	public static final String PENDING = "Pending";
	public static final String ENROLLED = "Enrolled";
	public static final String DENIED = "Denied";

	/** Pseudo-status for search(): applications still needing a decision (Pending + Denied). */
	public static final String OPEN = "Open";

	/** Full name as "First M. Last" (middle initial only, no double spaces). */
	static final String NAME_SQL =
			"CONCAT_WS(' ', s.first_name, "
			+ "CASE WHEN s.middle_name IS NULL OR s.middle_name = '' THEN NULL "
			+ "ELSE CONCAT(LEFT(s.middle_name, 1), '.') END, s.last_name)";

	/** One enrollment application. */
	public static class Enrollment {
		public final String studentKey;
		public final String name;
		public final String course;
		public final String yearLevel;
		public final int units;
		public final List<Object[]> subjects;
		public String status;

		Enrollment(String studentKey, String name, String course,
				String yearLevel, int units,
				List<Object[]> subjects, String status) {

			this.studentKey = studentKey;
			this.name = name;
			this.course = course;
			this.yearLevel = yearLevel;
			this.units = units;
			this.subjects = subjects;
			this.status = status;
		}

		public int subjectCount() {
			return subjects.size();
		}
	}

	private EnrollmentService() {
	}

	/**
	 * Saves a new enrollment (status Pending) with its subjects in one transaction.
	 *
	 * @return the saved enrollment, or null when it could not be saved
	 *         (the person has already been told why).
	 */
	public static Enrollment submit(String studentKey, String name,
			String course, String yearLevel, List<Object[]> subjects) {

		if (subjects == null || subjects.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please add at least one subject.",
					"No subjects selected", JOptionPane.WARNING_MESSAGE);
			return null;
		}

		try (Connection conn = DBConnection.getConnection()) {

			// The student must exist in the database
			String program = studentCourse(conn, studentKey);
			if (program == null) {
				JOptionPane.showMessageDialog(null,
						"No student record was found for \"" + studentKey + "\".\n"
								+ "Students must be registered (Enroll now) before choosing subjects.",
						"Student Not Found", JOptionPane.WARNING_MESSAGE);
				return null;
			}

			// One open application at a time
			if (PENDING.equals(getStatusOrNull(conn, studentKey))) {
				JOptionPane.showMessageDialog(null,
						"This student already has an enrollment waiting for approval.",
						"Already Submitted", JOptionPane.INFORMATION_MESSAGE);
				return null;
			}

			// Units are taken from the database, not from what the screen sent
			List<Object[]> saved = new ArrayList<Object[]>();
			int units = 0;
			String findSubject = "SELECT subject_code, title, units FROM subjects WHERE subject_code = ?";
			try (PreparedStatement pst = conn.prepareStatement(findSubject)) {
				List<String> seen = new ArrayList<String>();
				for (Object[] row : subjects) {
					String code = String.valueOf(row[0]);
					if (seen.contains(code)) {
						continue;
					}
					seen.add(code);
					pst.setString(1, code);
					try (ResultSet rs = pst.executeQuery()) {
						if (!rs.next()) {
							JOptionPane.showMessageDialog(null,
									"Subject " + code + " no longer exists. Please review your list.",
									"Subject Not Found", JOptionPane.WARNING_MESSAGE);
							return null;
						}
						units += rs.getInt("units");
						saved.add(new Object[] { rs.getString("subject_code"),
								rs.getString("title"), rs.getInt("units"), "" });
					}
				}
			}

			int limit = SubjectService.unitLimit(program);
			if (limit > 0 && units != limit) {
				JOptionPane.showMessageDialog(null,
						"The " + program + " program requires exactly " + limit + " units per enrollment.\n"
								+ "You selected " + units + " units.",
						"Units Must Match", JOptionPane.WARNING_MESSAGE);
				return null;
			}

			conn.setAutoCommit(false);

			try {
				int enrollmentId;

				try (PreparedStatement pst = conn.prepareStatement(
						"INSERT INTO enrollments (student_number, total_units, status, date_submitted) "
								+ "VALUES (?, ?, ?, CURRENT_DATE)", Statement.RETURN_GENERATED_KEYS)) {

					pst.setString(1, studentKey);
					pst.setInt(2, units);
					pst.setString(3, PENDING);
					pst.executeUpdate();

					try (ResultSet rs = pst.getGeneratedKeys()) {
						if (!rs.next()) {
							conn.rollback();
							return null;
						}
						enrollmentId = rs.getInt(1);
					}
				}

				try (PreparedStatement pst = conn.prepareStatement(
						"INSERT INTO enrollment_subjects (enrollment_id, subject_code) VALUES (?, ?)")) {
					for (Object[] row : saved) {
						pst.setInt(1, enrollmentId);
						pst.setString(2, String.valueOf(row[0]));
						pst.addBatch();
					}
					pst.executeBatch();
				}

				try (PreparedStatement pst = conn.prepareStatement(
						"UPDATE students SET status = ? WHERE student_number = ?")) {
					pst.setString(1, PENDING);
					pst.setString(2, studentKey);
					pst.executeUpdate();
				}

				conn.commit();

			} catch (SQLException e) {
				conn.rollback();
				throw e;
			}

			ActivityLOg.log(ActivityLOg.Type.ENROLLMENT,
					name + " enrolled in " + saved.size() + " subjects (" + units + " units)");

			return new Enrollment(studentKey, name, course, yearLevel, units, saved, PENDING);

		} catch (SQLException e) {

			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Failed to save enrollment:\n" + e.getMessage(),
					"Database Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	/** Searches applications. status = "All Statuses", "Pending" or "Enrolled". */
	public static List<Enrollment> search(String query, String status) {

		List<Enrollment> result = new ArrayList<Enrollment>();
		List<String> words = DBConnection.words(query);
		boolean anyStatus = status == null || "All Statuses".equals(status);

		// A student with no enrollment record yet (added by staff) counts as an application too.
		StringBuilder sql = new StringBuilder(
				"SELECT s.student_number, " + NAME_SQL + " AS student_name, "
				+ "s.course, s.year_level, COALESCE(e.total_units, 0) AS total_units, "
				+ "COALESCE(e.status, s.status) AS app_status "
				+ "FROM students s LEFT JOIN enrollments e ON e.id = (SELECT MAX(e2.id) FROM enrollments e2 WHERE e2.student_number = s.student_number) "
				+ "WHERE 1 = 1 ");
		for (int i = 0; i < words.size(); i++) {
			sql.append("AND (s.student_number LIKE ? OR s.first_name LIKE ? "
					+ "OR s.middle_name LIKE ? OR s.last_name LIKE ?) ");
		}
		boolean open = OPEN.equals(status);
		if (open) {
			sql.append("AND COALESCE(e.status, s.status) IN ('Pending', 'Denied') ");
		} else if (!anyStatus) {
			sql.append("AND COALESCE(e.status, s.status) = ? ");
		}
		sql.append("ORDER BY s.student_id DESC");

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql.toString())) {

			int index = 1;
			for (String w : words) {
				String like = DBConnection.like(w);
				for (int k = 0; k < 4; k++) {
					pst.setString(index++, like);
				}
			}
			if (!anyStatus && !open) {
				pst.setString(index, status);
			}

			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					result.add(new Enrollment(
							rs.getString("student_number"),
							rs.getString("student_name"),
							rs.getString("course"),
							rs.getString("year_level"),
							rs.getInt("total_units"),
							new ArrayList<Object[]>(),
							rs.getString("app_status")));
				}
			}

		} catch (SQLException e) {
			DBConnection.reportOnce(e);
		}

		return result;
	}

	/** One student's latest enrollment application, including its subjects. */
	public static Enrollment find(String studentKey) {

		String sql =
				"SELECT e.id AS enrollment_id, s.student_number, " + NAME_SQL + " AS student_name, "
				+ "s.course, s.year_level, COALESCE(e.total_units, 0) AS total_units, "
				+ "COALESCE(e.status, s.status) AS app_status "
				+ "FROM students s LEFT JOIN enrollments e ON e.id = (SELECT MAX(e2.id) FROM enrollments e2 WHERE e2.student_number = s.student_number) "
				+ "WHERE s.student_number = ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, studentKey);

			try (ResultSet rs = pst.executeQuery()) {

				if (!rs.next()) {
					return null;
				}

				return new Enrollment(
						rs.getString("student_number"),
						rs.getString("student_name"),
						rs.getString("course"),
						rs.getString("year_level"),
						rs.getInt("total_units"),
						getSubjects(conn, rs.getInt("enrollment_id")),
						rs.getString("app_status"));
			}

		} catch (SQLException e) {
			DBConnection.reportOnce(e);
			return null;
		}
	}

	private static List<Object[]> getSubjects(Connection conn, int enrollmentId) throws SQLException {

		List<Object[]> subjects = new ArrayList<Object[]>();

		String sql =
				"SELECT sub.subject_code, sub.title, sub.units "
				+ "FROM enrollment_subjects es "
				+ "JOIN subjects sub ON sub.subject_code = es.subject_code "
				+ "WHERE es.enrollment_id = ? ORDER BY es.id";

		try (PreparedStatement pst = conn.prepareStatement(sql)) {
			pst.setInt(1, enrollmentId);
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					subjects.add(new Object[] {
							rs.getString("subject_code"),
							rs.getString("title"),
							rs.getInt("units"),
							"" });
				}
			}
		}
		return subjects;
	}

	/** Approves the student's Pending enrollment: enrollment AND student become Enrolled. */
	public static boolean approve(String studentKey) {

		String sql =
				"UPDATE enrollments SET status = ?, date_enrolled = CURRENT_DATE, date_approved = CURRENT_DATE, "
				+ "deny_reason = NULL "
				+ "WHERE id = (SELECT latest FROM (SELECT MAX(id) AS latest FROM enrollments "
				+ "WHERE student_number = ?) AS t) AND status = 'Pending'";

		try (Connection conn = DBConnection.getConnection()) {

			conn.setAutoCommit(false);

			try {
				int affected;
				try (PreparedStatement pst = conn.prepareStatement(sql)) {
					pst.setString(1, ENROLLED);
					pst.setString(2, studentKey);
					affected = pst.executeUpdate();
				}

				if (affected == 0) {
					// No enrollment record at all (student added by staff): approve the student itself
					boolean hasNone = getStatusOrNull(conn, studentKey) == null;
					if (!hasNone) {
						conn.rollback();
						return false;
					}
				}

				try (PreparedStatement pst = conn.prepareStatement(
						"UPDATE students SET status = ? WHERE student_number = ? AND status <> 'Denied'")) {
					pst.setString(1, ENROLLED);
					pst.setString(2, studentKey);
					if (pst.executeUpdate() == 0) {
						conn.rollback(); // denied (or unknown) students cannot be approved
						return false;
					}
				}

				conn.commit();

			} catch (SQLException e) {
				conn.rollback();
				throw e;
			}

			Enrollment enrollment = find(studentKey);
			if (enrollment != null) {
				ActivityLOg.log(ActivityLOg.Type.ENROLLMENT, enrollment.name + "'s application was approved");
			}
			return true;

		} catch (SQLException e) {

			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Failed to approve enrollment:\n" + e.getMessage(),
					"Database Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	/**
	 * Denies the student's open application (latest enrollment, or the student record itself
	 * when no subjects were chosen yet). The student may apply again later.
	 *
	 * @param reason optional explanation (may be empty)
	 */
	public static boolean deny(String studentKey, String reason) {

		String sql =
				"UPDATE enrollments SET status = 'Denied', deny_reason = ?, date_enrolled = NULL, date_approved = NULL "
				+ "WHERE id = (SELECT latest FROM (SELECT MAX(id) AS latest FROM enrollments "
				+ "WHERE student_number = ?) AS t) AND status = 'Pending'";

		String cleanReason = reason == null ? "" : reason.trim();
		if (cleanReason.length() > 255) {
			cleanReason = cleanReason.substring(0, 255);
		}

		try (Connection conn = DBConnection.getConnection()) {

			conn.setAutoCommit(false);

			try {
				int affected;
				try (PreparedStatement pst = conn.prepareStatement(sql)) {
					pst.setString(1, cleanReason.isEmpty() ? null : cleanReason);
					pst.setString(2, studentKey);
					affected = pst.executeUpdate();
				}

				if (affected == 0 && getStatusOrNull(conn, studentKey) != null) {
					conn.rollback(); // latest enrollment is not Pending (already decided)
					return false;
				}

				try (PreparedStatement pst = conn.prepareStatement(
						"UPDATE students SET status = ? WHERE student_number = ? AND status = 'Pending'")) {
					pst.setString(1, DENIED);
					pst.setString(2, studentKey);
					if (pst.executeUpdate() == 0 && affected == 0) {
						conn.rollback();
						return false;
					}
				}

				conn.commit();

			} catch (SQLException e) {
				conn.rollback();
				throw e;
			}

			Enrollment enrollment = find(studentKey);
			if (enrollment != null) {
				ActivityLOg.log(ActivityLOg.Type.ENROLLMENT, enrollment.name + "'s application was denied"
						+ (cleanReason.isEmpty() ? "" : ": " + cleanReason));
			}
			return true;

		} catch (SQLException e) {

			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Failed to deny enrollment:\n" + e.getMessage(),
					"Database Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	/** Latest enrollment status of a student (Pending when they have none). */
	public static String getStatus(String studentKey) {

		try (Connection conn = DBConnection.getConnection()) {
			String status = getStatusOrNull(conn, studentKey);
			return status == null ? PENDING : status;
		} catch (SQLException e) {
			DBConnection.reportOnce(e);
			return PENDING;
		}
	}

	/** Changes the status of the latest enrollment (and the student). */
	public static void setStatus(String studentKey, String status) {

		try (Connection conn = DBConnection.getConnection()) {

			conn.setAutoCommit(false);

			try {
				syncLatestStatus(conn, studentKey, status);

				try (PreparedStatement pst = conn.prepareStatement(
						"UPDATE students SET status = ? WHERE student_number = ?")) {
					pst.setString(1, status);
					pst.setString(2, studentKey);
					pst.executeUpdate();
				}

				conn.commit();

			} catch (SQLException e) {
				conn.rollback();
				throw e;
			}

			Enrollment enrollment = find(studentKey);
			if (enrollment != null) {
				ActivityLOg.log(ActivityLOg.Type.ENROLLMENT, enrollment.name + " is now " + status);
			}

		} catch (SQLException e) {

			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Failed to update enrollment status:\n" + e.getMessage(),
					"Database Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Sets the status (and its dates) of the student's LATEST enrollment, using the
	 * caller's connection so it can be part of a bigger transaction.
	 */
	static void syncLatestStatus(Connection conn, String studentKey, String status) throws SQLException {

		String sql =
				"UPDATE enrollments SET status = ?, "
				+ "date_enrolled = CASE WHEN ? = 'Enrolled' THEN COALESCE(date_enrolled, CURRENT_DATE) ELSE NULL END, "
				+ "date_approved = CASE WHEN ? = 'Enrolled' THEN COALESCE(date_approved, CURRENT_DATE) ELSE NULL END "
				+ "WHERE id = (SELECT latest FROM (SELECT MAX(id) AS latest FROM enrollments "
				+ "WHERE student_number = ?) AS t)";

		try (PreparedStatement pst = conn.prepareStatement(sql)) {
			pst.setString(1, status);
			pst.setString(2, status);
			pst.setString(3, status);
			pst.setString(4, studentKey);
			pst.executeUpdate();
		}
	}

	private static String getStatusOrNull(Connection conn, String studentKey) throws SQLException {

		String sql = "SELECT status FROM enrollments WHERE student_number = ? ORDER BY id DESC LIMIT 1";

		try (PreparedStatement pst = conn.prepareStatement(sql)) {
			pst.setString(1, studentKey);
			try (ResultSet rs = pst.executeQuery()) {
				return rs.next() ? rs.getString(1) : null;
			}
		}
	}

	/** The program (course code) of the student, or null when there is no such student. */
	private static String studentCourse(Connection conn, String studentKey) throws SQLException {

		try (PreparedStatement pst = conn.prepareStatement(
				"SELECT course FROM students WHERE student_number = ?")) {
			pst.setString(1, studentKey);
			try (ResultSet rs = pst.executeQuery()) {
				return rs.next() ? rs.getString(1) : null;
			}
		}
	}

	// =================================================================
	// DASHBOARD COUNTS
	// =================================================================

	public static int countByStatus(String status) {
		return count("SELECT COUNT(*) " + "FROM students s LEFT JOIN enrollments e ON e.id = (SELECT MAX(e2.id) FROM enrollments e2 WHERE e2.student_number = s.student_number) " + "WHERE COALESCE(e.status, s.status) = ?", status);
	}

	public static int countEnrolledToday() {
		return count("SELECT COUNT(*) FROM enrollments WHERE status = ? AND date_enrolled = CURRENT_DATE", ENROLLED);
	}

	public static int countApprovedToday() {
		return count("SELECT COUNT(*) FROM enrollments WHERE date_approved = CURRENT_DATE AND ? IS NOT NULL", ENROLLED);
	}

	private static int count(String sql, String param) {

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, param);

			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}

		} catch (SQLException e) {
			DBConnection.reportOnce(e);
		}

		return 0;
	}
}
