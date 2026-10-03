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

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class CoursesFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tblCourses;
	private JTextField txtSearch;

	// Theme colors
	private static final Color DARK_TEAL = new Color(11, 55, 49);
	private static final Color ACCENT_GREEN = new Color(14, 100, 80);
	private static final Color BACKDROP_BG = new Color(245, 247, 248);
	private static final Color FIELD_BORDER = new Color(220, 225, 224);
	private static final Color LINE_COLOR = new Color(230, 233, 232);
	private static final Color SIDEBAR_HOVER = new Color(20, 75, 68);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CoursesFrame frame = new CoursesFrame();
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
	public CoursesFrame() {
		setTitle("Rey University - Courses & Schedules");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1280, 720);

		contentPane = new JPanel();
		contentPane.setBackground(BACKDROP_BG);
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		// =============================================================
		// LEFT SIDEBAR
		// =============================================================
		JPanel sidebarPanel = new JPanel();
		sidebarPanel.setBackground(DARK_TEAL);
		sidebarPanel.setPreferredSize(new Dimension(240, 720));
		sidebarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		contentPane.add(sidebarPanel, BorderLayout.WEST);

		// Brand Logo Header
		JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 25));
		brandPanel.setOpaque(false);
		brandPanel.setPreferredSize(new Dimension(240, 80));

		JLabel lblLogo = new JLabel("REY UNIVERSITY");
		lblLogo.setFont(new Font("Arial", Font.BOLD, 16));
		lblLogo.setForeground(Color.WHITE);
		brandPanel.add(lblLogo);
		sidebarPanel.add(brandPanel);

		// Navigation Menu Items
		String[] navItems = {
			"Dashboard", "Students", "Enrollment", 
			"Courses & Schedules", "Tuition & Payments", "Reports", "Settings"
		};

		for (String item : navItems) {
			JButton btnNav = new JButton(item);
			btnNav.setPreferredSize(new Dimension(240, 48));
			btnNav.setFont(new Font("Arial", Font.PLAIN, 14));
			btnNav.setHorizontalAlignment(SwingConstants.LEFT);
			btnNav.setFocusPainted(false);
			btnNav.setCursor(new Cursor(Cursor.HAND_CURSOR));

			if (item.equals("Courses & Schedules")) {
				btnNav.setForeground(Color.WHITE);
				btnNav.setBackground(SIDEBAR_HOVER);
				btnNav.setOpaque(true);
				btnNav.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, Color.WHITE));
			} else {
				btnNav.setForeground(new Color(180, 205, 200));
				btnNav.setContentAreaFilled(false);
				btnNav.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
			}
			sidebarPanel.add(btnNav);
		}

		// =============================================================
		// MAIN CONTENT AREA
		// =============================================================
		JPanel mainPanel = new JPanel();
		mainPanel.setOpaque(false);
		mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
		mainPanel.setLayout(new BorderLayout(0, 20));
		contentPane.add(mainPanel, BorderLayout.CENTER);

		// Top Header (Title + Add Button)
		JPanel topHeader = new JPanel(new BorderLayout());
		topHeader.setOpaque(false);

		JLabel lblTitle = new JLabel("Courses & Schedules");
		lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
		lblTitle.setForeground(DARK_TEAL);
		topHeader.add(lblTitle, BorderLayout.WEST);

		JButton btnAddCourse = new JButton("+ Add Course");
		btnAddCourse.setFont(new Font("Arial", Font.BOLD, 14));
		btnAddCourse.setForeground(Color.WHITE);
		btnAddCourse.setBackground(ACCENT_GREEN);
		btnAddCourse.setOpaque(true);
		btnAddCourse.setFocusPainted(false);
		btnAddCourse.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnAddCourse.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		btnAddCourse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: Open Add Course dialog or frame
			}
		});
		topHeader.add(btnAddCourse, BorderLayout.EAST);

		mainPanel.add(topHeader, BorderLayout.NORTH);

		// White Card Panel (Container for Tabs, Search, Table, and Pagination)
		RoundedPanel cardPanel = new RoundedPanel(Color.WHITE, 12);
		cardPanel.setLayout(new BorderLayout(0, 15));
		cardPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
		mainPanel.add(cardPanel, BorderLayout.CENTER);

		// Sub Header Area (Tabs & Filters)
		JPanel cardHeaderPanel = new JPanel(new BorderLayout(0, 15));
		cardHeaderPanel.setOpaque(false);

		// Tabs (Courses | Schedules)
		JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
		tabPanel.setOpaque(false);
		tabPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LINE_COLOR));

		JLabel tabCourses = new JLabel("Courses");
		tabCourses.setFont(new Font("Arial", Font.BOLD, 15));
		tabCourses.setForeground(DARK_TEAL);
		tabCourses.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, ACCENT_GREEN));

		JLabel tabSchedules = new JLabel("Schedules");
		tabSchedules.setFont(new Font("Arial", Font.PLAIN, 15));
		tabSchedules.setForeground(Color.GRAY);

		tabPanel.add(tabCourses);
		tabPanel.add(tabSchedules);
		cardHeaderPanel.add(tabPanel, BorderLayout.NORTH);

		// Filter Controls Row (Search Box + Department Dropdown)
		JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
		filterPanel.setOpaque(false);

		txtSearch = new JTextField(" Search course code or title...");
		txtSearch.setFont(new Font("Arial", Font.PLAIN, 13));
		txtSearch.setForeground(Color.GRAY);
		txtSearch.setPreferredSize(new Dimension(350, 38));
		txtSearch.setBorder(new CompoundBorder(new RoundedBorder(FIELD_BORDER, 8), new EmptyBorder(0, 10, 0, 10)));
		filterPanel.add(txtSearch);

		JComboBox<String> cmbDepartment = new JComboBox<String>();
		cmbDepartment.setModel(new DefaultComboBoxModel<String>(new String[] {
			"All Departments", "CS", "IT", "Math", "English", "PE"
		}));
		cmbDepartment.setFont(new Font("Arial", Font.PLAIN, 13));
		cmbDepartment.setBackground(Color.WHITE);
		cmbDepartment.setPreferredSize(new Dimension(180, 38));
		filterPanel.add(cmbDepartment);

		cardHeaderPanel.add(filterPanel, BorderLayout.SOUTH);
		cardPanel.add(cardHeaderPanel, BorderLayout.NORTH);

		// Table Component
		String[] columnNames = {"Course Code", "Title", "Units", "Department", "Actions"};
		Object[][] data = {
			{"CCS101", "Introduction to Computing", "3", "CS", "[Edit] [Delete]"},
			{"IT102", "Web Development", "3", "IT", "[Edit] [Delete]"},
			{"MATH101", "College Algebra", "3", "Math", "[Edit] [Delete]"},
			{"ENG101", "English for Academic Purposes", "3", "English", "[Edit] [Delete]"},
			{"PE101", "Physical Education", "2", "PE", "[Edit] [Delete]"}
		};

		DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tblCourses = new JTable(tableModel);
		tblCourses.setFont(new Font("Arial", Font.PLAIN, 13));
		tblCourses.setRowHeight(42);
		tblCourses.setShowGrid(false);
		tblCourses.setIntercellSpacing(new Dimension(0, 0));
		tblCourses.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
		tblCourses.getTableHeader().setBackground(Color.WHITE);
		tblCourses.getTableHeader().setForeground(DARK_TEAL);
		tblCourses.getTableHeader().setReorderingAllowed(false);

		// Center align units & actions columns
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		tblCourses.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		tblCourses.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

		JScrollPane scrollPane = new JScrollPane(tblCourses);
		scrollPane.getViewport().setBackground(Color.WHITE);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		cardPanel.add(scrollPane, BorderLayout.CENTER);

		// Pagination Footer Row
		JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		paginationPanel.setOpaque(false);

		JButton btnPrev = new JButton("<");
		stylePageButton(btnPrev, false);
		paginationPanel.add(btnPrev);

		JButton btn1 = new JButton("1");
		stylePageButton(btn1, true);
		paginationPanel.add(btn1);

		JButton btn2 = new JButton("2");
		stylePageButton(btn2, false);
		paginationPanel.add(btn2);

		JButton btn3 = new JButton("3");
		stylePageButton(btn3, false);
		paginationPanel.add(btn3);

		JButton btnNext = new JButton(">");
		stylePageButton(btnNext, false);
		paginationPanel.add(btnNext);

		cardPanel.add(paginationPanel, BorderLayout.SOUTH);
	}

	/** Helper method for page buttons styling. */
	private void stylePageButton(JButton btn, boolean active) {
		btn.setPreferredSize(new Dimension(32, 32));
		btn.setFont(new Font("Arial", Font.PLAIN, 12));
		btn.setFocusPainted(false);
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		if (active) {
			btn.setForeground(Color.WHITE);
			btn.setBackground(DARK_TEAL);
			btn.setOpaque(true);
			btn.setBorder(BorderFactory.createEmptyBorder());
		} else {
			btn.setForeground(Color.DARK_GRAY);
			btn.setBackground(Color.WHITE);
			btn.setBorder(new RoundedBorder(FIELD_BORDER, 6));
		}
	}

	// =================================================================
	// CUSTOM DRAWING CLASSES
	// =================================================================

	/** Panel with rounded background. */
	static class RoundedPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private Color bgColor;
		private int arc;

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

	/** Rounded border outline. */
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