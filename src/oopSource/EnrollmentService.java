package oopSource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Enrollment requests and their status (Pending -> Enrolled).
 *
 *   submit()      Confirm Enrollment  -> status "Pending", student appears (Unpaid) in Tuition
 *   setStatus()   Cashier confirms payment -> status "Enrolled"
 *   getStatus()   ExitFrame reads this to show the badge
 *
 * ---- DATABASE HOOK -------------------------------------------------------
 *   CREATE TABLE IF NOT EXISTS enrollments (
 *       id INT AUTO_INCREMENT PRIMARY KEY,
 *       student_number VARCHAR(12) NOT NULL,
 *       total_units INT NOT NULL,
 *       status VARCHAR(20) NOT NULL DEFAULT 'Pending',
 *       date_submitted DATE NOT NULL,
 *       date_enrolled DATE NULL);
 *   (plus an enrollment_subjects table: enrollment_id, course_code)
 *
 *   submit():              INSERT INTO enrollments ... + INSERT INTO enrollment_subjects ...
 *   getStatus():           SELECT status FROM enrollments WHERE student_number=? ORDER BY id DESC LIMIT 1
 *   setStatus():           UPDATE enrollments SET status=?, date_enrolled=CURDATE() WHERE student_number=?
 *   countByStatus():       SELECT COUNT(*) FROM enrollments WHERE status=?
 *   countEnrolledToday():  SELECT COUNT(*) FROM enrollments WHERE status='Enrolled' AND date_enrolled=CURDATE()
 *   search():              SELECT e.*, s.first_name, s.last_name, s.course, s.year_level FROM enrollments e
 *                          JOIN students s ON s.student_number = e.student_number
 *                          WHERE (e.student_number LIKE ? OR s.first_name LIKE ? OR s.last_name LIKE ?)
 *                            AND (? = 'All Statuses' OR e.status = ?)   ORDER BY e.id DESC
 *   approve():             UPDATE enrollments SET status='Enrolled', date_enrolled=CURDATE(),
 *                                 date_approved=CURDATE() WHERE student_number=? AND status='Pending'
 *   countApprovedToday():  SELECT COUNT(*) FROM enrollments WHERE date_approved = CURDATE()
 * ---------------------------------------------------------------------------
 */
public final class EnrollmentService {

	public static final String PENDING = "Pending";
	public static final String ENROLLED = "Enrolled";

	private static final SimpleDateFormat DAY = new SimpleDateFormat("yyyy-MM-dd");

	/** One enrollment application: who applied, and the subjects they picked. */
	public static class Enrollment {
		public final String studentKey;
		public final String name;
		public final String course;
		public final String yearLevel;
		public final int units;
		public final List<Object[]> subjects;   // rows of {code, title, units, department}
		public String status;
		public String dateEnrolled = "";
		public String dateApproved = "";

