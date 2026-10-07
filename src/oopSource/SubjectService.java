package oopSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

/** The subjects shown on the Courses & Schedules screen (table "subjects"). */
public final class SubjectService {

	private SubjectService() {
	}

	/** Rows of {code, title, units, department}. */
	public static List<Object[]> all() {

		List<Object[]> rows = new ArrayList<Object[]>();
		String sql = "SELECT subject_code, title, units, department FROM subjects ORDER BY subject_code";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			while (rs.next()) {
				rows.add(new Object[] {
						rs.getString("subject_code"),
						rs.getString("title"),
						rs.getInt("units"),
						rs.getString("department") });
			}

		} catch (SQLException e) {
			DBConnection.reportOnce(e);
		}
		return rows;
	}

	/** Rows of {code, day, time, room} for subjects that have a schedule. */
	public static List<Object[]> schedules() {

		List<Object[]> rows = new ArrayList<Object[]>();
		String sql = "SELECT subject_code, sched_day, sched_time, room FROM subjects "
				+ "WHERE sched_day IS NOT NULL ORDER BY subject_code";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			while (rs.next()) {
				rows.add(new Object[] {
						rs.getString("subject_code"),
						rs.getString("sched_day"),
						rs.getString("sched_time"),
						rs.getString("room") });
			}

		} catch (SQLException e) {
			DBConnection.reportOnce(e);
		}
		return rows;
	}

	/** Maximum units one enrollment may have for a program (courses.units); 0 = unknown / no limit. */
	public static int unitLimit(String program) {

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement("SELECT units FROM courses WHERE course_code = ?")) {

			pst.setString(1, program);
			try (ResultSet rs = pst.executeQuery()) {
				return rs.next() ? rs.getInt(1) : 0;
			}

		} catch (SQLException e) {
			DBConnection.reportOnce(e);
			return 0;
		}
	}

	/** Deletes a subject. Subjects already used by an enrollment cannot be deleted. */
	public static boolean delete(String code) {

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement("DELETE FROM subjects WHERE subject_code = ?")) {

			pst.setString(1, code);
			return pst.executeUpdate() > 0;

		} catch (SQLException e) {

			String msg = e.getErrorCode() == 1451
					? code + " is part of existing enrollments and cannot be deleted."
					: "Failed to delete the subject:\n" + e.getMessage();
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, msg, "Cannot Delete", JOptionPane.WARNING_MESSAGE);
			return false;
		}
	}
}
