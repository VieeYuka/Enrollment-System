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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class StudentsFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private String loggedInUser;
	private DefaultTableModel studentTableModel;

	// Dark Teal Theme Colors
	private static final Color DARK_TEAL = new Color(11, 55, 49);
	private static final Color HOVER_TEAL = new Color(20, 80, 72);
	private static final Color ACTIVE_NAV = new Color(40, 95, 87);
	private static final Color LIGHT_BG = new Color(235, 235, 235);
	private static final Color ACCENT_GREEN = new Color(38, 128, 98);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StudentsFrame frame = new StudentsFrame("Admin");
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Default constructor
	 */
	public StudentsFrame() {
		this("Admin");
	}

	/**
	 * Create the frame.
	 */
	public StudentsFrame(String username) {
		this.loggedInUser = username;

		setTitle("Rey University - Student Information Management");
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
		URL imgUrl = StudentsFrame.class.getResource("/RUlogo (1).png");
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

		// Dashboard
		NavItem navDashboard = new NavItem("Dashboard", false);
		navDashboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFrame(new DashboardFrame(loggedInUser));
			}
		});
		navContainer.add(navDashboard);

		// Students (current page - already active, so no action needed)
		NavItem navStudents = new NavItem("Students", true);
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
			Image topPfpImg = new ImageIcon(pfpUrl2).getImage().getScaledInstance(38, 38, Image.SCALE_SMOOTH);
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

		// ---- Students Screen Body ----
		JPanel studentsBody = new JPanel();
		studentsBody.setOpaque(false);
		studentsBody.setBorder(new EmptyBorder(25, 25, 25, 25));
		studentsBody.setLayout(new BorderLayout(0, 20));
		mainContentPanel.add(studentsBody, BorderLayout.CENTER);

		// ---- Header & Action Toolbar ----
		JPanel headerToolBar = new JPanel();
		headerToolBar.setOpaque(false);
		headerToolBar.setLayout(new BorderLayout(10, 0));
		studentsBody.add(headerToolBar, BorderLayout.NORTH);

		JLabel lblHeader = new JLabel("Student Management");
		lblHeader.setFont(new Font("Arial", Font.BOLD, 26));
		lblHeader.setForeground(DARK_TEAL);
		headerToolBar.add(lblHeader, BorderLayout.WEST);

		JPanel actionPanel = new JPanel();
		actionPanel.setOpaque(false);
		actionPanel.setLayout(new GridBagLayout());
		headerToolBar.add(actionPanel, BorderLayout.EAST);

		JTextField txtSearch = new JTextField(" Search student ID or name...");
		txtSearch.setFont(new Font("Arial", Font.PLAIN, 13));
		txtSearch.setPreferredSize(new Dimension(240, 38));
		txtSearch.setForeground(Color.GRAY);
		GridBagConstraints gbc_search = new GridBagConstraints();
		gbc_search.insets = new Insets(0, 0, 0, 10);
		gbc_search.gridx = 0;
		gbc_search.gridy = 0;
		actionPanel.add(txtSearch, gbc_search);

		JButton btnAddStudent = new JButton("+ Add New Student");
		btnAddStudent.setFont(new Font("Arial", Font.BOLD, 13));
		btnAddStudent.setBackground(ACCENT_GREEN);
		btnAddStudent.setForeground(Color.WHITE);
		btnAddStudent.setFocusPainted(false);
		btnAddStudent.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnAddStudent.setPreferredSize(new Dimension(170, 38));
		btnAddStudent.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		// Same destination as the "Enrollment" nav item
		btnAddStudent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFrame(new EnrollFrame());
			}
		});
		GridBagConstraints gbc_btn = new GridBagConstraints();
		gbc_btn.gridx = 1;
		gbc_btn.gridy = 0;
		actionPanel.add(btnAddStudent, gbc_btn);

		// ---- Main Table Card ----
		RoundedPanel tableCardPanel = new RoundedPanel(Color.WHITE, 20);
		tableCardPanel.setLayout(new BorderLayout(0, 15));
		tableCardPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		studentsBody.add(tableCardPanel, BorderLayout.CENTER);

		JLabel lblTableTitle = new JLabel("Enrolled Student Records");
		lblTableTitle.setFont(new Font("Arial", Font.BOLD, 18));
		lblTableTitle.setForeground(DARK_TEAL);
		tableCardPanel.add(lblTableTitle, BorderLayout.NORTH);

		String[] columns = {"Student ID", "Full Name", "Course / Program", "Year Level", "Enrollment Status", "Date Registered"};
		studentTableModel = new DefaultTableModel(columns, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		// Placeholder data
		studentTableModel.addRow(new Object[]{"2026-0001", "Juan Dela Cruz", "BS Computer Science", "1st Year", "Enrolled", "2026-09-01"});
		studentTableModel.addRow(new Object[]{"2026-0002", "Maria Santos", "BS Information Technology", "Ayoko na", "Enrolled", "2026-09-02"});
		studentTableModel.addRow(new Object[]{"2026-0003", "John Doe", "BS Business Administration", "2nd Year", "Pending", "2026-09-03"});
		studentTableModel.addRow(new Object[]{"2026-0004", "Anne Smith", "BS Computer Engineering", "3rd Year", "Enrolled", "2026-09-04"});

		JTable studentTable = new JTable(studentTableModel);
		studentTable.setFont(new Font("Arial", Font.PLAIN, 13));
		studentTable.setRowHeight(35);
		studentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
		studentTable.getTableHeader().setBackground(new Color(240, 240, 240));
		studentTable.getTableHeader().setForeground(DARK_TEAL);
		studentTable.getTableHeader().setReorderingAllowed(false);
		studentTable.setShowVerticalLines(false);
		studentTable.setGridColor(new Color(230, 230, 230));

		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
		for (int i = 0; i < studentTable.getColumnCount(); i++) {
			studentTable.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
		}

		JScrollPane scrollPane = new JScrollPane(studentTable);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getViewport().setBackground(Color.WHITE);
		tableCardPanel.add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * Shows the next frame (same position as this one) and closes this frame.
	 */
	private void openFrame(JFrame next) {
		next.setBounds(getBounds());
		next.setVisible(true);
		dispose();
	}

	/** Add student records dynamically (e.g. from MySQL). */
	public void addStudentRecord(String studentId, String name, String course, String yearLevel, String status, String date) {
		studentTableModel.addRow(new Object[]{studentId, name, course, yearLevel, status, date});
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