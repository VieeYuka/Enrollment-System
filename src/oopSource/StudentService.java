package oopSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Everything the screens need from the "students" table.
 * The frames only call these methods, so when MySQL is plugged in you
 * change the method BODIES here and no frame has to be touched.
 *
 * ---- DATABASE HOOK (table already created in DBConnection) ---------------
 *   search():       SELECT * FROM students
 *                   WHERE student_number LIKE ? OR first_name LIKE ? OR last_name LIKE ?
 *                      OR CONCAT(first_name,' ',last_name) LIKE ?   ORDER BY student_id DESC
 *   add():          INSERT INTO students(student_number, username, first_name, last_name,
 *                                        personal_email, univ_email, course, year_level, status)
 *   update():       UPDATE students SET first_name=?, last_name=?, univ_email=?, course=?,
 *                                       year_level=?, status=?  WHERE student_number=?
 *   delete():       DELETE FROM students WHERE student_number=?
 *   updateStatus(): UPDATE students SET status=? WHERE student_number=?
 * ---------------------------------------------------------------------------
 */
public final class StudentService {

	/** Drop-down choices (replace with SELECT course_name FROM courses when ready). */
	public static final String[] COURSES = {"BSCS", "BSIT", "BSIS", "BSEd", "BSBA"};
	public static final String[] YEAR_LEVELS = {"1st Year", "2nd Year", "3rd Year", "4th Year"};
	public static final String[] STATUSES = {EnrollmentService.PENDING, EnrollmentService.ENROLLED};

	/** One row of the students table. */
	public static class StudentRecord {
		public String studentId;     // student_number
		public String username;
		public String firstName;
		public String lastName;
		public String email;         // university email
		public String course;
		public String yearLevel;
		public String status;        // Pending / Enrolled

		public StudentRecord(String studentId, String username, String firstName, String lastName,
				String email, String course, String yearLevel, String status) {
			this.studentId = studentId;
			this.username = username;
			this.firstName = firstName;
			this.lastName = lastName;
			this.email = email;
			this.course = course;
			this.yearLevel = yearLevel;
			this.status = status;
		}

		public String fullName() {
			return firstName + " " + lastName;
		}

		public StudentRecord copy() {
			return new StudentRecord(studentId, username, firstName, lastName, email, course, yearLevel, status);
		}
	}

	private static final List<StudentRecord> STUDENTS = new ArrayList<StudentRecord>();

	static {
		// Placeholder data - remove once MySQL is used
		STUDENTS.add(new StudentRecord("2026-0001", "maria.santos", "Maria", "Santos",
				"maria.santos@reyuniversity.edu", "BSIT", "1st Year", EnrollmentService.ENROLLED));
		STUDENTS.add(new StudentRecord("2026-0002", "john.doe", "John", "Doe",
				"john.doe@reyuniversity.edu", "BSBA", "2nd Year", EnrollmentService.PENDING));
	}

	private StudentService() {
	}

	/** Matches student ID or name (first, last, or both in any order). Empty query = everyone. */
	public static List<StudentRecord> search(String query) {
		// TODO (database): run the SELECT in the header comment
		List<StudentRecord> result = new ArrayList<StudentRecord>();
		for (StudentRecord s : STUDENTS) {
			if (SearchField.matches(query, s.studentId, s.firstName, s.lastName, s.course)) {
				result.add(s);
			}
		}
		return result;
	}

	public static StudentRecord findById(String studentId) {
		for (StudentRecord s : STUDENTS) {
			if (s.studentId.equals(studentId)) return s;
		}
		return null;
	}

	public static StudentRecord findByUsername(String username) {
		for (StudentRecord s : STUDENTS) {
			if (s.username != null && s.username.equalsIgnoreCase(username)) return s;
		}
		return null;
	}

	/** The students table has UNIQUE emails - check before adding. */
	public static boolean emailExists(String email) {
		for (StudentRecord s : STUDENTS) {
			if (s.email.equalsIgnoreCase(email)) return true;
		}
		return false;
	}

	/** Creates a student with status "Pending" and a new student number. Returns the saved record. */
	public static StudentRecord add(String firstName, String lastName, String email, String course, String yearLevel) {
		// TODO (database): INSERT, then read back the generated student number
		StudentRecord s = new StudentRecord(nextStudentNumber(), email, firstName, lastName,
				email, course, yearLevel, EnrollmentService.PENDING);
		STUDENTS.add(s);
		return s;
	}

	/** Saves the edited fields of a student (matched by studentId). */
	public static void update(StudentRecord edited) {
		// TODO (database): UPDATE students SET ... WHERE student_number = ?
		StudentRecord original = findById(edited.studentId);
		if (original != null) {
			original.firstName = edited.firstName;
			original.lastName = edited.lastName;
			original.email = edited.email;
			original.course = edited.course;
			original.yearLevel = edited.yearLevel;
			original.status = edited.status;
		}
	}

	public static void delete(String studentId) {
		// TODO (database): DELETE FROM students WHERE student_number = ?
		StudentRecord s = findById(studentId);
		if (s != null) STUDENTS.remove(s);
	}

	public static void updateStatus(String studentId, String status) {
		// TODO (database): UPDATE students SET status = ? WHERE student_number = ?
		StudentRecord s = findById(studentId);
		if (s != null) s.status = status;
	}

	public static int count() {
		// TODO (database): SELECT COUNT(*) FROM students
		return STUDENTS.size();
	}

	private static String nextStudentNumber() {
		int max = 0;
		for (StudentRecord s : STUDENTS) {
			try {
				max = Math.max(max, Integer.parseInt(s.studentId.substring(s.studentId.indexOf('-') + 1)));
			} catch (NumberFormatException ignored) {
				// id not in the "2026-0001" format - skip it
			}
		}
		return String.format("2026-%04d", max + 1);
	}
}
