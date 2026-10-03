package oopSource;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

public class CoursesFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private String loggedInUser;

	private DefaultTableModel courseTableModel;
	private JTable courseTable;
	private TableRowSorter<DefaultTableModel> sorter;
	private JTextField txtSearch;
	private JComboBox<String> cmbDepartment;

	// Course codes the user pressed "Add" on (these get enrolled)
	private final Set<String> selectedCodes = new HashSet<String>();

	private JLabel tabCourses;
	private JLabel tabSchedules;
	private CardLayout cardLayout;
	private JPanel cardHolder;
	private String loggedInRole;

	private static final int ACTIONS_COL = 4;
	private static final String SEARCH_HINT = "Search course code or title...";

	// Dark Teal Theme Colors
	private static final Color DARK_TEAL = new Color(11, 55, 49);
	private static final Color LIGHT_BG = new Color(235, 235, 235);
	private static final Color ACCENT_GREEN = new Color(38, 128, 98);
	private static final Color DELETE_RED = new Color(192, 57, 43);
	private static final Color ADDED_GRAY = new Color(150, 170, 165);
	private static final Color SELECTED_ROW = new Color(225, 242, 236);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CoursesFrame frame = new CoursesFrame("Registrar","registar");
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
	public CoursesFrame() {
		this("registrar01","registrar");
	}

	/**
	 * Create the frame.
	 */
	public CoursesFrame(String username, String role) {
		this.loggedInUser = username;
		this.loggedInRole = role;
		setTitle("Rey University - Courses & Schedules");
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
		logoPanel.setLayout(new GridBagLayout());
		sidebarPanel.add(logoPanel, BorderLayout.NORTH);

		JLabel lblLogo = new JLabel("");
		lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
		URL imgUrl = CoursesFrame.class.getResource("/RUlogo (1).png");
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

		StudentsFrame.NavItem navDashboard = new StudentsFrame.NavItem("Dashboard", false);
		navDashboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFrame(new DashboardFrame(loggedInUser, loggedInRole));
			}
		});
		navContainer.add(navDashboard);

		StudentsFrame.NavItem navStudents = new StudentsFrame.NavItem("Students", false);
		navStudents.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFrame(new StudentsFrame(loggedInUser, loggedInRole));
			}
		});
		navContainer.add(navStudents);

		StudentsFrame.NavItem navEnrollment = new StudentsFrame.NavItem("Enrollment", false);
		navEnrollment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFrame(new EnrollmentStudent(loggedInUser));
			}
		});
		navContainer.add(navEnrollment);

		// Courses & Schedules (current page)
		StudentsFrame.NavItem navCourses = new StudentsFrame.NavItem("Courses & Schedules", true);
		navContainer.add(navCourses);

		StudentsFrame.NavItem navTuition = new StudentsFrame.NavItem("Tuition & Payments", false);
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

		// ---- Courses Screen Body ----
		JPanel coursesBody = new JPanel();
		coursesBody.setOpaque(false);
		coursesBody.setBorder(new EmptyBorder(25, 25, 25, 25));
		coursesBody.setLayout(new BorderLayout(0, 15));
		mainContentPanel.add(coursesBody, BorderLayout.CENTER);

		// ---- Header + Tabs + Enroll Subjects button ----
		JPanel headerPanel = new JPanel();
		headerPanel.setOpaque(false);
		headerPanel.setLayout(new BorderLayout(10, 8));
		coursesBody.add(headerPanel, BorderLayout.NORTH);

		JLabel lblHeader = new JLabel("Subjects & Schedules");
		lblHeader.setFont(new Font("Arial", Font.BOLD, 26));
		lblHeader.setForeground(DARK_TEAL);
		headerPanel.add(lblHeader, BorderLayout.NORTH);

		JPanel tabsPanel = new JPanel();
		tabsPanel.setOpaque(false);
		tabsPanel.setLayout(new GridLayout(1, 2, 10, 0));
		tabsPanel.setPreferredSize(new Dimension(300, 40));

		tabCourses = new JLabel("Subjects", SwingConstants.CENTER);
		tabSchedules = new JLabel("Schedules", SwingConstants.CENTER);
		tabCourses.setCursor(new Cursor(Cursor.HAND_CURSOR));
		tabSchedules.setCursor(new Cursor(Cursor.HAND_CURSOR));
		tabCourses.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				showTab("courses");
			}
		});
		tabSchedules.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				showTab("schedules");
			}
		});
		tabsPanel.add(tabCourses);
		tabsPanel.add(tabSchedules);

		JPanel tabsWrapper = new JPanel(new BorderLayout());
		tabsWrapper.setOpaque(false);
		tabsWrapper.add(tabsPanel, BorderLayout.WEST);
		headerPanel.add(tabsWrapper, BorderLayout.CENTER);

		JButton btnEnroll = new JButton(" ");
		btnEnroll.setFont(new Font("Arial", Font.BOLD, 13));
		btnEnroll.setBackground(ACCENT_GREEN);
		btnEnroll.setForeground(Color.WHITE);
		btnEnroll.setFocusPainted(false);
		btnEnroll.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnEnroll.setPreferredSize(new Dimension(170, 38));
		btnEnroll.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		btnEnroll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enrollSelectedSubjects();
			}
		});
		JPanel enrollWrapper = new JPanel(new GridBagLayout());
		enrollWrapper.setOpaque(false);
		enrollWrapper.add(btnEnroll);
		headerPanel.add(enrollWrapper, BorderLayout.EAST);

		// ---- Card holder (Courses / Schedules) ----
		cardLayout = new CardLayout();
		cardHolder = new JPanel(cardLayout);
		cardHolder.setOpaque(false);
		coursesBody.add(cardHolder, BorderLayout.CENTER);

		cardHolder.add(buildCoursesCard(), "courses");
		cardHolder.add(buildSchedulesCard(), "schedules");
		showTab("courses");
	}

	// =================================================================
	// COURSES TAB
	// =================================================================
	private JPanel buildCoursesCard() {
		StudentsFrame.RoundedPanel card = new StudentsFrame.RoundedPanel(Color.WHITE, 20);
		card.setLayout(new BorderLayout(0, 15));
		card.setBorder(new EmptyBorder(20, 20, 20, 20));

		// ---- Search + department filter ----
		JPanel filterRow = new JPanel(new BorderLayout(10, 0));
		filterRow.setOpaque(false);
		card.add(filterRow, BorderLayout.NORTH);

		txtSearch = new JTextField(SEARCH_HINT);
		txtSearch.setFont(new Font("Arial", Font.PLAIN, 13));
		txtSearch.setForeground(Color.GRAY);
		txtSearch.setPreferredSize(new Dimension(0, 38));
		txtSearch.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (txtSearch.getText().equals(SEARCH_HINT)) {
					txtSearch.setText("");
					txtSearch.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (txtSearch.getText().trim().isEmpty()) {
					txtSearch.setText(SEARCH_HINT);
					txtSearch.setForeground(Color.GRAY);
				}
			}
		});
		txtSearch.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { applyFilters(); }
			public void removeUpdate(DocumentEvent e) { applyFilters(); }
			public void changedUpdate(DocumentEvent e) { applyFilters(); }
		});
		filterRow.add(txtSearch, BorderLayout.CENTER);

		cmbDepartment = new JComboBox<String>(new String[] {"All Departments", "CS", "IT", "Math", "English", "PE"});
		cmbDepartment.setFont(new Font("Arial", Font.PLAIN, 13));
		cmbDepartment.setPreferredSize(new Dimension(200, 38));
		cmbDepartment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyFilters();
			}
		});
		filterRow.add(cmbDepartment, BorderLayout.EAST);

		// ---- Table ----
		String[] columns = {"Course Code", "Title", "Units", "Department", "Actions"};
		courseTableModel = new DefaultTableModel(columns, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		// Placeholder data
		addCourseRow("CCS101", "Introduction to Computing", 3, "CS");
		addCourseRow("IT102", "Web Development", 3, "IT");
		addCourseRow("MATH101", "College Algebra", 3, "Math");
		addCourseRow("ENG101", "English for Academic Purposes", 3, "English");
		addCourseRow("PE101", "Physical Education", 2, "PE");
		addCourseRow("CCS102", "Computer Programming 1", 3, "CS");
		addCourseRow("IT103", "Networking Fundamentals", 3, "IT");
		addCourseRow("MATH102", "Trigonometry", 3, "Math");
		addCourseRow("ENG102", "Purposive Communication", 3, "English");
		addCourseRow("PE102", "Physical Education 2", 2, "PE");
		addCourseRow("CCS103", "Discrete Mathematics", 3, "CS");
		addCourseRow("IT104", "Database Management", 3, "IT");

		courseTable = new JTable(courseTableModel);
		courseTable.setFont(new Font("Arial", Font.PLAIN, 13));
		courseTable.setRowHeight(48);
		courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		courseTable.setRowSelectionAllowed(false);
		courseTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
		courseTable.getTableHeader().setBackground(new Color(240, 240, 240));
		courseTable.getTableHeader().setForeground(DARK_TEAL);
		courseTable.getTableHeader().setReorderingAllowed(false);
		courseTable.setShowVerticalLines(false);
		courseTable.setGridColor(new Color(230, 230, 230));

		sorter = new TableRowSorter<DefaultTableModel>(courseTableModel);
		courseTable.setRowSorter(sorter);

		courseTable.getColumnModel().getColumn(0).setCellRenderer(new CourseCellRenderer(SwingConstants.LEFT));
		courseTable.getColumnModel().getColumn(1).setCellRenderer(new CourseCellRenderer(SwingConstants.LEFT));
		courseTable.getColumnModel().getColumn(2).setCellRenderer(new CourseCellRenderer(SwingConstants.CENTER));
		courseTable.getColumnModel().getColumn(3).setCellRenderer(new CourseCellRenderer(SwingConstants.LEFT));
		courseTable.getColumnModel().getColumn(ACTIONS_COL).setCellRenderer(new ActionsRenderer());

		courseTable.getColumnModel().getColumn(0).setPreferredWidth(110);
		courseTable.getColumnModel().getColumn(1).setPreferredWidth(300);
		courseTable.getColumnModel().getColumn(2).setPreferredWidth(60);
		courseTable.getColumnModel().getColumn(3).setPreferredWidth(110);
		courseTable.getColumnModel().getColumn(ACTIONS_COL).setPreferredWidth(190);

		// Click handling for the Add / Delete buttons inside the Actions column
		courseTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int viewRow = courseTable.rowAtPoint(e.getPoint());
				int viewCol = courseTable.columnAtPoint(e.getPoint());
				if (viewRow < 0 || viewCol != ACTIONS_COL) return;

				Rectangle cell = courseTable.getCellRect(viewRow, viewCol, false);
				boolean addClicked = e.getX() < cell.x + cell.width / 2;
				int modelRow = courseTable.convertRowIndexToModel(viewRow);
				if (addClicked) {
					addCourse(modelRow);
				} else {
					deleteCourse(modelRow);
				}
			}
		});
		courseTable.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int col = courseTable.columnAtPoint(e.getPoint());
				courseTable.setCursor(col == ACTIONS_COL ? new Cursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
			}
		});

		// Scrollable rows (replaces the 1 2 3 pagination)
		JScrollPane scrollPane = new JScrollPane(courseTable,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
		scrollPane.getViewport().setBackground(Color.WHITE);
		card.add(scrollPane, BorderLayout.CENTER);

		return card;
	}

	// =================================================================
	// SCHEDULES TAB
	// =================================================================
	private JPanel buildSchedulesCard() {
		StudentsFrame.RoundedPanel card = new StudentsFrame.RoundedPanel(Color.WHITE, 20);
		card.setLayout(new BorderLayout(0, 15));
		card.setBorder(new EmptyBorder(20, 20, 20, 20));

		String[] cols = {"Course Code", "Day", "Time", "Room"};
		DefaultTableModel model = new DefaultTableModel(cols, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		// Placeholder data
		model.addRow(new Object[] {"CCS101", "Mon / Wed", "8:00 - 9:30 AM", "Room 101"});
		model.addRow(new Object[] {"IT102", "Tue / Thu", "10:00 - 11:30 AM", "Lab 2"});
		model.addRow(new Object[] {"MATH101", "Mon / Wed", "1:00 - 2:30 PM", "Room 204"});
		model.addRow(new Object[] {"ENG101", "Tue / Thu", "1:00 - 2:30 PM", "Room 110"});
		model.addRow(new Object[] {"PE101", "Fri", "8:00 - 10:00 AM", "Gym"});

		JTable table = new JTable(model);
		table.setFont(new Font("Arial", Font.PLAIN, 13));
		table.setRowHeight(40);
		table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
		table.getTableHeader().setBackground(new Color(240, 240, 240));
		table.getTableHeader().setForeground(DARK_TEAL);
		table.getTableHeader().setReorderingAllowed(false);
		table.setShowVerticalLines(false);
		table.setGridColor(new Color(230, 230, 230));

		JScrollPane sp = new JScrollPane(table);
		sp.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
		sp.getViewport().setBackground(Color.WHITE);
		card.add(sp, BorderLayout.CENTER);
		return card;
	}

	// =================================================================
	// LOGIC
	// =================================================================
	private void addCourseRow(String code, String title, int units, String dept) {
		courseTableModel.addRow(new Object[] {code, title, units, dept, ""});
	}

	/** Add button: marks the course to be included in the enrollment. */
	private void addCourse(int modelRow) {
		String code = String.valueOf(courseTableModel.getValueAt(modelRow, 0));
		selectedCodes.add(code);
		courseTable.repaint();
	}

	/** Delete button: removes the course row from the table. */
	private void deleteCourse(int modelRow) {
		String code = String.valueOf(courseTableModel.getValueAt(modelRow, 0));
		String title = String.valueOf(courseTableModel.getValueAt(modelRow, 1));
		int choice = JOptionPane.showConfirmDialog(this,
				"Delete " + code + " - " + title + "?", "Delete Course",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (choice == JOptionPane.YES_OPTION) {
			selectedCodes.remove(code);
			courseTableModel.removeRow(modelRow);
			// TODO: also delete from MySQL
		}
	}

	/** Collects every added course, then opens the summary frame. */
	private void enrollSelectedSubjects() {
		List<Object[]> selected = new ArrayList<Object[]>();
		for (int i = 0; i < courseTableModel.getRowCount(); i++) {
			String code = String.valueOf(courseTableModel.getValueAt(i, 0));
			if (selectedCodes.contains(code)) {
				selected.add(new Object[] {
						courseTableModel.getValueAt(i, 0),
						courseTableModel.getValueAt(i, 1),
						courseTableModel.getValueAt(i, 2),
						courseTableModel.getValueAt(i, 3)
				});
			}
		}

		if (selected.isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"Please press \"Add\" on at least one course first.",
					"No subjects selected", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		EnrollmentSummaryFrame summary = new EnrollmentSummaryFrame(loggedInUser, selected, this, loggedInRole);
		summary.setBounds(getBounds());
		summary.setVisible(true);
		setVisible(false); // hidden (not disposed) so the Back button can restore it
	}

	/** Applies the search text and department filter together. */
	private void applyFilters() {
		List<RowFilter<DefaultTableModel, Integer>> filters = new ArrayList<RowFilter<DefaultTableModel, Integer>>();

		String text = txtSearch.getText().trim();
		if (!text.isEmpty() && !text.equals(SEARCH_HINT)) {
			filters.add(RowFilter.<DefaultTableModel, Integer>regexFilter("(?i)" + Pattern.quote(text), 0, 1));
		}

		String dept = (String) cmbDepartment.getSelectedItem();
		if (dept != null && !dept.equals("All Departments")) {
			filters.add(RowFilter.<DefaultTableModel, Integer>regexFilter("^" + Pattern.quote(dept) + "$", 3));
		}

		sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
	}

	private void showTab(String name) {
		cardLayout.show(cardHolder, name);
		styleTab(tabCourses, name.equals("courses"));
		styleTab(tabSchedules, name.equals("schedules"));
	}

	private void styleTab(JLabel tab, boolean active) {
		tab.setFont(new Font("Arial", active ? Font.BOLD : Font.PLAIN, 16));
		tab.setForeground(active ? DARK_TEAL : Color.GRAY);
		tab.setBorder(BorderFactory.createMatteBorder(0, 0, active ? 3 : 1, 0,
				active ? ACCENT_GREEN : new Color(220, 220, 220)));
	}

	/**
	 * Shows the next frame (same position as this one) and closes this frame.
	 */
	private void openFrame(JFrame next) {
		next.setBounds(getBounds());
		next.setVisible(true);
		dispose();
	}

	private static JButton makeActionButton(String text, Color bg) {
		JButton b = new JButton(text);
		b.setFont(new Font("Arial", Font.BOLD, 12));
		b.setBackground(bg);
		b.setForeground(Color.WHITE);
		b.setOpaque(true);
		b.setBorderPainted(false);
		b.setFocusable(false);
		return b;
	}

	// =================================================================
	// TABLE RENDERERS
	// =================================================================

	/** Plain cell renderer that tints rows that have been added. */
	private class CourseCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		private final int align;

		CourseCellRenderer(int align) {
			this.align = align;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, false, false, row, column);
			setHorizontalAlignment(align);
			setBorder(new EmptyBorder(0, 12, 0, 12));
			String code = String.valueOf(table.getValueAt(row, 0));
			setBackground(selectedCodes.contains(code) ? SELECTED_ROW : Color.WHITE);
			return this;
		}
	}

	/** Draws the Add + Delete buttons in the Actions column. */
	private class ActionsRenderer extends JPanel implements TableCellRenderer {
		private static final long serialVersionUID = 1L;
		private final JButton btnAdd = makeActionButton("Add", ACCENT_GREEN);
		private final JButton btnDelete = makeActionButton("Delete", DELETE_RED);

		ActionsRenderer() {
			setLayout(new GridLayout(1, 2, 6, 0));
			setBorder(new EmptyBorder(8, 8, 8, 8));
			add(btnAdd);
			add(btnDelete);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			String code = String.valueOf(table.getValueAt(row, 0));
			boolean added = selectedCodes.contains(code);
			btnAdd.setText(added ? "Added" : "Add");
			btnAdd.setBackground(added ? ADDED_GRAY : ACCENT_GREEN);
			setBackground(added ? SELECTED_ROW : Color.WHITE);
			return this;
		}
	}
}