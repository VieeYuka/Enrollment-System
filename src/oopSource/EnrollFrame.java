package oopSource;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import logReg.DBConnection;

import javax.swing.DefaultComboBoxModel;

public class EnrollFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	// The frame we came from (shown again when this form is closed)
	private JFrame previousFrame;

	private JTextField txtFirstName;
	private JTextField txtMiddleName;
	private JTextField txtLastName;
	private JTextField txtEmail;
	private JTextField txtAddress;
	private JComboBox<String> cmbGender;
	private JComboBox<String> cmbYearLevel;
	private JComboBox<String> cmbCourse;

	// Theme colors
	private static final Color DARK_TEAL = new Color(11, 55, 49);
	private static final Color ACCENT_GREEN = new Color(14, 100, 80);
	private static final Color FIELD_BORDER = new Color(210, 220, 218);
	private static final Color LINE_COLOR = new Color(230, 233, 232);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EnrollFrame frame = new EnrollFrame();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Default constructor (no previous frame given -> returns to StudentsFrame).
	 */
	public EnrollFrame() {
		this(null);
	}

	/**
	 * Create the frame.
	 * @param previous the frame to show again when this form is closed
	 */
	public EnrollFrame(JFrame previous) {
		this.previousFrame = previous;

		setTitle("Rey University - Add New Student");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1280, 720);

		contentPane = new JPanel();
		contentPane.setBackground(new Color(11, 55, 49));
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);
		contentPane.setLayout(new GridBagLayout());

		// =============================================================
		// WHITE CARD (centered, like the wireframe dialog)
		// =============================================================
		RoundedPanel cardPanel = new RoundedPanel(Color.WHITE, 16);
		cardPanel.setPreferredSize(new Dimension(820, 600));
		cardPanel.setLayout(new BorderLayout(0, 0));
		GridBagConstraints gbc_cardPanel = new GridBagConstraints();
		gbc_cardPanel.gridx = 0;
		gbc_cardPanel.gridy = 0;
		contentPane.add(cardPanel, gbc_cardPanel);

		// ---- Header: title + close (X) ----
		JPanel headerPanel = new JPanel();
		headerPanel.setOpaque(false);
		headerPanel.setBorder(new CompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, LINE_COLOR),
				new EmptyBorder(22, 35, 22, 25)));
		headerPanel.setLayout(new BorderLayout(0, 0));
		cardPanel.add(headerPanel, BorderLayout.NORTH);

		JLabel lblFormTitle = new JLabel("Add New Student");
		lblFormTitle.setFont(new Font("Arial", Font.BOLD, 22));
		lblFormTitle.setForeground(DARK_TEAL);
		headerPanel.add(lblFormTitle, BorderLayout.WEST);

		JButton btnClose = new JButton("\u00D7");
		btnClose.setFont(new Font("Arial", Font.PLAIN, 28));
		btnClose.setForeground(new Color(90, 90, 90));
		btnClose.setContentAreaFilled(false);
		btnClose.setBorderPainted(false);
		btnClose.setFocusPainted(false);
		btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				goBack();
			}
		});
		headerPanel.add(btnClose, BorderLayout.EAST);

		// ---- Form ----
		JPanel formPanel = new JPanel();
		formPanel.setOpaque(false);
		formPanel.setBorder(new EmptyBorder(25, 35, 10, 35));
		formPanel.setLayout(new GridBagLayout());
		cardPanel.add(formPanel, BorderLayout.CENTER);

		// Row 0 & 1: First Name | Middle Name
		JLabel lblFirstName = new JLabel("<html>First Name <font color='#D32F2F'>*</font></html>");
		lblFirstName.setFont(new Font("Arial", Font.BOLD, 13));
		GridBagConstraints gbc_lblFirstName = new GridBagConstraints();
		gbc_lblFirstName.anchor = GridBagConstraints.WEST;
		gbc_lblFirstName.insets = new Insets(0, 0, 6, 15);
		gbc_lblFirstName.gridx = 0;
		gbc_lblFirstName.gridy = 0;
		formPanel.add(lblFirstName, gbc_lblFirstName);

		txtFirstName = new JTextField();
		txtFirstName.setFont(new Font("Arial", Font.PLAIN, 14));
		txtFirstName.setBorder(new CompoundBorder(new RoundedBorder(FIELD_BORDER, 10), new EmptyBorder(0, 12, 0, 12)));
		txtFirstName.setPreferredSize(new Dimension(300, 42));
		GridBagConstraints gbc_txtFirstName = new GridBagConstraints();
		gbc_txtFirstName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFirstName.weightx = 1.0;
		gbc_txtFirstName.insets = new Insets(0, 0, 18, 15);
		gbc_txtFirstName.gridx = 0;
		gbc_txtFirstName.gridy = 1;
		formPanel.add(txtFirstName, gbc_txtFirstName);

		JLabel lblMiddleName = new JLabel("Middle Name");
		lblMiddleName.setFont(new Font("Arial", Font.BOLD, 13));
		GridBagConstraints gbc_lblMiddleName = new GridBagConstraints();
		gbc_lblMiddleName.anchor = GridBagConstraints.WEST;
		gbc_lblMiddleName.insets = new Insets(0, 15, 6, 0);
		gbc_lblMiddleName.gridx = 1;
		gbc_lblMiddleName.gridy = 0;
		formPanel.add(lblMiddleName, gbc_lblMiddleName);

		txtMiddleName = new JTextField();
		txtMiddleName.setFont(new Font("Arial", Font.PLAIN, 14));
		txtMiddleName.setBorder(new CompoundBorder(new RoundedBorder(FIELD_BORDER, 10), new EmptyBorder(0, 12, 0, 12)));
		txtMiddleName.setPreferredSize(new Dimension(300, 42));
		GridBagConstraints gbc_txtMiddleName = new GridBagConstraints();
		gbc_txtMiddleName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMiddleName.weightx = 1.0;
		gbc_txtMiddleName.insets = new Insets(0, 15, 18, 0);
		gbc_txtMiddleName.gridx = 1;
		gbc_txtMiddleName.gridy = 1;
		formPanel.add(txtMiddleName, gbc_txtMiddleName);

		// Row 2 & 3: Last Name | Email Address
		JLabel lblLastName = new JLabel("<html>Last Name <font color='#D32F2F'>*</font></html>");
		lblLastName.setFont(new Font("Arial", Font.BOLD, 13));
		GridBagConstraints gbc_lblLastName = new GridBagConstraints();
		gbc_lblLastName.anchor = GridBagConstraints.WEST;
		gbc_lblLastName.insets = new Insets(0, 0, 6, 15);
		gbc_lblLastName.gridx = 0;
		gbc_lblLastName.gridy = 2;
		formPanel.add(lblLastName, gbc_lblLastName);

		txtLastName = new JTextField();
		txtLastName.setFont(new Font("Arial", Font.PLAIN, 14));
		txtLastName.setBorder(new CompoundBorder(new RoundedBorder(FIELD_BORDER, 10), new EmptyBorder(0, 12, 0, 12)));
		txtLastName.setPreferredSize(new Dimension(300, 42));
		GridBagConstraints gbc_txtLastName = new GridBagConstraints();
		gbc_txtLastName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtLastName.weightx = 1.0;
		gbc_txtLastName.insets = new Insets(0, 0, 18, 15);
		gbc_txtLastName.gridx = 0;
		gbc_txtLastName.gridy = 3;
		formPanel.add(txtLastName, gbc_txtLastName);

		JLabel lblEmail = new JLabel("<html>Email Address <font color='#D32F2F'>*</font></html>");
		lblEmail.setFont(new Font("Arial", Font.BOLD, 13));
		GridBagConstraints gbc_lblEmail = new GridBagConstraints();
		gbc_lblEmail.anchor = GridBagConstraints.WEST;
		gbc_lblEmail.insets = new Insets(0, 15, 6, 0);
		gbc_lblEmail.gridx = 1;
		gbc_lblEmail.gridy = 2;
		formPanel.add(lblEmail, gbc_lblEmail);

		txtEmail = new JTextField();
		txtEmail.setFont(new Font("Arial", Font.PLAIN, 14));
		txtEmail.setBorder(new CompoundBorder(new RoundedBorder(FIELD_BORDER, 10), new EmptyBorder(0, 12, 0, 12)));
		txtEmail.setPreferredSize(new Dimension(300, 42));
		GridBagConstraints gbc_txtEmail = new GridBagConstraints();
		gbc_txtEmail.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtEmail.weightx = 1.0;
		gbc_txtEmail.insets = new Insets(0, 15, 18, 0);
		gbc_txtEmail.gridx = 1;
		gbc_txtEmail.gridy = 3;
		formPanel.add(txtEmail, gbc_txtEmail);

		// Row 4 & 5: Gender | Year Level | Course
		JLabel lblGender = new JLabel("<html>Gender <font color='#D32F2F'>*</font></html>");
		lblGender.setFont(new Font("Arial", Font.BOLD, 13));
		GridBagConstraints gbc_lblGender = new GridBagConstraints();
		gbc_lblGender.anchor = GridBagConstraints.WEST;
		gbc_lblGender.insets = new Insets(0, 0, 6, 15);
		gbc_lblGender.gridx = 0;
		gbc_lblGender.gridy = 4;
		formPanel.add(lblGender, gbc_lblGender);

		cmbGender = new JComboBox<String>();
		cmbGender.setModel(new DefaultComboBoxModel<String>(new String[] {"Male", "Female"}));
		cmbGender.setFont(new Font("Arial", Font.PLAIN, 14));
		cmbGender.setBackground(Color.WHITE);
		cmbGender.setPreferredSize(new Dimension(300, 42));
		GridBagConstraints gbc_cmbGender = new GridBagConstraints();
		gbc_cmbGender.fill = GridBagConstraints.HORIZONTAL;
		gbc_cmbGender.weightx = 1.0;
		gbc_cmbGender.insets = new Insets(0, 0, 18, 15);
		gbc_cmbGender.gridx = 0;
		gbc_cmbGender.gridy = 5;
		formPanel.add(cmbGender, gbc_cmbGender);

		// Combined Panel for Year Level & Course next to each other in Column 1
		JPanel pnlYearCourse = new JPanel(new GridBagLayout());
		pnlYearCourse.setOpaque(false);
		GridBagConstraints gbc_pnlYearCourse = new GridBagConstraints();
		gbc_pnlYearCourse.fill = GridBagConstraints.BOTH;
		gbc_pnlYearCourse.weightx = 1.0;
		gbc_pnlYearCourse.insets = new Insets(0, 15, 18, 0);
		gbc_pnlYearCourse.gridx = 1;
		gbc_pnlYearCourse.gridy = 4;
		gbc_pnlYearCourse.gridheight = 2;
		formPanel.add(pnlYearCourse, gbc_pnlYearCourse);

		JLabel lblYearLevel = new JLabel("<html>Year Level <font color='#D32F2F'>*</font></html>");
		lblYearLevel.setFont(new Font("Arial", Font.BOLD, 13));
		GridBagConstraints gbc_lblYearLevel = new GridBagConstraints();
		gbc_lblYearLevel.anchor = GridBagConstraints.WEST;
		gbc_lblYearLevel.insets = new Insets(0, 0, 6, 10);
		gbc_lblYearLevel.gridx = 0;
		gbc_lblYearLevel.gridy = 0;
		pnlYearCourse.add(lblYearLevel, gbc_lblYearLevel);

		JLabel lblCourse = new JLabel("<html>Course <font color='#D32F2F'>*</font></html>");
		lblCourse.setFont(new Font("Arial", Font.BOLD, 13));
		GridBagConstraints gbc_lblCourse = new GridBagConstraints();
		gbc_lblCourse.anchor = GridBagConstraints.WEST;
		gbc_lblCourse.insets = new Insets(0, 0, 6, 0);
		gbc_lblCourse.gridx = 1;
		gbc_lblCourse.gridy = 0;
		pnlYearCourse.add(lblCourse, gbc_lblCourse);

		cmbYearLevel = new JComboBox<String>();
		cmbYearLevel.setModel(new DefaultComboBoxModel<String>(new String[] {"1st Year", "2nd Year", "3rd Year", "4th Year"}));
		cmbYearLevel.setFont(new Font("Arial", Font.PLAIN, 14));
		cmbYearLevel.setBackground(Color.WHITE);
		cmbYearLevel.setPreferredSize(new Dimension(140, 42));
		GridBagConstraints gbc_cmbYearLevel = new GridBagConstraints();
		gbc_cmbYearLevel.fill = GridBagConstraints.HORIZONTAL;
		gbc_cmbYearLevel.weightx = 0.4;
		gbc_cmbYearLevel.insets = new Insets(0, 0, 0, 10);
		gbc_cmbYearLevel.gridx = 0;
		gbc_cmbYearLevel.gridy = 1;
		pnlYearCourse.add(cmbYearLevel, gbc_cmbYearLevel);

		cmbCourse = new JComboBox<String>();
		
		
		try {
			Connection conn = DBConnection.getConnection();
			
			String sql ="Select course_code FROM courses";
					
			PreparedStatement pstate = conn.prepareStatement(sql);	
			
			ResultSet rs = pstate.executeQuery();
			
			while(rs.next()) {
				cmbCourse.addItem(rs.getString("course_code"));
			}
			rs.close();
			pstate.close();
			conn.close();
			
		}catch(SQLException e) {
			JOptionPane.showMessageDialog(
    		        null,
    		        "Error loading courses: " + e.getMessage(),
    		        "Database Error",
    		        JOptionPane.ERROR_MESSAGE);
		}
