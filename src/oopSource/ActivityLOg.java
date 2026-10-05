package oopSource;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * "Recent activity" feed shown on the dashboard.
 * Any frame records an action with:   ActivityLog.log(ActivityLog.Type.PAYMENT, "Maria paid ...");
 *
 * ---- DATABASE HOOK ------------------------------------------------------
 * Today the entries live in memory. To use MySQL, keep these two method
 * signatures and change only their bodies:
 *
 *   CREATE TABLE IF NOT EXISTS activity_log (
 *       id INT AUTO_INCREMENT PRIMARY KEY,
 *       activity_type VARCHAR(20) NOT NULL,
 *       message VARCHAR(255) NOT NULL,
 *       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
 *
 *   log():    INSERT INTO activity_log(activity_type, message) VALUES (?, ?)
 *   recent(): SELECT activity_type, message, created_at FROM activity_log
 *             ORDER BY created_at DESC LIMIT ?
 * -------------------------------------------------------------------------
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

	private static final int MAX_KEPT = 200;
	private static final LinkedList<Entry> ENTRIES = new LinkedList<Entry>(); // newest first

	static {
		// Placeholder samples so the dashboard is not empty. Delete once MySQL is used.
		seed(Type.ENROLLMENT, "Maria Santos enrolled in 7 subjects", 42);
		seed(Type.PAYMENT, "Anne Smith paid tuition (Cash)", 95);
		seed(Type.STUDENT, "John Doe's record was updated", 180);
	}

	private ActivityLOg() {
	}

	private static void seed(Type type, String message, int minutesAgo) {
		ENTRIES.addLast(new Entry(type, message, new Date(System.currentTimeMillis() - minutesAgo * 60000L)));
	}

	/** Records something that just happened. */
	public static void log(Type type, String message) {
		// TODO (database): INSERT INTO activity_log ...
		ENTRIES.addFirst(new Entry(type, message, new Date()));
		while (ENTRIES.size() > MAX_KEPT) {
			ENTRIES.removeLast();
		}
	}

	/** Newest first, at most 'limit' entries. */
	public static List<Entry> recent(int limit) {
		// TODO (database): SELECT ... ORDER BY created_at DESC LIMIT ?
		return new ArrayList<Entry>(ENTRIES.subList(0, Math.min(limit, ENTRIES.size())));
	}
}
