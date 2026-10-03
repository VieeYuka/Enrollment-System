package oopSource;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ExitFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JLabel lblStatusBadge;

    private static final Color DARK_TEAL = new Color(11, 55, 49);
    private static final Color LIGHT_BG = new Color(235, 235, 235);
    private static final Color PENDING_ORANGE = new Color(211, 84, 0);
    private static final Color ENROLLED_GREEN = new Color(39, 174, 96);

    public ExitFrame(String studentName, String enrollmentStatus) {
        setTitle("Rey University - Enrollment Submitted");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 500);
        setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(LIGHT_BG);
        setContentPane(contentPane);

        // Top Header
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topBar.setBackground(DARK_TEAL);
        topBar.setPreferredSize(new Dimension(0, 70));

        JLabel lblUni = new JLabel("REY UNIVERSITY ENROLLMENT SYSTEM");
        lblUni.setForeground(Color.WHITE);
        lblUni.setFont(new Font("Arial", Font.BOLD, 18));
        topBar.add(lblUni);
        contentPane.add(topBar, BorderLayout.NORTH);

        // Center Card
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        contentPane.add(centerWrapper, BorderLayout.CENTER);

        StudentsFrame.RoundedPanel card = new StudentsFrame.RoundedPanel(Color.WHITE, 25);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel lblThankYou = new JLabel("Thank you for using the system!");
        lblThankYou.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblThankYou.setFont(new Font("Arial", Font.BOLD, 22));
        lblThankYou.setForeground(DARK_TEAL);

        JLabel lblMessage = new JLabel("Your enrollment application has been successfully recorded.");
        lblMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblMessage.setFont(new Font("Arial", Font.PLAIN, 14));
        lblMessage.setForeground(Color.GRAY);

        JLabel lblStatusTitle = new JLabel("Current Enrollment Status:");
        lblStatusTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblStatusTitle.setFont(new Font("Arial", Font.BOLD, 14));

        lblStatusBadge = new JLabel(enrollmentStatus.toUpperCase());
        lblStatusBadge.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblStatusBadge.setFont(new Font("Arial", Font.BOLD, 16));
        lblStatusBadge.setOpaque(true);
        updateStatusBadge(enrollmentStatus);

        JButton btnClose = new JButton("Close System");
        btnClose.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnClose.setFont(new Font("Arial", Font.BOLD, 14));
        btnClose.setBackground(DARK_TEAL);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setPreferredSize(new Dimension(160, 40));
        btnClose.setMaximumSize(new Dimension(160, 40));
        btnClose.addActionListener(e -> System.exit(0));

        card.add(lblThankYou);
        card.add(Box.createVerticalStrut(10));
        card.add(lblMessage);
        card.add(Box.createVerticalStrut(25));
        card.add(lblStatusTitle);
        card.add(Box.createVerticalStrut(8));
        card.add(lblStatusBadge);
        card.add(Box.createVerticalStrut(30));
        card.add(btnClose);

        centerWrapper.add(card);
    }

    /** Placeholder database helper method to update status badge styling */
    public void updateStatusBadge(String status) {
        if ("Enrolled".equalsIgnoreCase(status)) {
            lblStatusBadge.setText(" ENROLLED ");
            lblStatusBadge.setBackground(new Color(223, 245, 232));
            lblStatusBadge.setForeground(ENROLLED_GREEN);
            lblStatusBadge.setBorder(BorderFactory.createLineBorder(ENROLLED_GREEN, 1));
        } else {
            lblStatusBadge.setText(" PENDING ");
            lblStatusBadge.setBackground(new Color(253, 235, 208));
            lblStatusBadge.setForeground(PENDING_ORANGE);
            lblStatusBadge.setBorder(BorderFactory.createLineBorder(PENDING_ORANGE, 1));
        }
    }
}