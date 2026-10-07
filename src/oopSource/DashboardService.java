package oopSource;

/**
 * The four numbers on the dashboard cards, read live from MySQL.
 * DashboardFrame asks this class every few seconds.
 */
public final class DashboardService {

	private DashboardService() {
	}

	/** SELECT COUNT(*) FROM enrollments WHERE status='Enrolled' AND date_enrolled = CURDATE() */
	public static int enrolledToday() {
		return EnrollmentService.countEnrolledToday();
	}

	/** SELECT COUNT(*) FROM students */
	public static int totalStudents() {
		return StudentService.count();
	}

	/** SELECT COUNT(*) FROM enrollments WHERE status='Pending' */
	public static int pendingRequests() {
		return EnrollmentService.countByStatus(EnrollmentService.PENDING);
	}

	/** SELECT COUNT(*) FROM enrollments WHERE date_approved = CURDATE() */
	public static int approvedToday() {
		return EnrollmentService.countApprovedToday();
	}
}