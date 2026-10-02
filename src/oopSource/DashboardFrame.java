package oopSource;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class DashboardFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private String loggedInUser;

	// Dark Teal Theme Colors
	private static final Color DARK_TEAL = new Color(11, 55, 49);
	private static final Color HOVER_TEAL = new Color(20, 80, 72);
	private static final Color ACTIVE_NAV = new Color(40, 95, 87);
	private static final Color LIGHT_BG = new Color(235, 235, 235);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DashboardFrame frame = new DashboardFrame("Admin");
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
	public DashboardFrame(String username) {
		this.loggedInUser = username;

		setTitle("Rey University - System Dashboard");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1280, 720);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		// =============================================================
		// LEFT SIDEBAR
		// =============================================================
		JPanel sidebarPanel = new JPanel();
		sidebarPanel.setBackground(DARK_TEAL);
		sidebarPanel.setPreferredSize(new Dimension(280, 720));
		sidebarPanel.setLayout(new BorderLayout(0, 0));
		contentPane.add(sidebarPanel, BorderLayout.WEST);

		// ---- Logo + Title ----
		JPanel logoPanel = new JPanel();
		logoPanel.setOpaque(false);
		logoPanel.setBorder(new EmptyBorder(20, 15, 20, 15));
		GridBagLayout gbl_logoPanel = new GridBagLayout();
		logoPanel.setLayout(gbl_logoPanel);
		sidebarPanel.add(logoPanel, BorderLayout.NORTH);

		JLabel lblLogo = new JLabel("");
		lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
		URL imgUrl = DashboardFrame.class.getResource("/RUlogo (1).png");
		if (imgUrl != null) {
			Image img = new ImageIcon(imgUrl).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
			lblLogo.setIcon(new ImageIcon(img));
		} else {
			lblLogo.setText("LOGO");
			lblLogo.setFont(new Font("Arial", Font.BOLD, 10));
			lblLogo.setForeground(DARK_TEAL);
			lblLogo.setOpaque(true);
			lblLogo.setBackground(Color.WHITE);
			lblLogo.setPreferredSize(new Dimension(45, 45));
		}
		GridBagConstraints gbc_lblLogo = new GridBagConstraints();
		gbc_lblLogo.insets = new Insets(0, 5, 0, 10);
		gbc_lblLogo.gridx = 0;
		gbc_lblLogo.gridy = 0;
		logoPanel.add(lblLogo, gbc_lblLogo);

		JLabel lblTitle = new JLabel("REY UNIVERSITY");
		lblTitle.setForeground(Color.WHITE);
		lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
		GridBagConstraints gbc_lblTitle = new GridBagConstraints();
		gbc_lblTitle.insets = new Insets(0, 5, 0, 10);
		gbc_lblTitle.weightx = 1.0;
		gbc_lblTitle.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblTitle.gridx = 1;
		gbc_lblTitle.gridy = 0;
		logoPanel.add(lblTitle, gbc_lblTitle);

		// ---- Navigation ----
		JPanel navContainer = new JPanel();
		navContainer.setOpaque(false);
		navContainer.setLayout(new GridLayout(8, 1, 0, 10));
		navContainer.setBorder(new EmptyBorder(10, 15, 10, 15));
		sidebarPanel.add(navContainer, BorderLayout.CENTER);

		// Dashboard (current page - already active, so no action needed)
		NavItem navDashboard = new NavItem("Dashboard", true);
		navContainer.add(navDashboard);

		// Students
		NavItem navStudents = new NavItem("Students", false);
		navStudents.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFrame(new StudentsFrame(loggedInUser));
			}
		});
		navContainer.add(navStudents);

		// Enrollment
		NavItem navEnrollment = new NavItem("Enrollment", false);
		navEnrollment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFrame(new EnrollmentStudent());
			}
		});
		navContainer.add(navEnrollment);

		// Courses & Schedules
		NavItem navCourses = new NavItem("Courses & Schedules", false);
		navCourses.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFrame(new CoursesFrame());
			}
		});
		navContainer.add(navCourses);

		// Tuition & Payments
		NavItem navTuition = new NavItem("Tuition & Payments", false);
		navTuition.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFrame(new TuitionFrame());
			}
		});
		navContainer.add(navTuition);

		// ---- Bottom User Profile ----
		JPanel userProfilePanel = new JPanel();
		userProfilePanel.setOpaque(false);
		userProfilePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(255, 255, 255, 40)),
				new EmptyBorder(15, 15, 15, 15)));
		userProfilePanel.setLayout(new GridBagLayout());
		sidebarPanel.add(userProfilePanel, BorderLayout.SOUTH);

		JLabel lblPfp = new JLabel("", SwingConstants.CENTER);
		lblPfp.setPreferredSize(new Dimension(40, 40));
		lblPfp.setHorizontalAlignment(SwingConstants.CENTER);

		URL pfpUrl = this.getClass().getResource("/Profile1.png");
		if (pfpUrl != null) {
			Image pfpImg = new ImageIcon(pfpUrl).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
			lblPfp.setIcon(new ImageIcon(pfpImg));
		} else {
			lblPfp.setText("PFP");
			lblPfp.setOpaque(true);
			lblPfp.setBackground(Color.WHITE);
			lblPfp.setForeground(DARK_TEAL);
			lblPfp.setFont(new Font("Arial", Font.BOLD, 10));
		}

		GridBagConstraints gbc_lblPfp = new GridBagConstraints();
		gbc_lblPfp.gridheight = 2;
		gbc_lblPfp.insets = new Insets(0, 0, 0, 12);
		gbc_lblPfp.gridx = 0;
		gbc_lblPfp.gridy = 0;
		userProfilePanel.add(lblPfp, gbc_lblPfp);

		JLabel lblUsername = new JLabel(this.loggedInUser);
		lblUsername.setForeground(Color.WHITE);
		lblUsername.setFont(new Font("Arial", Font.BOLD, 15));
		GridBagConstraints gbc_lblUsername = new GridBagConstraints();
		gbc_lblUsername.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblUsername.weightx = 1.0;
		gbc_lblUsername.insets = new Insets(0, 0, 2, 0);
		gbc_lblUsername.gridx = 1;
		gbc_lblUsername.gridy = 0;
		userProfilePanel.add(lblUsername, gbc_lblUsername);

		JLabel lblRole = new JLabel("System Administrator");
		lblRole.setForeground(new Color(180, 200, 195));
		lblRole.setFont(new Font("Arial", Font.PLAIN, 12));
		GridBagConstraints gbc_lblRole = new GridBagConstraints();
		gbc_lblRole.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblRole.weightx = 1.0;
		gbc_lblRole.gridx = 1;
		gbc_lblRole.gridy = 1;
		userProfilePanel.add(lblRole, gbc_lblRole);

		// =============================================================
		// RIGHT MAIN CONTENT AREA
		// =============================================================
		JPanel mainContentPanel = new JPanel();
		mainContentPanel.setBackground(LIGHT_BG);
		mainContentPanel.setLayout(new BorderLayout(0, 0));
		contentPane.add(mainContentPanel, BorderLayout.CENTER);

		// ---- Top Welcome Bar ----
		JPanel topBar = new JPanel();
		topBar.setBackground(Color.WHITE);
		topBar.setPreferredSize(new Dimension(0, 70));
		topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
		topBar.setLayout(new GridBagLayout());
		mainContentPanel.add(topBar, BorderLayout.NORTH);

		JLabel lblTopPfp = new JLabel("", SwingConstants.CENTER);
		lblTopPfp.setPreferredSize(new Dimension(38, 38));
		lblTopPfp.setHorizontalAlignment(SwingConstants.CENTER);

		URL pfpUrl2 = this.getClass().getResource("/Profile2.png");
		if (pfpUrl2 != null) {
			Image topPfpImg = new ImageIcon(pfpUrl2).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
			lblTopPfp.setIcon(new ImageIcon(topPfpImg));
		} else {
			lblTopPfp.setText("PFP");
			lblTopPfp.setOpaque(true);
			lblTopPfp.setBackground(new Color(160, 175, 180));
			lblTopPfp.setForeground(Color.WHITE);
			lblTopPfp.setFont(new Font("Arial", Font.BOLD, 10));
		}

		GridBagConstraints gbc_lblTopPfp = new GridBagConstraints();
		gbc_lblTopPfp.insets = new Insets(0, 25, 0, 12);
		gbc_lblTopPfp.gridx = 0;
		gbc_lblTopPfp.gridy = 0;
		topBar.add(lblTopPfp, gbc_lblTopPfp);

		JLabel lblWelcomeMsg = new JLabel("Welcome, " + this.loggedInUser);
		lblWelcomeMsg.setFont(new Font("Arial", Font.BOLD, 20));
		GridBagConstraints gbc_lblWelcomeMsg = new GridBagConstraints();
		gbc_lblWelcomeMsg.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblWelcomeMsg.weightx = 1.0;
		gbc_lblWelcomeMsg.gridx = 1;
		gbc_lblWelcomeMsg.gridy = 0;
		topBar.add(lblWelcomeMsg, gbc_lblWelcomeMsg);

		// ---- Dashboard Body ----
		JPanel dashboardBody = new JPanel();
		dashboardBody.setOpaque(false);
		dashboardBody.setBorder(new EmptyBorder(25, 25, 25, 25));
		GridBagLayout gbl_dashboardBody = new GridBagLayout();
		gbl_dashboardBody.columnWidths = new int[]{0, 0, 0, -102};
		dashboardBody.setLayout(gbl_dashboardBody);
		mainContentPanel.add(dashboardBody, BorderLayout.CENTER);

		JLabel lblDashboardHeader = new JLabel("Dashboard");
		lblDashboardHeader.setFont(new Font("Arial", Font.BOLD, 26));
		GridBagConstraints gbc_lblDashboardHeader = new GridBagConstraints();
		gbc_lblDashboardHeader.anchor = GridBagConstraints.WEST;
		gbc_lblDashboardHeader.gridwidth = 4;
		gbc_lblDashboardHeader.insets = new Insets(0, 0, 20, 0);
		gbc_lblDashboardHeader.gridx = 0;
		gbc_lblDashboardHeader.gridy = 0;
		dashboardBody.add(lblDashboardHeader, gbc_lblDashboardHeader);

		// ---- Stat Cards ----
		StatCard cardEnrolled = new StatCard("Total Enrolled Students\nToday", "100", new Color(38, 128, 98));
		GridBagConstraints gbc_cardEnrolled = new GridBagConstraints();
		gbc_cardEnrolled.fill = GridBagConstraints.BOTH;
		gbc_cardEnrolled.weightx = 1.0;
		gbc_cardEnrolled.insets = new Insets(0, 0, 20, 10);
		gbc_cardEnrolled.gridx = 0;
		gbc_cardEnrolled.gridy = 1;
		dashboardBody.add(cardEnrolled, gbc_cardEnrolled);

		StatCard cardTotal = new StatCard("Total Students\n(Overall)", "100", new Color(60, 115, 190));
		GridBagConstraints gbc_cardTotal = new GridBagConstraints();
		gbc_cardTotal.fill = GridBagConstraints.BOTH;
		gbc_cardTotal.weightx = 1.0;
		gbc_cardTotal.insets = new Insets(0, 5, 20, 10);
		gbc_cardTotal.gridx = 1;
		gbc_cardTotal.gridy = 1;
		dashboardBody.add(cardTotal, gbc_cardTotal);

		StatCard cardFeature3 = new StatCard("Feature 3", "100", new Color(240, 178, 75));
		GridBagConstraints gbc_cardFeature3 = new GridBagConstraints();
		gbc_cardFeature3.fill = GridBagConstraints.BOTH;
		gbc_cardFeature3.weightx = 1.0;
		gbc_cardFeature3.insets = new Insets(0, 5, 20, 10);
		gbc_cardFeature3.gridx = 2;
		gbc_cardFeature3.gridy = 1;
		dashboardBody.add(cardFeature3, gbc_cardFeature3);

		StatCard cardFeature4 = new StatCard("Feature 4", "100", new Color(215, 78, 85));
		GridBagConstraints gbc_cardFeature4 = new GridBagConstraints();
		gbc_cardFeature4.fill = GridBagConstraints.BOTH;
		gbc_cardFeature4.weightx = 1.0;
		gbc_cardFeature4.insets = new Insets(0, 5, 20, 0);
		gbc_cardFeature4.gridx = 3;
		gbc_cardFeature4.gridy = 1;
		dashboardBody.add(cardFeature4, gbc_cardFeature4);

		// ---- Lower Section ----
		RoundedPanel leftBox = new RoundedPanel(Color.WHITE, 20);
		GridBagConstraints gbc_leftBox = new GridBagConstraints();
		gbc_leftBox.fill = GridBagConstraints.BOTH;
		gbc_leftBox.gridwidth = 4;
		gbc_leftBox.weightx = 2.5;
		gbc_leftBox.weighty = 1.0;
		gbc_leftBox.insets = new Insets(0, 0, 0, 15);
		gbc_leftBox.gridx = 0;
		gbc_leftBox.gridy = 2;
		dashboardBody.add(leftBox, gbc_leftBox);
	}

	/**
	 * Shows the next frame (same position as this one) and closes the dashboard.
	 */
	private void openFrame(JFrame next) {
		next.setBounds(getBounds());
		next.setVisible(true);
		dispose();
	}

	// =================================================================
	// CUSTOM COMPONENTS
	// =================================================================

	/** Panel with a rounded, filled background. */
	static class RoundedPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private Color bgColor;
		private int arc;

		public RoundedPanel() {
			this(Color.WHITE, 20);
		}

		public RoundedPanel(Color bgColor, int arc) {
			this.bgColor = bgColor;
			this.arc = arc;
			setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(bgColor);
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
			g2.dispose();
		}
	}

	/** Rounded colored statistic card with a title and a big value. */
	static class StatCard extends RoundedPanel {
		private static final long serialVersionUID = 1L;

		public StatCard(String title, String value, Color bgColor) {
			super(bgColor, 16);
			setLayout(new BorderLayout(0, 0));
			setPreferredSize(new Dimension(0, 130));
			setBorder(new EmptyBorder(12, 15, 12, 15));

			JLabel lblCardTitle = new JLabel("<html>" + title.replace("\n", "<br>") + "</html>");
			lblCardTitle.setForeground(new Color(255, 255, 255, 220));
			lblCardTitle.setFont(new Font("Arial", Font.PLAIN, 13));
			add(lblCardTitle, BorderLayout.NORTH);

			JLabel lblCardValue = new JLabel(value);
			lblCardValue.setForeground(Color.WHITE);
			lblCardValue.setFont(new Font("Arial", Font.PLAIN, 36));
			add(lblCardValue, BorderLayout.SOUTH);
		}
	}

	/** Sidebar navigation item with hover effect and click (action) support. */
	static class NavItem extends JPanel {
		private static final long serialVersionUID = 1L;
		private final String title;
		private final List<ActionListener> listeners = new ArrayList<ActionListener>();

		public NavItem(String title, final boolean isActive) {
			this.title = title;
			setLayout(new BorderLayout(0, 0));
			setOpaque(true);
			setBackground(isActive ? ACTIVE_NAV : DARK_TEAL);
			setCursor(new Cursor(Cursor.HAND_CURSOR));

			JLabel label = new JLabel(title);
			label.setForeground(Color.WHITE);
			label.setFont(new Font("Arial", isActive ? Font.BOLD : Font.PLAIN, 15));
			label.setBorder(new EmptyBorder(10, 20, 10, 20));
			add(label, BorderLayout.CENTER);

			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					if (!isActive) setBackground(HOVER_TEAL);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					if (!isActive) setBackground(DARK_TEAL);
				}

				@Override
				public void mouseClicked(MouseEvent e) {
					fireAction();
				}
			});
		}

		/** Register a listener that runs when this item is clicked. */
		public void addActionListener(ActionListener l) {
			listeners.add(l);
		}

		private void fireAction() {
			ActionEvent ev = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, title);
			for (ActionListener l : new ArrayList<ActionListener>(listeners)) {
				l.actionPerformed(ev);
			}
		}
	}
}