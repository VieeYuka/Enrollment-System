package oopSource;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

/**
 * Admin / cashier home screen.
 *  - four number cards  -> values come from DashboardService (database)
 *  - "Recent Activity"  -> entries come from ActivityLog (enrollments, payments, student changes...)
 * Both refresh automatically every few seconds.
 */
public class DashboardFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final int REFRESH_MS = 10000;
	private static final int ACTIVITY_LIMIT = 20;

	private static final Color LIGHT_BG = new Color(235, 235, 235);
	private static final Color DARK_TEAL = new Color(11, 55, 49);

	private final String loggedInUser;
	private final String loggedInRole;

	private StatCard cardEnrolledToday;
	private StatCard cardTotalStudents;
	private StatCard cardPending;
	private StatCard cardApproved;
	private JPanel activityList;
	private final Timer refreshTimer;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DashboardFrame frame = new DashboardFrame("Admin", Roles.ADMIN);
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DashboardFrame(String username, String role) {
		this.loggedInUser = username;
		this.loggedInRole = role;

		setTitle("Rey University - System Dashboard");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1280, 720);

		JPanel contentPane = new JPanel(new BorderLayout(0, 0));
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);

		// Sidebar: shows ONLY the buttons this role is allowed to see (see Roles.java)
		contentPane.add(Sidebar.build(this, loggedInUser, loggedInRole, Roles.NAV_DASHBOARD), BorderLayout.WEST);

		JPanel mainContentPanel = new JPanel(new BorderLayout(0, 0));
		mainContentPanel.setBackground(LIGHT_BG);
		contentPane.add(mainContentPanel, BorderLayout.CENTER);

		mainContentPanel.add(buildTopBar(), BorderLayout.NORTH);
		mainContentPanel.add(buildBody(), BorderLayout.CENTER);

		refreshDashboard();
		refreshTimer = new Timer(REFRESH_MS, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshDashboard();
			}
		});
		refreshTimer.start();
	}

	// =================================================================
	// LAYOUT
	// =================================================================
	private JPanel buildTopBar() {
		JPanel topBar = new JPanel(new GridBagLayout());
		topBar.setBackground(Color.WHITE);
		topBar.setPreferredSize(new Dimension(0, 70));
		topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

		JLabel lblTopPfp = new JLabel("", SwingConstants.CENTER);
		lblTopPfp.setPreferredSize(new Dimension(38, 38));
		URL pfpUrl = this.getClass().getResource("/Profile2.png");
		if (pfpUrl != null) {
			Image img = new ImageIcon(pfpUrl).getImage().getScaledInstance(38, 38, Image.SCALE_SMOOTH);
			lblTopPfp.setIcon(new ImageIcon(img));
		} else {
			lblTopPfp.setText("PFP");
			lblTopPfp.setOpaque(true);
			lblTopPfp.setBackground(new Color(160, 175, 180));
			lblTopPfp.setForeground(Color.WHITE);
			lblTopPfp.setFont(new Font("Arial", Font.BOLD, 10));
		}
		GridBagConstraints gbcPfp = new GridBagConstraints();
		gbcPfp.insets = new Insets(0, 25, 0, 12);
		gbcPfp.gridx = 0;
		gbcPfp.gridy = 0;
		topBar.add(lblTopPfp, gbcPfp);

		JLabel lblWelcomeMsg = new JLabel("Welcome, " + loggedInUser);
		lblWelcomeMsg.setFont(new Font("Arial", Font.BOLD, 20));
		GridBagConstraints gbcWelcome = new GridBagConstraints();
		gbcWelcome.fill = GridBagConstraints.HORIZONTAL;
		gbcWelcome.weightx = 1.0;
		gbcWelcome.gridx = 1;
		gbcWelcome.gridy = 0;
		topBar.add(lblWelcomeMsg, gbcWelcome);
		return topBar;
	}

	private JPanel buildBody() {
		JPanel body = new JPanel(new GridBagLayout());
		body.setOpaque(false);
		body.setBorder(new EmptyBorder(25, 25, 25, 25));

		JLabel lblHeader = new JLabel("Dashboard");
		lblHeader.setFont(new Font("Arial", Font.BOLD, 26));
		GridBagConstraints gbcHeader = new GridBagConstraints();
		gbcHeader.anchor = GridBagConstraints.WEST;
		gbcHeader.gridwidth = 4;
		gbcHeader.insets = new Insets(0, 0, 20, 0);
		gbcHeader.gridx = 0;
		gbcHeader.gridy = 0;
		body.add(lblHeader, gbcHeader);

		// ---- Stat cards (values are filled in by refreshDashboard) ----
		cardEnrolledToday = new StatCard("Total Enrolled Students\nToday", new Color(38, 128, 98));
		cardTotalStudents = new StatCard("Total Students\n(Overall)", new Color(60, 115, 190));
		cardPending = new StatCard("Pending Requests\nTotal", new Color(240, 178, 75));
		cardApproved = new StatCard("Approved Requests\nToday", new Color(215, 78, 85));
		StatCard[] cards = {cardEnrolledToday, cardTotalStudents, cardPending, cardApproved};
		for (int i = 0; i < cards.length; i++) {
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 1.0;
			gbc.insets = new Insets(0, i == 0 ? 0 : 5, 20, i == cards.length - 1 ? 0 : 10);
			gbc.gridx = i;
			gbc.gridy = 1;
			body.add(cards[i], gbc);
		}

		// ---- Recent activity box ----
		GridBagConstraints gbcActivity = new GridBagConstraints();
		gbcActivity.fill = GridBagConstraints.BOTH;
		gbcActivity.gridwidth = 4;
		gbcActivity.weightx = 1.0;
		gbcActivity.weighty = 1.0;
		gbcActivity.gridx = 0;
		gbcActivity.gridy = 2;
		body.add(buildActivityBox(), gbcActivity);
		return body;
	}

	/** The big white rounded box: title + scrolling list of recent actions. */
	private StudentsFrame.RoundedPanel buildActivityBox() {
		StudentsFrame.RoundedPanel box = new StudentsFrame.RoundedPanel(Color.WHITE, 20);
		box.setLayout(new BorderLayout(0, 12));
		box.setBorder(new EmptyBorder(20, 25, 20, 25));

		JLabel lblTitle = new JLabel("Recent Activity");
		lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
		lblTitle.setForeground(DARK_TEAL);
		box.add(lblTitle, BorderLayout.NORTH);

		activityList = new JPanel();
		activityList.setOpaque(false);
		activityList.setLayout(new BoxLayout(activityList, BoxLayout.Y_AXIS));

		JPanel listHolder = new JPanel(new BorderLayout()); // keeps the rows at the top
		listHolder.setOpaque(false);
		listHolder.add(activityList, BorderLayout.NORTH);

		JScrollPane scroll = new JScrollPane(listHolder);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.setOpaque(false);
		scroll.getViewport().setOpaque(false);
		box.add(scroll, BorderLayout.CENTER);
		return box;
	}

	// =================================================================
	// DATA
	// =================================================================

	/** Re-reads the numbers and the activity list. Called on open and by the timer. */
	private void refreshDashboard() {
		cardEnrolledToday.setValue(DashboardService.enrolledToday());
		cardTotalStudents.setValue(DashboardService.totalStudents());
		cardPending.setValue(DashboardService.pendingRequests());
		cardApproved.setValue(DashboardService.approvedToday());

		activityList.removeAll();
		java.util.List<ActivityLOg.Entry> entries = ActivityLOg.recent(ACTIVITY_LIMIT);
		if (entries.isEmpty()) {
			JLabel empty = new JLabel("No recent activity yet.");
			empty.setFont(new Font("Arial", Font.ITALIC, 13));
			empty.setForeground(Color.GRAY);
			activityList.add(empty);
		}
		for (ActivityLOg.Entry entry : entries) {
			activityList.add(buildActivityRow(entry));
		}
		activityList.revalidate();
		activityList.repaint();
	}

	private JPanel buildActivityRow(ActivityLOg.Entry entry) {
		JPanel row = new JPanel(new BorderLayout(15, 0));
		row.setOpaque(false);
		row.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(238, 238, 238)),
				new EmptyBorder(11, 0, 11, 0)));

		// colored tag (Enrollment / Payment / ...) in a fixed-width cell so the rows line up
		JLabel tag = new JLabel(tagText(entry.type), SwingConstants.CENTER);
		tag.setOpaque(true);
		tag.setBackground(tagColor(entry.type));
		tag.setForeground(Color.WHITE);
		tag.setFont(new Font("Arial", Font.BOLD, 11));
		tag.setBorder(new EmptyBorder(4, 8, 4, 8));
		JPanel tagCell = new JPanel(new GridBagLayout());
		tagCell.setOpaque(false);
		tagCell.setPreferredSize(new Dimension(105, 24));
		GridBagConstraints gbcTag = new GridBagConstraints();
		gbcTag.anchor = GridBagConstraints.WEST; // all pills start at the same left edge
		gbcTag.weightx = 1.0;
		tagCell.add(tag, gbcTag);
		row.add(tagCell, BorderLayout.WEST);

		JLabel lblMessage = new JLabel(entry.message);
		lblMessage.setFont(new Font("Arial", Font.PLAIN, 14));
		row.add(lblMessage, BorderLayout.CENTER);

		JLabel lblTime = new JLabel(entry.timeAgo());
		lblTime.setFont(new Font("Arial", Font.PLAIN, 12));
		lblTime.setForeground(Color.GRAY);
		row.add(lblTime, BorderLayout.EAST);
		return row;
	}

	private static String tagText(ActivityLOg.Type type) {
		switch (type) {
		case ENROLLMENT: return "Enrollment";
		case PAYMENT: return "Payment";
		case STUDENT: return "Student";
		case DELETED: return "Deleted";
		default: return "Login";
		}
	}

	private static Color tagColor(ActivityLOg.Type type) {
		switch (type) {
		case ENROLLMENT: return new Color(38, 128, 98);
		case PAYMENT: return new Color(60, 115, 190);
		case STUDENT: return new Color(240, 178, 75);
		case DELETED: return new Color(215, 78, 85);
		default: return new Color(130, 140, 145);
		}
	}

	@Override
	public void dispose() {
		refreshTimer.stop();
		super.dispose();
	}

	// =================================================================
	// CUSTOM COMPONENT
	// =================================================================

	/** Rounded colored statistic card with a title and a big value. */
	static class StatCard extends StudentsFrame.RoundedPanel {
		private static final long serialVersionUID = 1L;
		private final JLabel lblCardValue = new JLabel("0");

		public StatCard(String title, Color bgColor) {
			super(bgColor, 16);
			setLayout(new BorderLayout(0, 0));
			setPreferredSize(new Dimension(0, 130));
			setBorder(new EmptyBorder(12, 15, 12, 15));

			JLabel lblCardTitle = new JLabel("<html>" + title.replace("\n", "<br>") + "</html>");
			lblCardTitle.setForeground(new Color(255, 255, 255, 220));
			lblCardTitle.setFont(new Font("Arial", Font.PLAIN, 13));
			add(lblCardTitle, BorderLayout.NORTH);

			lblCardValue.setForeground(Color.WHITE);
			lblCardValue.setFont(new Font("Arial", Font.PLAIN, 36));
			add(lblCardValue, BorderLayout.SOUTH);
		}

		/** Updates the big number. */
		public void setValue(int value) {
			lblCardValue.setText(String.format("%,d", value));
		}
	}
}