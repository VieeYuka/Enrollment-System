package oopSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * "Recent activity" feed shown on the dashboard, stored in the activity_log table.
 * Any frame records an action with:   ActivityLOg.log(ActivityLOg.Type.PAYMENT, "Maria paid ...");
 */
public final class ActivityLOg {

	public enum Type { ENROLLMENT, PAYMENT, STUDENT, DELETED, LOGIN }

	public static final class Entry {
		public final Type type;
		public final String message;
		public final Date time;

		Entry(Type type, String message, Date time) {
			this.type = type;
			this.message = message;
			this.time = time;
		}

		/** "just now", "5 min ago", "3 hr ago", "2 days ago" */
		public String timeAgo() {
			long minutes = (System.currentTimeMillis() - time.getTime()) / 60000;
			if (minutes < 1) return "just now";
			if (minutes < 60) return minutes + " min ago";
			if (minutes < 60 * 24) return (minutes / 60) + " hr ago";
			return (minutes / (60 * 24)) + " days ago";
		}
	}

	private ActivityLOg() {
	}

	/** Records something that just happened. A logging problem never stops the real action. */
	public static void log(Type type, String message) {

		String sql = "INSERT INTO activity_log (activity_type, message, created_at) VALUES (?, ?, ?)";

		if (message != null && message.length() > 255) {
			message = message.substring(0, 255);
		}

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, type.name());
			pst.setString(2, message);
			pst.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			pst.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** Newest first, at most 'limit' entries. */
	public static List<Entry> recent(int limit) {

		List<Entry> list = new ArrayList<Entry>();
		String sql = "SELECT activity_type, message, created_at FROM activity_log "
				+ "ORDER BY created_at DESC, id DESC LIMIT ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setInt(1, limit);

			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					Type type;
					try {
						type = Type.valueOf(rs.getString("activity_type"));
					} catch (IllegalArgumentException ex) {
						type = Type.STUDENT;
					}
					list.add(new Entry(type, rs.getString("message"), rs.getTimestamp("created_at")));
				}
			}

		} catch (SQLException e) {
			DBConnection.reportOnce(e);
		}

		return list;
	}
}
