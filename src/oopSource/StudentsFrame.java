package oopSource;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Student Management screen (admin / registrar).
 *  - search by student ID or name (live)
 *  - click a row -> StudentDetailDialog (Modify Info / Delete Record)
 *  - "+ Add New Student" -> EnrollFrame
 * Data comes from StudentService.
 */
public class StudentsFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private final String loggedInUser;
	private final String loggedInRole;
	private DefaultTableModel studentTableModel;
	private JTable studentTable;
	private SearchField txtSearch;

	// Dark Teal Theme Colors (NavItem below uses these too)
	private static final Color DARK_TEAL = new Color(11, 55, 49);
	private static final Color HOVER_TEAL = new Color(20, 80, 72);
	private static final Color ACTIVE_NAV = new Color(40, 95, 87);
	private static final Color LIGHT_BG = new Color(235, 235, 235);
	private static final Color ACCENT_GREEN = new Color(38, 128, 98);

	private static final int STATUS_COL = 6;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Session.login("Admin", Roles.ADMIN);
					StudentsFrame frame = new StudentsFrame("Admin", Roles.ADMIN);
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Default constructor (uses whoever is logged in)
	 */
	public StudentsFrame() {
		this(Session.username(), Session.role());
	}

	/**
	 * Create the frame.
	 */
	public StudentsFrame(String username, String role) {
		this.loggedInUser = username;
		this.loggedInRole = role;
		setTitle("Rey University - Student Information Management");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1280, 720);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		contentPane.add(Sidebar.build(this, loggedInUser, loggedInRole, Roles.NAV_STUDENTS), BorderLayout.WEST);

		JPanel mainContentPanel = new JPanel();
		mainContentPanel.setBackground(LIGHT_BG);
		mainContentPanel.setLayout(new BorderLayout(0, 0));
		contentPane.add(mainContentPanel, BorderLayout.CENTER);

		mainContentPanel.add(buildTopBar(), BorderLayout.NORTH);

		// ---- Students Screen Body ----
		JPanel studentsBody = new JPanel();
		studentsBody.setOpaque(false);
		studentsBody.setBorder(new EmptyBorder(25, 25, 25, 25));
		studentsBody.setLayout(new BorderLayout(0, 20));
		mainContentPanel.add(studentsBody, BorderLayout.CENTER);

		studentsBody.add(buildHeaderToolBar(), BorderLayout.NORTH);
		studentsBody.add(buildTableCard(), BorderLayout.CENTER);

		loadTable();
	}

	// =================================================================
	// LAYOUT
	// =================================================================
	private JPanel buildTopBar() {
		JPanel topBar = new JPanel();
		topBar.setBackground(Color.WHITE);
		topBar.setPreferredSize(new Dimension(0, 70));
		topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
		topBar.setLayout(new GridBagLayout());

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
		return topBar;
	}

	/** Title on the left, search box + "Add New Student" on the right. */
	private JPanel buildHeaderToolBar() {
		JPanel headerToolBar = new JPanel();
		headerToolBar.setOpaque(false);
		headerToolBar.setLayout(new BorderLayout(10, 0));

		JLabel lblHeader = new JLabel("Student Management");
		lblHeader.setFont(new Font("Arial", Font.BOLD, 26));
		lblHeader.setForeground(DARK_TEAL);
		headerToolBar.add(lblHeader, BorderLayout.WEST);

		JPanel actionPanel = new JPanel();
		actionPanel.setOpaque(false);
		actionPanel.setLayout(new GridBagLayout());
		headerToolBar.add(actionPanel, BorderLayout.EAST);

		// Live search: reloads the table on every keystroke
		txtSearch = new SearchField("Search student ID or name...", 260, new Runnable() {
			public void run() {
				loadTable();
			}
		});
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
		btnAddStudent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Sidebar.navigate(StudentsFrame.this, new EnrollFrame());
			}
		});
		GridBagConstraints gbc_btn = new GridBagConstraints();
		gbc_btn.gridx = 1;
		gbc_btn.gridy = 0;
		actionPanel.add(btnAddStudent, gbc_btn);
		return headerToolBar;
	}

	private StudentsFrame.RoundedPanel buildTableCard() {
		RoundedPanel tableCardPanel = new RoundedPanel(Color.WHITE, 20);
		tableCardPanel.setLayout(new BorderLayout(0, 15));
		tableCardPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

		JPanel cardHeader = new JPanel(new BorderLayout());
		cardHeader.setOpaque(false);
		tableCardPanel.add(cardHeader, BorderLayout.NORTH);

		JLabel lblTableTitle = new JLabel("Enrollment Student Records");
		lblTableTitle.setFont(new Font("Arial", Font.BOLD, 18));
		lblTableTitle.setForeground(DARK_TEAL);
		cardHeader.add(lblTableTitle, BorderLayout.WEST);

		JLabel lblHint = new JLabel("Click a student to view, modify or delete");
		lblHint.setFont(new Font("Arial", Font.ITALIC, 12));
		lblHint.setForeground(Color.GRAY);
		cardHeader.add(lblHint, BorderLayout.EAST);

		String[] columns = {"Student ID", "First Name", "Last Name", "University Email", "Course", "Year Level", "Enrollment Status"};
		studentTableModel = new DefaultTableModel(columns, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		studentTable = new JTable(studentTableModel);
		studentTable.setFont(new Font("Arial", Font.PLAIN, 13));
		studentTable.setRowHeight(35);
		studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		studentTable.setSelectionBackground(new Color(225, 242, 236));
		studentTable.setSelectionForeground(Color.BLACK);
		studentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
		studentTable.getTableHeader().setBackground(new Color(240, 240, 240));
		studentTable.getTableHeader().setForeground(DARK_TEAL);
		studentTable.getTableHeader().setReorderingAllowed(false);
		studentTable.setShowVerticalLines(false);
		studentTable.setGridColor(new Color(230, 230, 230));

		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
		for (int i = 0; i < studentTable.getColumnCount() - 1; i++) {
			studentTable.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
		}
		studentTable.getColumnModel().getColumn(STATUS_COL).setCellRenderer(new StatusRenderer());

		// Click a row -> open the info / modify / delete pop-up
		studentTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = studentTable.rowAtPoint(e.getPoint());
				if (row < 0) return;
				String id = String.valueOf(studentTableModel.getValueAt(row, 0));
				StudentService.StudentRecord record = StudentService.findById(id);
				if (record != null) {
					openStudentDialog(record);
				}
			}
		});
		studentTable.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				boolean overRow = studentTable.rowAtPoint(e.getPoint()) >= 0;
				studentTable.setCursor(overRow ? new Cursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
			}
		});

		JScrollPane scrollPane = new JScrollPane(studentTable);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getViewport().setBackground(Color.WHITE);
		tableCardPanel.add(scrollPane, BorderLayout.CENTER);
		return tableCardPanel;
	}

	// =================================================================
	// LOGIC
	// =================================================================

	/** Fills the table with the students that match the search box. */
	private void loadTable() {
		studentTableModel.setRowCount(0);
		List<StudentService.StudentRecord> students = StudentService.search(txtSearch.getQuery(), EnrollmentService.ENROLLED);
		for (StudentService.StudentRecord s : students) {
			studentTableModel.addRow(new Object[] {
					s.studentId, s.firstName, s.lastName, s.email, s.course, s.yearLevel, s.status });
		}
	}

	private void openStudentDialog(StudentService.StudentRecord record) {
		StudentDetailDialog dialog = new StudentDetailDialog(this, record, new Runnable() {
			public void run() {
				loadTable(); // refresh after modify / delete
			}
		});
		dialog.setVisible(true);
	}

	/** Colors "Enrolled" green and "Pending" amber. */
	private static class StatusRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			boolean enrolled = EnrollmentService.ENROLLED.equals(String.valueOf(value));
			setHorizontalAlignment(SwingConstants.LEFT);
			setFont(new Font("Arial", Font.BOLD, 13));
			setForeground(enrolled ? TuitionFrame.PAID_GREEN : new Color(156, 101, 0));
			return this;
		}
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
