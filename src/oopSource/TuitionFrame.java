package oopSource;

import java.awt.BorderLayout;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
import javax.swing.table.TableRowSorter;

public class TuitionFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private String loggedInUser;
	private String loggedInRole;
	private DefaultTableModel tuitionTableModel;
	private JTable tuitionTable;
	private TableRowSorter<DefaultTableModel> sorter;
	private JTextField txtSearch;

	private static final String SEARCH_HINT = " Search student ID or name...";

	// Theme colors (package-private so PaymentFrame can reuse them)
	static final Color DARK_TEAL = new Color(11, 55, 49);
	static final Color LIGHT_BG = new Color(235, 235, 235);
	static final Color ACCENT_GREEN = new Color(38, 128, 98);
	static final Color PAID_GREEN = new Color(30, 130, 76);
	static final Color PAID_BG = new Color(223, 245, 232);
	static final Color UNPAID_RED = new Color(192, 57, 43);
	static final Color UNPAID_BG = new Color(252, 228, 225);

	/** Tuition rate: total due = units x RATE_PER_UNIT. */
	public static final int RATE_PER_UNIT = 300;
	static final String PESO = "\u20B1";
	static final DecimalFormat MONEY = new DecimalFormat("#,##0.00");

	// =================================================================
	// SHARED TUITION DATA (kept in memory so it survives frame changes)
	// Replace with MySQL queries later.
	// =================================================================
	public static class TuitionRecord {
		public final String studentId;
		public final String name;
		public final String course;
		public final String yearLevel;
		public final int units;

		public boolean paid;
		public String paymentMethod = "";
		public double amountReceived;
		public double change;
		public String datePaid = "";

		public TuitionRecord(String studentId, String name, String course, String yearLevel, int units) {
			this.studentId = studentId;
			this.name = name;
			this.course = course;
			this.yearLevel = yearLevel;
			this.units = units;
		}

		public double getTotalDue() {
			return units * RATE_PER_UNIT;
		}

		public String getStatus() {
			return paid ? "Paid" : "Unpaid";
		}
	}

	private static final List<TuitionRecord> RECORDS = new ArrayList<TuitionRecord>();

	static {
		// Placeholder data
		RECORDS.add(new TuitionRecord("RU-2026-0001", "Santos, Maria A.", "BSIT", "1st Year", 21));
		RECORDS.add(new TuitionRecord("RU-2026-0002", "Dela Cruz, Juan", "BSCS", "1st Year", 24));
		RECORDS.add(new TuitionRecord("RU-2026-0003", "Doe, John", "BSBA", "2nd Year", 18));
		RECORDS.add(new TuitionRecord("RU-2026-0004", "Smith, Anne", "BSCpE", "3rd Year", 21));
		RECORDS.get(3).paid = true;
		RECORDS.get(3).paymentMethod = "Cash";
		RECORDS.get(3).amountReceived = 6500;
		RECORDS.get(3).change = 200;
		RECORDS.get(3).datePaid = "2026-09-04";
	}

	/**
	 * Call this from the admin dashboard when an enrollment is APPROVED.
	 * The student then appears in the Tuition table as "Unpaid".
	 */
	public static void addApprovedStudent(String studentId, String name, String course, String yearLevel, int units) {
		if (findById(studentId) == null) {
			RECORDS.add(new TuitionRecord(studentId, name, course, yearLevel, units));
		}
	}

	public static List<TuitionRecord> getRecords() {
		return Collections.unmodifiableList(RECORDS);
	}

	public static TuitionRecord findById(String studentId) {
		for (TuitionRecord r : RECORDS) {
			if (r.studentId.equals(studentId)) return r;
		}
		return null;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TuitionFrame frame = new TuitionFrame("Cashier01", "cashier");
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
	public TuitionFrame() {
		this("Cashier01", "Cashier");
	}

	/**
	 * Create the frame.
	 */
	public TuitionFrame(String username, String role) {
		this.loggedInUser = username;
		this.loggedInRole = role;
		setTitle("Rey University - Tuition & Payments");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1280, 720);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		// ---- Left sidebar (shared) ----
		contentPane.add(buildSidebar(this, loggedInUser, role), BorderLayout.WEST);

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

		// ---- Tuition Screen Body ----
		JPanel tuitionBody = new JPanel();
		tuitionBody.setOpaque(false);
		tuitionBody.setBorder(new EmptyBorder(25, 25, 25, 25));
		tuitionBody.setLayout(new BorderLayout(0, 20));
		mainContentPanel.add(tuitionBody, BorderLayout.CENTER);

		// ---- Header + search (no Add button) ----
		JPanel headerToolBar = new JPanel();
		headerToolBar.setOpaque(false);
		headerToolBar.setLayout(new BorderLayout(10, 0));
		tuitionBody.add(headerToolBar, BorderLayout.NORTH);

		JLabel lblHeader = new JLabel("Tuition & Payments");
		lblHeader.setFont(new Font("Arial", Font.BOLD, 26));
		lblHeader.setForeground(DARK_TEAL);
		headerToolBar.add(lblHeader, BorderLayout.WEST);

		txtSearch = new JTextField(SEARCH_HINT);
		txtSearch.setFont(new Font("Arial", Font.PLAIN, 13));
		txtSearch.setPreferredSize(new Dimension(260, 38));
		txtSearch.setForeground(Color.GRAY);
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
			public void insertUpdate(DocumentEvent e) { applySearch(); }
			public void removeUpdate(DocumentEvent e) { applySearch(); }
			public void changedUpdate(DocumentEvent e) { applySearch(); }
		});
		headerToolBar.add(txtSearch, BorderLayout.EAST);

		// ---- Main Table Card ----
		StudentsFrame.RoundedPanel tableCardPanel = new StudentsFrame.RoundedPanel(Color.WHITE, 20);
		tableCardPanel.setLayout(new BorderLayout(0, 15));
		tableCardPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		tuitionBody.add(tableCardPanel, BorderLayout.CENTER);

		JPanel cardHeader = new JPanel(new BorderLayout());
		cardHeader.setOpaque(false);
		tableCardPanel.add(cardHeader, BorderLayout.NORTH);

		JLabel lblTableTitle = new JLabel("Approved Students - Tuition Records");
		lblTableTitle.setFont(new Font("Arial", Font.BOLD, 18));
		lblTableTitle.setForeground(DARK_TEAL);
		cardHeader.add(lblTableTitle, BorderLayout.WEST);

		JLabel lblHint = new JLabel("Click a student to process payment");
		lblHint.setFont(new Font("Arial", Font.ITALIC, 12));
		lblHint.setForeground(Color.GRAY);
		cardHeader.add(lblHint, BorderLayout.EAST);

		String[] columns = {"Student ID", "Full Name", "Course / Program", "Year Level", "Units", "Total Due", "Payment Status"};
		tuitionTableModel = new DefaultTableModel(columns, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		loadTable();

		tuitionTable = new JTable(tuitionTableModel);
		tuitionTable.setFont(new Font("Arial", Font.PLAIN, 13));
		tuitionTable.setRowHeight(38);
		tuitionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tuitionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
		tuitionTable.getTableHeader().setBackground(new Color(240, 240, 240));
		tuitionTable.getTableHeader().setForeground(DARK_TEAL);
		tuitionTable.getTableHeader().setReorderingAllowed(false);
		tuitionTable.setShowVerticalLines(false);
		tuitionTable.setGridColor(new Color(230, 230, 230));
		tuitionTable.setSelectionBackground(new Color(225, 242, 236));
		tuitionTable.setSelectionForeground(Color.BLACK);

		sorter = new TableRowSorter<DefaultTableModel>(tuitionTableModel);
		tuitionTable.setRowSorter(sorter);

		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
		for (int i = 0; i < tuitionTable.getColumnCount() - 1; i++) {
			tuitionTable.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
		}
		tuitionTable.getColumnModel().getColumn(6).setCellRenderer(new StatusRenderer());

		// Click a row -> open the payment wireframe for that student
		tuitionTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int viewRow = tuitionTable.rowAtPoint(e.getPoint());
				if (viewRow < 0) return;
				int modelRow = tuitionTable.convertRowIndexToModel(viewRow);
				String id = String.valueOf(tuitionTableModel.getValueAt(modelRow, 0));
				TuitionRecord rec = findById(id);
				if (rec != null) {
					navigate(TuitionFrame.this, new PaymentFrame(loggedInUser, rec, loggedInRole));
				}
			}
		});
		tuitionTable.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				boolean overRow = tuitionTable.rowAtPoint(e.getPoint()) >= 0;
				tuitionTable.setCursor(overRow ? new Cursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
			}
		});

		JScrollPane scrollPane = new JScrollPane(tuitionTable);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getViewport().setBackground(Color.WHITE);
		tableCardPanel.add(scrollPane, BorderLayout.CENTER);
	}

	// =================================================================
	// LOGIC
	// =================================================================
	private void loadTable() {
		tuitionTableModel.setRowCount(0);
		for (TuitionRecord r : RECORDS) {
			tuitionTableModel.addRow(new Object[] {
					r.studentId, r.name, r.course, r.yearLevel, r.units,
					PESO + MONEY.format(r.getTotalDue()), r.getStatus()
			});
		}
	}

	private void applySearch() {
		String text = txtSearch.getText().trim();
		if (text.isEmpty() || text.equals(SEARCH_HINT.trim())) {
			sorter.setRowFilter(null);
		} else {
			sorter.setRowFilter(RowFilter.<DefaultTableModel, Integer>regexFilter(
					"(?i)" + Pattern.quote(text), 0, 1));
		}
	}

	/** Colors the Paid / Unpaid text. */
	private static class StatusRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			boolean paid = "Paid".equals(String.valueOf(value));
			setHorizontalAlignment(SwingConstants.LEFT);
			setFont(new Font("Arial", Font.BOLD, 13));
			setForeground(paid ? PAID_GREEN : UNPAID_RED);
			if (!isSelected) setBackground(paid ? PAID_BG : UNPAID_BG);
			return this;
		}
	}

	// =================================================================
	// SHARED HELPERS (also used by PaymentFrame)
	// =================================================================

	/** Shows the next frame (same position as the current one) and closes the current one. */
	static void navigate(JFrame from, JFrame next) {
		next.setBounds(from.getBounds());
		next.setVisible(true);
		from.dispose();
	}

	/** Builds the dark teal sidebar with "Tuition & Payments" highlighted. */
	static JPanel buildSidebar(final JFrame owner, final String user, final String role) {
		JPanel sidebarPanel = new JPanel();
		sidebarPanel.setBackground(DARK_TEAL);
		sidebarPanel.setPreferredSize(new Dimension(280, 720));
		sidebarPanel.setLayout(new BorderLayout(0, 0));

		// ---- Logo + Title ----
		JPanel logoPanel = new JPanel();
		logoPanel.setOpaque(false);
		logoPanel.setBorder(new EmptyBorder(20, 15, 20, 15));
		logoPanel.setLayout(new GridBagLayout());
		sidebarPanel.add(logoPanel, BorderLayout.NORTH);

		JLabel lblLogo = new JLabel("");
		lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
		URL imgUrl = TuitionFrame.class.getResource("/RUlogo (1).png");
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
				navigate(owner, new DashboardFrame(user, role ));
			}
		});
		navContainer.add(navDashboard);

		StudentsFrame.NavItem navStudents = new StudentsFrame.NavItem("Students", false);
		navStudents.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				navigate(owner, new StudentsFrame(user, role));
			}
		});
		navContainer.add(navStudents);

		StudentsFrame.NavItem navEnrollment = new StudentsFrame.NavItem("Enrollment", false);
		navEnrollment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				navigate(owner, new EnrollmentStudent(user));
			}
		});
		navContainer.add(navEnrollment);

		StudentsFrame.NavItem navCourses = new StudentsFrame.NavItem("Courses & Schedules", false);
		navCourses.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				navigate(owner, new CoursesFrame(user, role));
			}
		});
		navContainer.add(navCourses);

		// Tuition & Payments (current section)
		StudentsFrame.NavItem navTuition = new StudentsFrame.NavItem("Tuition & Payments", true);
		navTuition.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// From the payment screen this returns to the tuition list
				if (!(owner instanceof TuitionFrame)) {
					navigate(owner, new TuitionFrame(user, role));
				}
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
		URL pfpUrl = TuitionFrame.class.getResource("/Profile1.png");
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

		JLabel lblUsername = new JLabel(user);
		lblUsername.setForeground(Color.WHITE);
		lblUsername.setFont(new Font("Arial", Font.BOLD, 15));
		GridBagConstraints gbc_lblUsername = new GridBagConstraints();
		gbc_lblUsername.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblUsername.weightx = 1.0;
		gbc_lblUsername.insets = new Insets(0, 0, 2, 0);
		gbc_lblUsername.gridx = 1;
		gbc_lblUsername.gridy = 0;
		userProfilePanel.add(lblUsername, gbc_lblUsername);

		JLabel lblRole = new JLabel(role);
		lblRole.setForeground(new Color(180, 200, 195));
		lblRole.setFont(new Font("Arial", Font.PLAIN, 12));
		GridBagConstraints gbc_lblRole = new GridBagConstraints();
		gbc_lblRole.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblRole.weightx = 1.0;
		gbc_lblRole.gridx = 1;
		gbc_lblRole.gridy = 1;
		userProfilePanel.add(lblRole, gbc_lblRole);

		return sidebarPanel;
	}
}