//		cmbCourse.setModel(new DefaultComboBoxModel<String>(new String[] {"BSCS", "BSIT", "BSIS", "BSEd", "BSBA"}));
		
		
		
		
		cmbCourse.setFont(new Font("Arial", Font.PLAIN, 14));
		cmbCourse.setBackground(Color.WHITE);
		cmbCourse.setPreferredSize(new Dimension(150, 42));
		GridBagConstraints gbc_cmbCourse = new GridBagConstraints();
		gbc_cmbCourse.fill = GridBagConstraints.HORIZONTAL;
		gbc_cmbCourse.weightx = 0.6;
		gbc_cmbCourse.gridx = 1;
		gbc_cmbCourse.gridy = 1;
		pnlYearCourse.add(cmbCourse, gbc_cmbCourse);

		// Row 6 & 7: Address (Spans full width)
		JLabel lblAddress = new JLabel("<html>Address <font color='#D32F2F'>*</font></html>");
		lblAddress.setFont(new Font("Arial", Font.BOLD, 13));
		GridBagConstraints gbc_lblAddress = new GridBagConstraints();
		gbc_lblAddress.anchor = GridBagConstraints.WEST;
		gbc_lblAddress.insets = new Insets(0, 0, 6, 0);
		gbc_lblAddress.gridwidth = 2;
		gbc_lblAddress.gridx = 0;
		gbc_lblAddress.gridy = 6;
		formPanel.add(lblAddress, gbc_lblAddress);

		txtAddress = new JTextField();
		txtAddress.setFont(new Font("Arial", Font.PLAIN, 14));
		txtAddress.setBorder(new CompoundBorder(new RoundedBorder(FIELD_BORDER, 10), new EmptyBorder(0, 12, 0, 12)));
		txtAddress.setPreferredSize(new Dimension(630, 42));
		GridBagConstraints gbc_txtAddress = new GridBagConstraints();
		gbc_txtAddress.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtAddress.weightx = 1.0;
		gbc_txtAddress.gridwidth = 2;
		gbc_txtAddress.insets = new Insets(0, 0, 0, 0);
		gbc_txtAddress.gridx = 0;
		gbc_txtAddress.gridy = 7;
		formPanel.add(txtAddress, gbc_txtAddress);

		// Spacer so the form stays at the top
		JPanel spacer = new JPanel();
		spacer.setOpaque(false);
		GridBagConstraints gbc_spacer = new GridBagConstraints();
		gbc_spacer.fill = GridBagConstraints.BOTH;
		gbc_spacer.weighty = 1.0;
		gbc_spacer.gridwidth = 2;
		gbc_spacer.gridx = 0;
		gbc_spacer.gridy = 8;
		formPanel.add(spacer, gbc_spacer);

		// ---- Footer: Cancel + Save ----
		JPanel footerPanel = new JPanel();
		footerPanel.setOpaque(false);
		footerPanel.setBorder(new CompoundBorder(
				BorderFactory.createMatteBorder(1, 0, 0, 0, LINE_COLOR),
				new EmptyBorder(15, 35, 15, 35)));
		footerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 0));
		cardPanel.add(footerPanel, BorderLayout.SOUTH);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.setFont(new Font("Arial", Font.PLAIN, 14));
		btnCancel.setForeground(new Color(70, 70, 70));
		btnCancel.setBackground(Color.WHITE);
		btnCancel.setFocusPainted(false);
		btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnCancel.setBorder(new CompoundBorder(new RoundedBorder(FIELD_BORDER, 10), new EmptyBorder(0, 12, 0, 12)));
		btnCancel.setPreferredSize(new Dimension(120, 42));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				goBack();
			}
		});
		footerPanel.add(btnCancel);

		JButton btnSave = new JButton("Save");
		btnSave.setFont(new Font("Arial", Font.BOLD, 14));
		btnSave.setForeground(Color.WHITE);
		btnSave.setBackground(ACCENT_GREEN);
		btnSave.setOpaque(true);
		btnSave.setFocusPainted(false);
		btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnSave.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		btnSave.setPreferredSize(new Dimension(120, 42));
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveStudent();
			}
		});
		footerPanel.add(btnSave);
	}

	/**
	 * Closes this form and shows the previous frame again.
	 */
	private void goBack() {
		JFrame target = previousFrame;
		if (target == null) {
			target = new StudentsFrame();
		}
		target.setBounds(getBounds());
		target.setVisible(true);
		dispose();
	}

	private int generateID() {
		return 0;
	}
	
	private String generateUnivEmail() {
		return "";
	}
	private void saveStudent() {
		String firstName = txtFirstName.getText().trim();
		String middleName = txtMiddleName.getText().trim();
		String lastName = txtLastName.getText().trim();
		String email = txtEmail.getText().trim();
		String address = txtAddress.getText().trim();
		String gender = (String) cmbGender.getSelectedItem();
		String yearLevel = (String) cmbYearLevel.getSelectedItem();
		String course = (String) cmbCourse.getSelectedItem();

		// Validation check for empty required fields
		if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || address.isEmpty()) {
			JOptionPane.showMessageDialog(this, "First Name, Last Name, Email, and Address are required.",
					"Missing Information", JOptionPane.WARNING_MESSAGE);
			return;
		}

		if (!email.contains("@")) {
			JOptionPane.showMessageDialog(this, "Please enter a valid email address.",
					"Invalid Email", JOptionPane.WARNING_MESSAGE);
			return;
		}

		// TODO: insert firstName, middleName, lastName, email, address, gender, yearLevel, course into MySQL here.

		
        
		String courseCode = (String) cmbCourse.getSelectedItem();

		String getCourseName =
		        "SELECT course_name FROM courses WHERE course_code = ?";

		String courseName = "";

		try (Connection conn = DBConnection.getConnection();
		     PreparedStatement pst = conn.prepareStatement(getCourseName)) {

		    pst.setString(1, courseCode);

		    ResultSet rs = pst.executeQuery();

		    if (rs.next()) {
		        courseName = rs.getString("course_name");
		    } else {
		        JOptionPane.showMessageDialog(
		            this,
		            "Selected course does not exist.",
		            "Invalid Course",
		            JOptionPane.WARNING_MESSAGE
		        );
		        return;
		    }

		} catch (SQLException e) {
		    JOptionPane.showMessageDialog(
		        this,
		        "Error getting course: " + e.getMessage(),
		        "Database Error",
		        JOptionPane.ERROR_MESSAGE
		    );
		    return;
		}

		String insertStudent =
		        "INSERT INTO students "
		      + "(student_number, first_name, last_name, personal_email, "
		      + "univ_email, course, year_level) "
		      + "VALUES (?, ?, ?, ?, ?, ?, ?) "
		      + "ON DUPLICATE KEY UPDATE "
		      + "student_number = student_number";

		try (Connection conn = DBConnection.getConnection();
		     PreparedStatement pstate = conn.prepareStatement(insertStudent)) {

		    pstate.setString(1, "");
		    pstate.setString(2, firstName);
		    pstate.setString(3, lastName);
		    pstate.setString(4, email);
		    pstate.setString(5, lastName + "@university.edu");
		    pstate.setString(6, courseName);
		    pstate.setString(7, yearLevel);
		    

		    pstate.executeUpdate();

		    JOptionPane.showMessageDialog(
		        this,
		        "Record added successfully!",
		        "Success",
		        JOptionPane.INFORMATION_MESSAGE
		    );

		} catch (SQLException e) {

		    JOptionPane.showMessageDialog(
		        this,
		        "Error adding record: " + e.getMessage(),
		        "Database Error",
		        JOptionPane.ERROR_MESSAGE
		    );
		}
		
		
		
		JOptionPane.showMessageDialog(this, "Student saved:\n" + firstName + " " + middleName + " " + lastName
				+ "\nEmail: " + email + "\nAddress: " + address + "\n" + gender + " | " + yearLevel + " - " + course, 
				"Saved", JOptionPane.INFORMATION_MESSAGE);
		goBack();
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

	/** Thin rounded outline used for the input fields and the Cancel button. */
	static class RoundedBorder extends AbstractBorder {
		private static final long serialVersionUID = 1L;
		private Color color;
		private int arc;

		public RoundedBorder(Color color, int arc) {
			this.color = color;
			this.arc = arc;
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(color);
			g2.drawRoundRect(x, y, width - 1, height - 1, arc, arc);
			g2.dispose();
		}

		@Override
		public Insets getBorderInsets(Component c) {
			return new Insets(1, 1, 1, 1);
		}
	}
}