package oopSource;

import javax.swing.JFrame;

/**
 * ONE place that decides what each role can see and where it lands after login.
 * To change who sees what, edit canSee() and landingFrame() only.
 *
 *   applicant  - no account, enrolled through "Enroll now"
 *   student    - has an account
 *   cashier    - Dashboard + Tuition & Payments
 *   registrar  - Students + Enrollment + Courses  (not specified yet - adjust here)
 *   admin      - everything
 */
public final class Roles {

	public static final String ADMIN = "admin";
	public static final String CASHIER = "cashier";
	public static final String REGISTRAR = "registrar";
	public static final String STUDENT = "student";
	public static final String APPLICANT = "applicant";

	// Sidebar menu names (used as keys by Sidebar)
	public static final String NAV_DASHBOARD = "Dashboard";
	public static final String NAV_STUDENTS = "Students";
	public static final String NAV_ENROLLMENT = "Enrollment";
	public static final String NAV_COURSES = "Courses & Schedules";
	public static final String NAV_TUITION = "Tuition & Payments";

	/** Order in which the menu items appear in the sidebar. */
	public static final String[] ALL_NAV = {
			NAV_DASHBOARD, NAV_STUDENTS, NAV_ENROLLMENT, NAV_COURSES, NAV_TUITION };

	private Roles() {
	}

	/** Lower-cases / trims so "Admin", "admin " and null are all handled. */
	public static String normalize(String role) {
		return role == null ? "" : role.trim().toLowerCase();
	}

	/** Is this menu item visible for the role? */
	public static boolean canSee(String role, String nav) {
		switch (normalize(role)) {
		case ADMIN:
			return true;
		case CASHIER:
			return nav.equals(NAV_DASHBOARD) || nav.equals(NAV_TUITION);
		case REGISTRAR:
			return nav.equals(NAV_STUDENTS) || nav.equals(NAV_ENROLLMENT) || nav.equals(NAV_COURSES);
		case STUDENT:
		case APPLICANT:
			return nav.equals(NAV_COURSES);
		default:
			return false;
		}
	}

	/** Admin and registrar may delete courses; students only pick subjects. */
	public static boolean canManageCourses(String role) {
		return false;
//		String r = normalize(role);
//		return r.equals(ADMIN) || r.equals(REGISTRAR);
	}

	/**
	 * true  -> after "Confirm Enrollment" the person goes to the Exit screen and the system closes.
	 * false -> staff go back to their own landing screen instead.
	 * (Change this if you want staff to see the Exit screen too.)
	 */
	public static boolean endsSessionAfterEnrollment(String role) {
		String r = normalize(role);
		return r.equals(STUDENT) || r.equals(APPLICANT);
	}

	/** First screen shown after login. */
	public static JFrame landingFrame(String user, String role) {
		switch (normalize(role)) {
		case ADMIN:
		case CASHIER:
			return new DashboardFrame(user, role);
		case REGISTRAR:
			return new StudentsFrame(user, role);
		default: // student / applicant
			return new CoursesFrame(user, role);
		}
	}

	/** Text shown under the username in the sidebar. */
	public static String displayName(String role) {
		switch (normalize(role)) {
		case ADMIN:
			return "System Administrator";
		case CASHIER:
			return "Cashier";
		case REGISTRAR:
			return "Registrar";
		case STUDENT:
			return "Student";
		case APPLICANT:
			return "Applicant";
		default:
			return "User";
		}
	}
}
