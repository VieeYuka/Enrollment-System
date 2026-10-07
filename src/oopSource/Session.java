package oopSource;

/**
 * Remembers who is using the system right now, so every frame does not have
 * to be handed the same values over and over.
 *
 * For someone enrolling WITHOUT an account (applicant) the profile fields are
 * filled in by EnrollFrame. For a logged-in student they are loaded at login.
 */
public final class Session {

	private static String username = "Guest";
	private static String role = Roles.APPLICANT;

	// Profile of the person who is enrolling (shown on Exit / used by the enrollment record)
	private static String studentKey = "";   // student number (or username) - the id used in every table
	private static String fullName = "";
	private static String course = "N/A";
	private static String yearLevel = "N/A";

	// true when studentKey really is a student record that subjects can be enrolled for
	private static boolean enrollTarget = false;

	private Session() {
	}

	/** Called after a successful login. */
	public static void login(String user, String userRole) {
		username = user;
		role = userRole;
		studentKey = user;
		fullName = user;
		course = "N/A";
		yearLevel = "N/A";
		enrollTarget = false;

		if (Roles.normalize(userRole).equals(Roles.STUDENT)) {
			// TODO (database): load the student row that belongs to this username
			StudentService.StudentRecord s = StudentService.findByUsername(user);
			if (s != null) {
				studentKey = s.studentId;
				fullName = s.firstName + " " + s.lastName;
				course = s.course;
				yearLevel = s.yearLevel;
				enrollTarget = true;
			}
		}
	}

	/** Called by EnrollFrame (no-account flow) once the application form is saved. */
	public static void startApplicant(String studentNumber, String name, String studentCourse, String year) {
		role = Roles.APPLICANT;
		username = name;
		studentKey = studentNumber;
		fullName = name;
		course = studentCourse;
		yearLevel = year;
		enrollTarget = true;
	}

	/**
	 * Staff enrolling subjects ON BEHALF of a student (e.g. right after "+ Add New Student").
	 * Unlike startApplicant, the staff member stays logged in with their own role.
	 */
	public static void enrollFor(String studentNumber, String name, String studentCourse, String year) {
		studentKey = studentNumber;
		fullName = name;
		course = studentCourse;
		yearLevel = year;
		enrollTarget = true;
	}

	/** Forget the student staff were enrolling for. */
	public static void clearEnrollTarget() {
		if (!Roles.normalize(role).equals(Roles.STUDENT) && !Roles.normalize(role).equals(Roles.APPLICANT)) {
			studentKey = "";
			fullName = "";
			course = "N/A";
			yearLevel = "N/A";
			enrollTarget = false;
		}
	}

	public static boolean hasEnrollTarget() { return enrollTarget; }

	public static String username() { return username; }
	public static String role() { return role; }
	public static String studentKey() { return studentKey.isEmpty() ? username : studentKey; }
	public static String fullName() { return fullName.isEmpty() ? username : fullName; }
	public static String course() { return course; }
	public static String yearLevel() { return yearLevel; }
}