		Enrollment(String studentKey, String name, String course, String yearLevel,
				int units, List<Object[]> subjects, String status) {
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

	private static final Map<String, Enrollment> BY_STUDENT = new LinkedHashMap<String, Enrollment>();

	static {
		// Placeholder data matching StudentService samples - remove once MySQL is used
		Enrollment maria = new Enrollment("2026-0001", "Maria Santos", "BSIT", "1st Year", 21,
				sampleSubjects("CCS101", "IT102", "MATH101", "ENG101", "CCS102", "IT103", "MATH102"), ENROLLED);
		maria.dateEnrolled = today();
		BY_STUDENT.put(maria.studentKey, maria);
		BY_STUDENT.put("2026-0002", new Enrollment("2026-0002", "John Doe", "BSBA", "2nd Year", 18,
				sampleSubjects("CCS101", "IT102", "MATH101", "ENG101", "ENG102", "IT104"), PENDING));
	}

	/** Placeholder helper: builds subject rows from course codes (all 3 units). */
	private static List<Object[]> sampleSubjects(String... codes) {
		String[][] catalog = {
				{"CCS101", "Introduction to Computing", "CS"}, {"CCS102", "Computer Programming 1", "CS"},
				{"IT102", "Web Development", "IT"}, {"IT103", "Networking Fundamentals", "IT"},
				{"IT104", "Database Management", "IT"}, {"MATH101", "College Algebra", "Math"},
				{"MATH102", "Trigonometry", "Math"}, {"ENG101", "English for Academic Purposes", "English"},
				{"ENG102", "Purposive Communication", "English"}};
		List<Object[]> rows = new ArrayList<Object[]>();
		for (String code : codes) {
			for (String[] c : catalog) {
				if (c[0].equals(code)) rows.add(new Object[] {c[0], c[1], 3, c[2]});
			}
		}
		return rows;
	}

	private EnrollmentService() {
	}

	/**
	 * Saves a new enrollment as "Pending", adds the student to the Tuition list (Unpaid)
	 * and writes it to the activity feed.
	 *
	 * @param subjects rows of {code, title, units, department} - same rows EnrollmentSummaryFrame shows
	 */
	public static Enrollment submit(String studentKey, String name, String course, String yearLevel,
			List<Object[]> subjects) {
		int units = 0;
		for (Object[] row : subjects) {
			units += Integer.parseInt(String.valueOf(row[2]));
		}

		// TODO (database): INSERT the enrollment + its subjects
		Enrollment e = new Enrollment(studentKey, name, course, yearLevel, units,
				new ArrayList<Object[]>(subjects), PENDING);
		BY_STUDENT.put(studentKey, e);

		TuitionFrame.addApprovedStudent(studentKey, name, course, yearLevel, units);
		StudentService.updateStatus(studentKey, PENDING);
		ActivityLOg.log(ActivityLOg.Type.ENROLLMENT,
				name + " enrolled in " + subjects.size() + " subjects (" + units + " units)");
		return e;
	}

	/**
	 * Applications for the Enrollment screen, newest first.
	 * @param query  student ID or name (any word order); empty = everyone
	 * @param status "Pending", "Enrolled" or anything else (e.g. "All Statuses") for all
	 */
	public static List<Enrollment> search(String query, String status) {
		// TODO (database): run the SELECT in the header comment
		List<Enrollment> result = new ArrayList<Enrollment>();
		for (Enrollment e : BY_STUDENT.values()) {
			boolean statusOk = !(PENDING.equals(status) || ENROLLED.equals(status)) || e.status.equals(status);
			if (statusOk && SearchField.matches(query, e.studentKey, e.name)) {
				result.add(0, e); // newest first
			}
		}
		return result;
	}

	/** The application of one student (null if none). */
	public static Enrollment find(String studentKey) {
		// TODO (database): SELECT ... WHERE student_number = ?
		return BY_STUDENT.get(studentKey);
	}

	/**
	 * Registrar / admin approves a pending application -> status becomes "Enrolled".
	 * @return false if there is nothing to approve (no application, or already Enrolled)
	 */
	public static boolean approve(String studentKey) {
		// TODO (database): UPDATE enrollments SET status='Enrolled', date_enrolled=CURDATE(), date_approved=CURDATE() ...
		Enrollment e = BY_STUDENT.get(studentKey);
		if (e == null || ENROLLED.equals(e.status)) {
			return false;
		}
		e.status = ENROLLED;
		e.dateEnrolled = today();
		e.dateApproved = today();
		StudentService.updateStatus(studentKey, ENROLLED);
		ActivityLOg.log(ActivityLOg.Type.ENROLLMENT, e.name + "'s application was approved");
		return true;
	}

	public static int countApprovedToday() {
		// TODO (database): SELECT COUNT(*) ... date_approved = CURDATE()
		int n = 0;
		for (Enrollment e : BY_STUDENT.values()) {
			if (e.dateApproved.equals(today())) n++;
		}
		return n;
	}

	/** Pending until the cashier confirms the payment. */
	public static String getStatus(String studentKey) {
		// TODO (database): SELECT status ...
		Enrollment e = BY_STUDENT.get(studentKey);
		return e == null ? PENDING : e.status;
	}

	/** Changes the status (also updates the student's row and the activity feed). */
	public static void setStatus(String studentKey, String status) {
		// TODO (database): UPDATE enrollments SET status = ?, date_enrolled = ...
		Enrollment e = BY_STUDENT.get(studentKey);
		if (e == null) {
			return; // student has no enrollment request
		}
		e.status = status;
		e.dateEnrolled = ENROLLED.equals(status) ? today() : "";
		StudentService.updateStatus(studentKey, status);
		ActivityLOg.log(ActivityLOg.Type.ENROLLMENT, e.name + " is now " + status);
	}

	public static int countByStatus(String status) {
		// TODO (database): SELECT COUNT(*) ...
		int n = 0;
		for (Enrollment e : BY_STUDENT.values()) {
			if (e.status.equals(status)) n++;
		}
		return n;
	}

	public static int countEnrolledToday() {
		// TODO (database): SELECT COUNT(*) ... date_enrolled = CURDATE()
		int n = 0;
		for (Enrollment e : BY_STUDENT.values()) {
			if (e.status.equals(ENROLLED) && e.dateEnrolled.equals(today())) n++;
		}
		return n;
	}

	private static String today() {
		return DAY.format(new Date());
	}
}