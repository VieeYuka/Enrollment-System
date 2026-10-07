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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * Enrollment screen (admin / registrar).
 *
 * Two views inside the same frame:
 *   1. LIST   - table of students who applied (like the Courses screen).
 *               The last column has a "Review Application" button.
 *   2. REVIEW - the student's information + the subjects they added,
 *               with [Cancel] (back to the list) and [Approved] (status -> Enrolled).
 *
 * All data comes from EnrollmentService, so MySQL can be added there
 * without touching this frame.
 */
public class EnrollmentStudent extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final String VIEW_LIST = "list";
	private static final String VIEW_REVIEW = "review";
	

	// Table columns
	private static final int STATUS_COL = 4;
	private static final int ACTIONS_COL = 5;

	// Dark Teal Theme Colors
	private static final Color DARK_TEAL = new Color(11, 55, 49);
	private static final Color LIGHT_BG = new Color(235, 235, 235);
	private static final Color ACCENT_GREEN = new Color(38, 128, 98);
	private static final Color DISABLED_GRAY = new Color(170, 178, 176);
	private static final Color DENY_RED = new Color(192, 57, 43);
	private static final Color PENDING_AMBER = new Color(156, 101, 0);

	private final String loggedInUser;
	private final String loggedInRole;

	// frame skeleton
	private JLabel lblHeader;
	private CardLayout cardLayout;
	private JPanel cardHolder;

	// list view
	private DefaultTableModel tableModel;
	private JTable table;
	private SearchField txtSearch;
	

	// review view
	private JLabel lblStudentIdValue;
	private JLabel lblNameValue;
	private JLabel lblCourseValue;
	private JLabel lblYearValue;
	private JLabel lblStatusValue;
	private JLabel lblTotals;
	private JPanel selectedListPanel;
	private JButton btnApprove;
	private JButton btnDeny;
	private String reviewingKey; // student whose application is open in the review view

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Session.login("Admin", Roles.ADMIN);
					EnrollmentStudent frame = new EnrollmentStudent("Admin", Roles.ADMIN);
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
	public EnrollmentStudent() {
		this(Session.username(), Session.role());
	}

	/**
	 * Create the frame.
	 */
	public EnrollmentStudent(String username) {
		this(username, Session.role());
	}

	/**
	 * Create the frame.
	 */
	public EnrollmentStudent(String username, String role) {
		this.loggedInUser = username;
		this.loggedInRole = role;

		setTitle("Rey University - Enrollment Processing");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1280, 720);

		JPanel contentPane = new JPanel(new BorderLayout(0, 0));
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);

		contentPane.add(Sidebar.build(this, loggedInUser, loggedInRole, Roles.NAV_ENROLLMENT), BorderLayout.WEST);

		JPanel mainContentPanel = new JPanel(new BorderLayout(0, 0));
		mainContentPanel.setBackground(LIGHT_BG);
		contentPane.add(mainContentPanel, BorderLayout.CENTER);

		mainContentPanel.add(buildTopBar(), BorderLayout.NORTH);

		// ---- Body: header + (list | review) ----
		JPanel body = new JPanel(new BorderLayout(0, 15));
		body.setOpaque(false);
		body.setBorder(new EmptyBorder(25, 25, 25, 25));
		mainContentPanel.add(body, BorderLayout.CENTER);

		lblHeader = new JLabel();
		lblHeader.setFont(new Font("Arial", Font.BOLD, 26));
		lblHeader.setForeground(DARK_TEAL);
		body.add(lblHeader, BorderLayout.NORTH);

		cardLayout = new CardLayout();
		cardHolder = new JPanel(cardLayout);
		cardHolder.setOpaque(false);
		body.add(cardHolder, BorderLayout.CENTER);
		cardHolder.add(buildListView(), VIEW_LIST);
		cardHolder.add(buildReviewView(), VIEW_REVIEW);

		loadTable();
		showList();

		// New applications are submitted from other screens/computers, so keep the list current.
		refreshTimer = new Timer(3000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				autoRefresh();
			}
		});
		refreshTimer.start();
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				autoRefresh();
			}
		});
	}

	private Timer refreshTimer;

	/** Reloads the pending list only when it changed (and only while the list is showing). */
	private void autoRefresh() {
		if (reviewingKey != null) {
			return;
		}
		List<EnrollmentService.Enrollment> latest =
				EnrollmentService.search(txtSearch.getQuery(), EnrollmentService.PENDING);
		boolean same = latest.size() == tableModel.getRowCount();
		for (int i = 0; same && i < latest.size(); i++) {
			same = latest.get(i).studentKey.equals(String.valueOf(tableModel.getValueAt(i, 0)));
		}
		if (!same) {
			loadTable();
		}
	}

	@Override
	public void dispose() {
		if (refreshTimer != null) {
			refreshTimer.stop();
		}
		super.dispose();
	}

	// =================================================================
	// TOP BAR
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

	// =================================================================
	// VIEW 1: LIST OF APPLICATIONS (same style as the Courses table)
	// =================================================================
	private JPanel buildListView() {
		StudentsFrame.RoundedPanel card = new StudentsFrame.RoundedPanel(Color.WHITE, 20);
		card.setLayout(new BorderLayout(0, 15));
		card.setBorder(new EmptyBorder(20, 20, 20, 20));

		

		JPanel filterRow = new JPanel(new BorderLayout(10, 0));
		filterRow.setOpaque(false);
		card.add(filterRow, BorderLayout.NORTH);

		txtSearch = new SearchField("Search student ID or name...", 0, new Runnable() {
		    public void run() {
		        loadTable();
		    }
		});

		txtSearch.setPreferredSize(new Dimension(0, 38));

		filterRow.add(txtSearch, BorderLayout.CENTER);

		// ---- Table ----
		String[] columns = {"Student ID", "Name", "Course", "Year Level", "Status", "Actions"};
		tableModel = new DefaultTableModel(columns, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		table = new JTable(tableModel);
		table.setFont(new Font("Arial", Font.PLAIN, 13));
		table.setRowHeight(48);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowSelectionAllowed(false);
		table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
		table.getTableHeader().setBackground(new Color(240, 240, 240));
		table.getTableHeader().setForeground(DARK_TEAL);
		table.getTableHeader().setReorderingAllowed(false);
		table.setShowVerticalLines(false);
		table.setGridColor(new Color(230, 230, 230));

		for (int i = 0; i < ACTIONS_COL; i++) {
			int align = (i == 4) ? SwingConstants.CENTER : SwingConstants.LEFT; // Units centered
			table.getColumnModel().getColumn(i).setCellRenderer(new TextCellRenderer(align, i == STATUS_COL));
		}
		table.getColumnModel().getColumn(ACTIONS_COL).setCellRenderer(new ReviewButtonRenderer());

		table.getColumnModel().getColumn(0).setPreferredWidth(110);
		table.getColumnModel().getColumn(1).setPreferredWidth(220);
		table.getColumnModel().getColumn(2).setPreferredWidth(90);
		table.getColumnModel().getColumn(3).setPreferredWidth(100);
		table.getColumnModel().getColumn(4).setPreferredWidth(60);
		table.getColumnModel().getColumn(STATUS_COL).setPreferredWidth(100);
		table.getColumnModel().getColumn(ACTIONS_COL).setPreferredWidth(190);

		// Click on "Review Application"
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				int col = table.columnAtPoint(e.getPoint());
				if (row < 0 || col != ACTIONS_COL) return;
				showReview(String.valueOf(tableModel.getValueAt(row, 0)));
			}
		});
		table.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int col = table.columnAtPoint(e.getPoint());
				table.setCursor(col == ACTIONS_COL ? new Cursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
			}
		});

		JScrollPane scrollPane = new JScrollPane(table,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
		scrollPane.getViewport().setBackground(Color.WHITE);
		card.add(scrollPane, BorderLayout.CENTER);
		return card;
	}

	/** Fills the table with the applications that match the search box and the status filter. */
	private void loadTable() {
	    tableModel.setRowCount(0);

	    for (EnrollmentService.Enrollment e :
	            EnrollmentService.search(txtSearch.getQuery(), EnrollmentService.PENDING)) {

	        tableModel.addRow(new Object[] {
	                e.studentKey,
	                e.name,
	                e.course,
	                e.yearLevel,
	                e.status,
	                ""
	        });
	    }
	}

	// =================================================================
	// VIEW 2: REVIEW APPLICATION (student information + added subjects)
	// =================================================================
	private JPanel buildReviewView() {
		JPanel cardsRow = new JPanel(new GridBagLayout());
		cardsRow.setOpaque(false);

		// ===== Student Information card (left) =====
		StudentsFrame.RoundedPanel studentCard = new StudentsFrame.RoundedPanel(Color.WHITE, 20);
		studentCard.setLayout(new BorderLayout(0, 15));
		studentCard.setBorder(new EmptyBorder(20, 20, 20, 20));

		GridBagConstraints gbcStudent = new GridBagConstraints();
		gbcStudent.gridx = 0;
		gbcStudent.gridy = 0;
		gbcStudent.weightx = 0.6;
		gbcStudent.weighty = 1.0;
		gbcStudent.fill = GridBagConstraints.BOTH;
		gbcStudent.insets = new Insets(0, 0, 0, 10);
		cardsRow.add(studentCard, gbcStudent);

		JLabel lblStudentInfoTitle = new JLabel("Student Information");
		lblStudentInfoTitle.setFont(new Font("Arial", Font.BOLD, 18));
		lblStudentInfoTitle.setForeground(DARK_TEAL);
		lblStudentInfoTitle.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(225, 225, 225)),
				new EmptyBorder(0, 0, 10, 0)));
		studentCard.add(lblStudentInfoTitle, BorderLayout.NORTH);

		JPanel infoBody = new JPanel(new GridBagLayout());
		infoBody.setOpaque(false);
		studentCard.add(infoBody, BorderLayout.CENTER);

		JLabel lblPhoto = new JLabel("", SwingConstants.CENTER);
		lblPhoto.setPreferredSize(new Dimension(100, 100));
		URL photoUrl = this.getClass().getResource("/Profile3.png");
		if (photoUrl != null) {
			Image photoImg = new ImageIcon(photoUrl).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
			lblPhoto.setIcon(new ImageIcon(photoImg));
		} else {
			lblPhoto.setText("PHOTO");
			lblPhoto.setOpaque(true);
			lblPhoto.setBackground(new Color(200, 205, 208));
			lblPhoto.setForeground(Color.WHITE);
			lblPhoto.setFont(new Font("Arial", Font.BOLD, 12));
		}
		GridBagConstraints gbcPhoto = new GridBagConstraints();
		gbcPhoto.gridx = 0;
		gbcPhoto.gridy = 0;
		gbcPhoto.gridheight = 5;
		gbcPhoto.anchor = GridBagConstraints.NORTHWEST;
		gbcPhoto.insets = new Insets(10, 0, 0, 30);
		infoBody.add(lblPhoto, gbcPhoto);

		lblStudentIdValue = addInfoRow(infoBody, 0, "Student ID:");
		lblNameValue = addInfoRow(infoBody, 1, "Name:");
		lblCourseValue = addInfoRow(infoBody, 2, "Course:");
		lblYearValue = addInfoRow(infoBody, 3, "Year Level:");
		lblStatusValue = addInfoRow(infoBody, 4, "Status:");

		GridBagConstraints gbcFiller = new GridBagConstraints(); // keeps the rows at the top
		gbcFiller.gridx = 2;
		gbcFiller.gridy = 5;
		gbcFiller.weightx = 1.0;
		gbcFiller.weighty = 1.0;
		infoBody.add(Box.createGlue(), gbcFiller);

		// ===== Selected Subjects card (right) =====
		StudentsFrame.RoundedPanel subjectsCard = new StudentsFrame.RoundedPanel(Color.WHITE, 20);
		subjectsCard.setLayout(new BorderLayout(0, 15));
		subjectsCard.setBorder(new EmptyBorder(20, 20, 20, 20));

		GridBagConstraints gbcSubjects = new GridBagConstraints();
		gbcSubjects.gridx = 1;
		gbcSubjects.gridy = 0;
		gbcSubjects.weightx = 0.4;
		gbcSubjects.weighty = 1.0;
		gbcSubjects.fill = GridBagConstraints.BOTH;
		gbcSubjects.insets = new Insets(0, 10, 0, 0);
		cardsRow.add(subjectsCard, gbcSubjects);

		JLabel lblSelectedTitle = new JLabel("Selected Subjects");
		lblSelectedTitle.setFont(new Font("Arial", Font.BOLD, 18));
		lblSelectedTitle.setForeground(DARK_TEAL);
		subjectsCard.add(lblSelectedTitle, BorderLayout.NORTH);

		selectedListPanel = new JPanel();
		selectedListPanel.setBackground(Color.WHITE);
		selectedListPanel.setLayout(new BoxLayout(selectedListPanel, BoxLayout.Y_AXIS));

		JPanel listHolder = new JPanel(new BorderLayout()); // keeps the rows at the top
		listHolder.setBackground(Color.WHITE);
		listHolder.add(selectedListPanel, BorderLayout.NORTH);

		JScrollPane subjectsScroll = new JScrollPane(listHolder);
		subjectsScroll.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225)));
		subjectsScroll.getViewport().setBackground(Color.WHITE);
		subjectsCard.add(subjectsScroll, BorderLayout.CENTER);

		// ---- bottom: totals + Cancel / Approved ----
		JPanel bottom = new JPanel(new BorderLayout(0, 12));
		bottom.setOpaque(false);
		subjectsCard.add(bottom, BorderLayout.SOUTH);

		lblTotals = new JLabel(" ");
		lblTotals.setFont(new Font("Arial", Font.BOLD, 14));
		lblTotals.setForeground(DARK_TEAL);
		bottom.add(lblTotals, BorderLayout.NORTH);

		JPanel buttonRow = new JPanel(new GridBagLayout());
		buttonRow.setOpaque(false);
		bottom.add(buttonRow, BorderLayout.CENTER);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.setFont(new Font("Arial", Font.BOLD, 13));
		btnCancel.setBackground(Color.WHITE);
		btnCancel.setForeground(DARK_TEAL);
		btnCancel.setFocusPainted(false);
		btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnCancel.setPreferredSize(new Dimension(110, 38));
		btnCancel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showList(); // back to the table, nothing changed
			}
		});
		GridBagConstraints gbcCancel = new GridBagConstraints();
		gbcCancel.gridx = 0;
		gbcCancel.gridy = 0;
		gbcCancel.weightx = 1.0;
		gbcCancel.anchor = GridBagConstraints.EAST;
		gbcCancel.insets = new Insets(0, 0, 0, 10);
		buttonRow.add(btnCancel, gbcCancel);

		btnApprove = new JButton("Approved");
		btnApprove.setFont(new Font("Arial", Font.BOLD, 13));
		btnApprove.setBackground(ACCENT_GREEN);
		btnApprove.setForeground(Color.WHITE);
		btnApprove.setOpaque(true);
		btnApprove.setBorderPainted(false);
		btnApprove.setFocusPainted(false);
		btnApprove.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnApprove.setPreferredSize(new Dimension(110, 38));
		btnApprove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				confirmApproval();
			}
		});
		btnDeny = new JButton("Deny");
		btnDeny.setFont(new Font("Arial", Font.BOLD, 13));
		btnDeny.setBackground(DENY_RED);
		btnDeny.setForeground(Color.WHITE);
		btnDeny.setOpaque(true);
		btnDeny.setBorderPainted(false);
		btnDeny.setFocusPainted(false);
		btnDeny.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnDeny.setPreferredSize(new Dimension(110, 38));
		btnDeny.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				confirmDenial();
			}
		});
		GridBagConstraints gbcDeny = new GridBagConstraints();
		gbcDeny.gridx = 1;
		gbcDeny.gridy = 0;
		gbcDeny.insets = new Insets(0, 0, 0, 10);
		buttonRow.add(btnDeny, gbcDeny);

		GridBagConstraints gbcApprove = new GridBagConstraints();
		gbcApprove.gridx = 2;
		gbcApprove.gridy = 0;
		buttonRow.add(btnApprove, gbcApprove);

		return cardsRow;
	}

	/** Adds one "Label: Value" row to the student info panel and returns the value label. */
	private JLabel addInfoRow(JPanel parent, int row, String caption) {
		JLabel lblCaption = new JLabel(caption);
		lblCaption.setFont(new Font("Arial", Font.PLAIN, 14));
		lblCaption.setForeground(new Color(110, 110, 110));
		GridBagConstraints gbcCaption = new GridBagConstraints();
		gbcCaption.gridx = 1;
		gbcCaption.gridy = row;
		gbcCaption.anchor = GridBagConstraints.WEST;
		gbcCaption.insets = new Insets(8, 0, 8, 20);
		parent.add(lblCaption, gbcCaption);

		JLabel lblValue = new JLabel(" ");
		lblValue.setFont(new Font("Arial", Font.BOLD, 14));
		lblValue.setForeground(Color.BLACK);
		GridBagConstraints gbcValue = new GridBagConstraints();
		gbcValue.gridx = 2;
		gbcValue.gridy = row;
		gbcValue.anchor = GridBagConstraints.WEST;
		gbcValue.insets = new Insets(8, 0, 8, 0);
		parent.add(lblValue, gbcValue);
		return lblValue;
	}

	// =================================================================
	// LOGIC
	// =================================================================

	/** Shows the table of applications. */
	private void showList() {
		reviewingKey = null;
		lblHeader.setText("Enrollment Applications");
		loadTable(); // pick up any status change
		cardLayout.show(cardHolder, VIEW_LIST);
	}

	/** Opens the review view for one student: their information and the subjects they added. */
	private void showReview(String studentKey) {
		EnrollmentService.Enrollment app = EnrollmentService.find(studentKey);
		if (app == null) {
			return;
		}
		reviewingKey = studentKey;
		lblHeader.setText("Review Application");

		lblStudentIdValue.setText(app.studentKey);
		lblNameValue.setText(app.name);
		lblCourseValue.setText(app.course);
		lblYearValue.setText(app.yearLevel);
		showStatus(app.status);

		// subjects
		selectedListPanel.removeAll();
		if (app.subjects.isEmpty()) {
			JLabel none = new JLabel("No subjects were added.", SwingConstants.CENTER);
			none.setFont(new Font("Arial", Font.PLAIN, 13));
			none.setForeground(Color.GRAY);
			none.setBorder(new EmptyBorder(20, 10, 20, 10));
			selectedListPanel.add(none);
		}
		for (Object[] subject : app.subjects) {
			selectedListPanel.add(buildSubjectRow(subject));
		}
		selectedListPanel.revalidate();
		selectedListPanel.repaint();
		lblTotals.setText("Total Subjects: " + app.subjectCount() + "     |     Total Units: " + app.units);

		// Already enrolled -> nothing left to approve
		// Only a Pending application can be approved or denied; a decision is final.
		boolean canApprove = EnrollmentService.PENDING.equals(app.status);
		btnApprove.setEnabled(canApprove);
		btnApprove.setBackground(canApprove ? ACCENT_GREEN : DISABLED_GRAY);
		btnApprove.setCursor(canApprove ? new Cursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());

		boolean canDeny = EnrollmentService.PENDING.equals(app.status);
		btnDeny.setEnabled(canDeny);
		btnDeny.setBackground(canDeny ? DENY_RED : DISABLED_GRAY);
		btnDeny.setCursor(canDeny ? new Cursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());

		cardLayout.show(cardHolder, VIEW_REVIEW);
	}

	private void showStatus(String status) {
		boolean enrolled = EnrollmentService.ENROLLED.equals(status);
		lblStatusValue.setText(status);
		lblStatusValue.setForeground(enrolled ? TuitionFrame.PAID_GREEN
				: EnrollmentService.DENIED.equals(status) ? DENY_RED : PENDING_AMBER);
	}

	/** One line in the Selected Subjects list: "CCS101  Introduction to Computing   3 units". */
	private JPanel buildSubjectRow(Object[] subject) {
		JPanel row = new JPanel(new BorderLayout(12, 0));
		row.setBackground(Color.WHITE);
		row.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(238, 238, 238)),
				new EmptyBorder(9, 10, 9, 10)));

		JLabel lblCode = new JLabel(String.valueOf(subject[0]));
		lblCode.setFont(new Font("Arial", Font.BOLD, 13));
		lblCode.setForeground(DARK_TEAL);
		lblCode.setPreferredSize(new Dimension(70, 18));
		row.add(lblCode, BorderLayout.WEST);

		JLabel lblTitle = new JLabel(String.valueOf(subject[1]));
		lblTitle.setFont(new Font("Arial", Font.PLAIN, 13));
		row.add(lblTitle, BorderLayout.CENTER);

		JLabel lblUnits = new JLabel(subject[2] + " units");
		lblUnits.setFont(new Font("Arial", Font.PLAIN, 12));
		lblUnits.setForeground(Color.GRAY);
		row.add(lblUnits, BorderLayout.EAST);
		return row;
	}

	/** [Approved] button: asks first, then approves. */
	private void confirmApproval() {
		if (reviewingKey == null) {
			return;
		}
		int choice = JOptionPane.showConfirmDialog(this,
				"Approve the enrollment application of " + lblNameValue.getText() + "?",
				"Approve Application", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (choice != JOptionPane.YES_OPTION) {
			return;
		}
		String name = lblNameValue.getText();
		if (approveCurrent()) {
			JOptionPane.showMessageDialog(this, name + " is now Enrolled.",
					"Application Approved", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/** [Deny] button: asks for an optional reason, then denies the application. */
	private void confirmDenial() {
		if (reviewingKey == null) {
			return;
		}
		String name = lblNameValue.getText();
		JTextField txtReason = new JTextField(28);
		Object[] message = { "Deny the enrollment application of " + name + "?", " ",
				"Reason (optional):", txtReason };
		int choice = JOptionPane.showConfirmDialog(this, message, "Deny Application",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (choice != JOptionPane.OK_OPTION) {
			return;
		}
		boolean denied = EnrollmentService.deny(reviewingKey, txtReason.getText());
		
		if (denied) {
			
			showList();
			reviewingKey = null;
		} else {
			JOptionPane.showMessageDialog(this, "This application can no longer be denied (it was already decided).",
					"Not Denied", JOptionPane.WARNING_MESSAGE);
		}
	}

	/**
	 * Does the approval (no pop-ups): status Pending -> Enrolled, then back to the list.
	 * @return true if it was approved
	 */
	private boolean approveCurrent() {
		boolean approved = reviewingKey != null && EnrollmentService.approve(reviewingKey);
		showList(); // table now shows the new status
		return approved;
	}

	// =================================================================
	// TABLE RENDERERS
	// =================================================================

	/** Plain cell renderer; the Status column is colored (green Enrolled / amber Pending). */
	private static class TextCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		private final int align;
		private final boolean statusColumn;

		TextCellRenderer(int align, boolean statusColumn) {
			this.align = align;
			this.statusColumn = statusColumn;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, false, false, row, column);
			setHorizontalAlignment(align);
			setBorder(new EmptyBorder(0, 12, 0, 12));
			setBackground(Color.WHITE);
			if (statusColumn) {
				boolean enrolled = EnrollmentService.ENROLLED.equals(String.valueOf(value));
				setFont(new Font("Arial", Font.BOLD, 13));
				setForeground(enrolled ? TuitionFrame.PAID_GREEN
						: EnrollmentService.DENIED.equals(String.valueOf(value)) ? DENY_RED : PENDING_AMBER);
			} else {
				setFont(new Font("Arial", Font.PLAIN, 13));
				setForeground(Color.BLACK);
			}
			return this;
		}
	}

	/** Draws the green "Review Application" button in the Actions column. */
	private static class ReviewButtonRenderer extends JPanel implements TableCellRenderer {
		private static final long serialVersionUID = 1L;

		ReviewButtonRenderer() {
			setLayout(new GridLayout(1, 1));
			setBorder(new EmptyBorder(8, 8, 8, 8));
			setBackground(Color.WHITE);

			JButton b = new JButton("Review Application");
			b.setFont(new Font("Arial", Font.BOLD, 12));
			b.setBackground(ACCENT_GREEN);
			b.setForeground(Color.WHITE);
			b.setOpaque(true);
			b.setBorderPainted(false);
			b.setFocusable(false);
			add(b);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			return this;
		}
	}
}