package oopSource;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

public class NavBuilder {

    public static JPanel createSidebar(JFrame owner, String username, String role) {
        JPanel sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBackground(new Color(11, 55, 49));
        sidebarPanel.setPreferredSize(new Dimension(280, 720));

        // Navigation Links Container
        JPanel navContainer = new JPanel(new GridLayout(8, 1, 0, 10));
        navContainer.setOpaque(false);
        navContainer.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        // Role-Based Navigation Logic
        boolean isGuestOrStudent = role.equalsIgnoreCase("guest") || role.equalsIgnoreCase("student");
        boolean isCashier = role.equalsIgnoreCase("cashier");
        boolean isAdmin = role.equalsIgnoreCase("admin");

        if (isAdmin || isCashier) {
            navContainer.add(createNavItem("Dashboard", owner instanceof DashboardFrame, 
                e -> navigate(owner, new DashboardFrame(username, role))));
        }

        if (isAdmin) {
            navContainer.add(createNavItem("Students", owner instanceof StudentsFrame, 
                e -> navigate(owner, new StudentsFrame(username, role))));
            navContainer.add(createNavItem("Enrollment", owner instanceof EnrollmentStudent, 
                e -> navigate(owner, new EnrollmentStudent(username))));
        }

        if (isAdmin || isGuestOrStudent) {
            navContainer.add(createNavItem("Courses & Schedules", owner instanceof CoursesFrame, 
                e -> navigate(owner, new CoursesFrame(username, role))));
        }

        if (isAdmin || isCashier) {
            navContainer.add(createNavItem("Tuition & Payments", owner instanceof TuitionFrame, 
                e -> navigate(owner, new TuitionFrame(username, role))));
        }

        sidebarPanel.add(navContainer, BorderLayout.CENTER);
        return sidebarPanel;
    }

    private static StudentsFrame.NavItem createNavItem(String title, boolean active, ActionListener listener) {
        StudentsFrame.NavItem item = new StudentsFrame.NavItem(title, active);
        item.addActionListener(listener);
        return item;
    }

    private static void navigate(JFrame current, JFrame next) {
        next.setBounds(current.getBounds());
        next.setVisible(true);
        current.dispose();
    }
}