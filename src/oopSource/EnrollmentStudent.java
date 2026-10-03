package oopSource;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class EnrollmentStudent extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private String loggedInUser;
	private String loggedInRole;

	// Student info labels (can be updated from the database)
	private JLabel lblStudentIdValue;
	private JLabel lblNameValue;
	private JLabel lblCourseValue;
	private JLabel lblYearValue;

	// Selected subjects
	private JPanel selectedListPanel;
	private JLabel lblNoSubjects;

	// Dark Teal Theme Colors
	private static final Color DARK_TEAL = new Color(11, 55, 49);
	private static final Color LIGHT_BG = new Color(235, 235, 235);
	private static final Color ACCENT_GREEN = new Color(38, 128, 98);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EnrollmentStudent frame = new EnrollmentStudent("Admin");
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
		this("Admin");
	}

	/**
	 * Create the frame.
	 */
	public EnrollmentStudent(String username) {
		this.loggedInUser = username;

		setTitle("Rey University - Enrollment Processing");
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
		URL imgUrl = EnrollmentStudent.class.getResource("/RUlogo (1).png");
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
		StudentsFrame.NavItem navDashboard = new StudentsFrame.NavItem("Dashboard", false);
		navDashboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFrame(new DashboardFrame(loggedInUser, loggedInRole));
			}
		});
		navContainer.add(navDashboard);

		// Students
		StudentsFrame.NavItem navStudents = new StudentsFrame.NavItem("Students", false);
		navStudents.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFrame(new StudentsFrame(loggedInUser, loggedInRole));
			}
		});
		navContainer.add(navStudents);

		// Enrollment (current page)
		StudentsFrame.NavItem navEnrollment = new StudentsFrame.NavItem("Enrollment", true);
		navContainer.add(navEnrollment);

		// Courses & Schedules
		StudentsFrame.NavItem navCourses = new StudentsFrame.NavItem("Courses & Schedules", false);
		navCourses.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFrame(new CoursesFrame());
			}
		});
		navContainer.add(navCourses);

		// Tuition & Payments
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

		// ---- Enrollment Screen Body ----
		JPanel enrollBody = new JPanel();
		enrollBody.setOpaque(false);
		enrollBody.setBorder(new EmptyBorder(25, 25, 25, 25));
		enrollBody.setLayout(new BorderLayout(0, 20));
		mainContentPanel.add(enrollBody, BorderLayout.CENTER);

		// ---- Header + Search Subjects (stacked at the top) ----
		JPanel topSection = new JPanel();
		topSection.setOpaque(false);
		topSection.setLayout(new BorderLayout(0, 15));
		enrollBody.add(topSection, BorderLayout.NORTH);

		JLabel lblHeader = new JLabel("Enroll Student");
		lblHeader.setFont(new Font("Arial", Font.BOLD, 26));
		lblHeader.setForeground(DARK_TEAL);
		topSection.add(lblHeader, BorderLayout.NORTH);

		// Search Subjects card
		StudentsFrame.RoundedPanel searchCard = new StudentsFrame.RoundedPanel(Color.WHITE, 20);
		searchCard.setLayout(new BorderLayout(0, 12));
		searchCard.setBorder(new EmptyBorder(15, 20, 15, 20));
		topSection.add(searchCard, BorderLayout.CENTER);

		JLabel lblSearchTitle = new JLabel("Search Students");
		lblSearchTitle.setFont(new Font("Arial", Font.BOLD, 16));
		lblSearchTitle.setForeground(DARK_TEAL);
		searchCard.add(lblSearchTitle, BorderLayout.NORTH);

		JPanel searchRow = new JPanel();
		searchRow.setOpaque(false);
		searchRow.setLayout(new BorderLayout(10, 0));
		searchCard.add(searchRow, BorderLayout.CENTER);

		final JTextField txtSearch = new JTextField("Enter student ID or name...");
		txtSearch.setFont(new Font("Arial", Font.PLAIN, 13));
		txtSearch.setForeground(Color.GRAY);
		txtSearch.setPreferredSize(new Dimension(0, 40));
		searchRow.add(txtSearch, BorderLayout.CENTER);

		JButton btnSearch = new JButton("Search");
		btnSearch.setFont(new Font("Arial", Font.BOLD, 13));
		btnSearch.setBackground(ACCENT_GREEN);
		btnSearch.setForeground(Color.WHITE);
		btnSearch.setFocusPainted(false);
		btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnSearch.setPreferredSize(new Dimension(140, 40));
		btnSearch.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		
		//ditooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
		
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: search subjects (e.g. from MySQL) using txtSearch.getText().trim()
			}
		});
		
		searchRow.add(btnSearch, BorderLayout.EAST);

		// ---- Two cards: Student Information | Selected Subjects ----
		JPanel cardsRow = new JPanel();
		cardsRow.setOpaque(false);
		cardsRow.setLayout(new GridBagLayout());
		enrollBody.add(cardsRow, BorderLayout.CENTER);

		// ===== Student Information card (left) =====
		StudentsFrame.RoundedPanel studentCard = new StudentsFrame.RoundedPanel(Color.WHITE, 20);
		studentCard.setLayout(new BorderLayout(0, 15));
		studentCard.setBorder(new EmptyBorder(20, 20, 20, 20));

		GridBagConstraints gbc_studentCard = new GridBagConstraints();
		gbc_studentCard.gridx = 0;
		gbc_studentCard.gridy = 0;
		gbc_studentCard.weightx = 0.6;
		gbc_studentCard.weighty = 1.0;
		gbc_studentCard.fill = GridBagConstraints.BOTH;
		gbc_studentCard.insets = new Insets(0, 0, 0, 10);
		cardsRow.add(studentCard, gbc_studentCard);

		JLabel lblStudentInfoTitle = new JLabel("Student Information");
		lblStudentInfoTitle.setFont(new Font("Arial", Font.BOLD, 18));
		lblStudentInfoTitle.setForeground(DARK_TEAL);
		lblStudentInfoTitle.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(225, 225, 225)),
				new EmptyBorder(0, 0, 10, 0)));
		studentCard.add(lblStudentInfoTitle, BorderLayout.NORTH);

		JPanel studentInfoBody = new JPanel();
		studentInfoBody.setOpaque(false);
		studentInfoBody.setLayout(new GridBagLayout());
		studentCard.add(studentInfoBody, BorderLayout.CENTER);

		// Student photo
		JLabel lblStudentPhoto = new JLabel("", SwingConstants.CENTER);
		lblStudentPhoto.setPreferredSize(new Dimension(100, 100));
		URL photoUrl = this.getClass().getResource("/Profile3.png");
		if (photoUrl != null) {
			Image photoImg = new ImageIcon(photoUrl).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
			lblStudentPhoto.setIcon(new ImageIcon(photoImg));
		} else {
			lblStudentPhoto.setText("PHOTO");
			lblStudentPhoto.setOpaque(true);
			lblStudentPhoto.setBackground(new Color(200, 205, 208));
			lblStudentPhoto.setForeground(Color.WHITE);
			lblStudentPhoto.setFont(new Font("Arial", Font.BOLD, 12));
		}
		GridBagConstraints gbc_photo = new GridBagConstraints();
		gbc_photo.gridx = 0;
		gbc_photo.gridy = 0;
		gbc_photo.gridheight = 4;
		gbc_photo.anchor = GridBagConstraints.NORTHWEST;
		gbc_photo.insets = new Insets(10, 0, 0, 30);
		studentInfoBody.add(lblStudentPhoto, gbc_photo);

		lblStudentIdValue = addInfoRow(studentInfoBody, 0, "Student ID:", "RU-2025-0001");
		lblNameValue = addInfoRow(studentInfoBody, 1, "Name:", "Santos, Maria A.");
		lblCourseValue = addInfoRow(studentInfoBody, 2, "Course:", "BSIT");
		lblYearValue = addInfoRow(studentInfoBody, 3, "Year Level:", "1st Year");

		// Filler so the rows stay at the top
		GridBagConstraints gbc_filler = new GridBagConstraints();
		gbc_filler.gridx = 2;
		gbc_filler.gridy = 4;
		gbc_filler.weightx = 1.0;
		gbc_filler.weighty = 1.0;
		studentInfoBody.add(Box.createGlue(), gbc_filler);

		// ===== Selected Subjects card (right) =====
		StudentsFrame.RoundedPanel subjectsCard = new StudentsFrame.RoundedPanel(Color.WHITE, 20);
		subjectsCard.setLayout(new BorderLayout(0, 15));
		subjectsCard.setBorder(new EmptyBorder(20, 20, 20, 20));

		GridBagConstraints gbc_subjectsCard = new GridBagConstraints();
		gbc_subjectsCard.gridx = 1;
		gbc_subjectsCard.gridy = 0;
		gbc_subjectsCard.weightx = 0.4;
		gbc_subjectsCard.weighty = 1.0;
		gbc_subjectsCard.fill = GridBagConstraints.BOTH;
		gbc_subjectsCard.insets = new Insets(0, 10, 0, 0);
		cardsRow.add(subjectsCard, gbc_subjectsCard);

		JLabel lblSelectedTitle = new JLabel("Selected Subjects");
		lblSelectedTitle.setFont(new Font("Arial", Font.BOLD, 18));
		lblSelectedTitle.setForeground(DARK_TEAL);
		subjectsCard.add(lblSelectedTitle, BorderLayout.NORTH);

		selectedListPanel = new JPanel();
		selectedListPanel.setBackground(Color.WHITE);
		selectedListPanel.setLayout(new BoxLayout(selectedListPanel, BoxLayout.Y_AXIS));

		lblNoSubjects = new JLabel("No subjects selected yet.", SwingConstants.CENTER);
		lblNoSubjects.setFont(new Font("Arial", Font.PLAIN, 13));
		lblNoSubjects.setForeground(Color.GRAY);

		// Placeholder wrapper keeps the empty message centered
		final JPanel emptyWrapper = new JPanel(new BorderLayout());
		emptyWrapper.setBackground(Color.WHITE);
		emptyWrapper.add(lblNoSubjects, BorderLayout.CENTER);
		selectedListPanel.add(emptyWrapper);

		JScrollPane subjectsScroll = new JScrollPane(selectedListPanel);
		subjectsScroll.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225)));
		subjectsScroll.getViewport().setBackground(Color.WHITE);
		subjectsCard.add(subjectsScroll, BorderLayout.CENTER);

		// Cancel / Next buttons
		JPanel buttonRow = new JPanel();
		buttonRow.setOpaque(false);
		buttonRow.setLayout(new GridBagLayout());
		subjectsCard.add(buttonRow, BorderLayout.SOUTH);

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
				openFrame(new StudentsFrame(loggedInUser, loggedInRole));
			}
		});
		GridBagConstraints gbc_cancel = new GridBagConstraints();
		gbc_cancel.gridx = 0;
		gbc_cancel.gridy = 0;
		gbc_cancel.weightx = 1.0;
		gbc_cancel.anchor = GridBagConstraints.EAST;
		gbc_cancel.insets = new Insets(0, 0, 0, 10);
		buttonRow.add(btnCancel, gbc_cancel);

		JButton btnNext = new JButton("Next");
		btnNext.setFont(new Font("Arial", Font.BOLD, 13));
		btnNext.setBackground(ACCENT_GREEN);
		btnNext.setForeground(Color.WHITE);
		btnNext.setFocusPainted(false);
		btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnNext.setPreferredSize(new Dimension(110, 38));
		btnNext.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: proceed to the next step / save the enrollment
			}
		});
		GridBagConstraints gbc_next = new GridBagConstraints();
		gbc_next.gridx = 1;
		gbc_next.gridy = 0;
		buttonRow.add(btnNext, gbc_next);
	}

	/** Adds one "Label: Value" row to the student info panel and returns the value label. */
	private JLabel addInfoRow(JPanel parent, int row, String caption, String value) {
		JLabel lblCaption = new JLabel(caption);
		lblCaption.setFont(new Font("Arial", Font.PLAIN, 14));
		lblCaption.setForeground(new Color(110, 110, 110));
		GridBagConstraints gbc_caption = new GridBagConstraints();
		gbc_caption.gridx = 1;
		gbc_caption.gridy = row;
		gbc_caption.anchor = GridBagConstraints.WEST;
		gbc_caption.insets = new Insets(8, 0, 8, 20);
		parent.add(lblCaption, gbc_caption);

		JLabel lblValue = new JLabel(value);
		lblValue.setFont(new Font("Arial", Font.BOLD, 14));
		lblValue.setForeground(Color.BLACK);
		GridBagConstraints gbc_value = new GridBagConstraints();
		gbc_value.gridx = 2;
		gbc_value.gridy = row;
		gbc_value.anchor = GridBagConstraints.WEST;
		gbc_value.insets = new Insets(8, 0, 8, 0);
		parent.add(lblValue, gbc_value);
		return lblValue;
	}

	/**
	 * Shows the next frame (same position as this one) and closes this frame.
	 */
	private void openFrame(JFrame next) {
		next.setBounds(getBounds());
		next.setVisible(true);
		dispose();
	}

	/** Update the student information card (e.g. from MySQL). */
	public void setStudentInfo(String studentId, String name, String course, String yearLevel) {
		lblStudentIdValue.setText(studentId);
		lblNameValue.setText(name);
		lblCourseValue.setText(course);
		lblYearValue.setText(yearLevel);
	}

	/** Add a subject to the "Selected Subjects" list. */
	public void addSelectedSubject(String subjectText) {
		// Remove the "No subjects selected yet." placeholder the first time
		if (lblNoSubjects.getParent() != null) {
			selectedListPanel.removeAll();
//			lblNoSubjects.setParent_removed();
		}
		JLabel item = new JLabel(subjectText);
		item.setFont(new Font("Arial", Font.PLAIN, 13));
		item.setBorder(new EmptyBorder(8, 10, 8, 10));
		selectedListPanel.add(item);
		selectedListPanel.revalidate();
		selectedListPanel.repaint();
	}
}