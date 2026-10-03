package oopSource;

import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {

    // Placeholder data access layer for database integration
    public static int getTotalEnrolledStudents() {
        return 1248; // TODO: DB Query "SELECT COUNT(*) FROM students WHERE status='Enrolled'"
    }

    public static int getTotalPaidStudents() {
        return 980; // TODO: DB Query "SELECT COUNT(*) FROM tuition WHERE status='Paid'"
    }

    public static int getPendingApplications() {
        return 42; // TODO: DB Query "SELECT COUNT(*) FROM enrollments WHERE status='Pending'"
    }

    public static List<String> getRecentSystemActions() {
        List<String> actions = new ArrayList<>();
        actions.add("Student Enrolled: Santos, Maria (RU-2026-0001)");
        actions.add("Tuition Paid: Dela Cruz, Juan (₱6,300.00)");
        actions.add("Application Approved: Smith, Anne");
        actions.add("Student Enrolled: Doe, John (RU-2026-0003)");
        actions.add("Course Added: CS102 - Data Structures");
        return actions;
    }
}