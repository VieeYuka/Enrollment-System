package oopSource;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class EnrollmentSummaryFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private String loggedInUser;
	private CoursesFrame parent;
	private String loggedInRole;
	private static final Color DARK_TEAL = new Color(11, 55, 49);
	private static final Color LIGHT_BG = new Color(235, 235, 235);
	private static final Color ACCENT_GREEN = new Color(38, 128, 98);

	/**
	 * @param rows each row = {code, title, units, department}
	 * @param parent the hidden CoursesFrame, restored when "Back" is pressed
	 */
	public EnrollmentSummaryFrame(String username, List<Object[]> rows, CoursesFrame parent, String role) {
		this.loggedInUser = username;
		this.parent = parent;
		this.loggedInRole = role;

		setTitle("Rey University - Enrollment Summary");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1280, 720);

		contentPane = new JPanel();
		contentPane.setBackground(LIGHT_BG);
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		// ---- Top bar ----
		JPanel topBar = new JPanel(new BorderLayout());
		topBar.setBackground(DARK_TEAL);
		topBar.setPreferredSize(new Dimension(0, 70));
		topBar.setBorder(new EmptyBorder(0, 30, 0, 30));
		contentPane.add(topBar, BorderLayout.NORTH);

		// Logo on the left, then the university name
		JPanel brand = new JPanel(new GridBagLayout());
		brand.setOpaque(false);
		GridBagConstraints gbc_logo = new GridBagConstraints();
		gbc_logo.gridx = 0;
		gbc_logo.gridy = 0;
		gbc_logo.insets = new Insets(0, 0, 0, 12);
		brand.add(Sidebar.createLogo(45), gbc_logo);

		JLabel lblUni = new JLabel("REY UNIVERSITY");
		lblUni.setForeground(Color.WHITE);
		lblUni.setFont(new Font("Arial", Font.BOLD, 18));
		GridBagConstraints gbc_uni = new GridBagConstraints();
		gbc_uni.gridx = 1;
		gbc_uni.gridy = 0;
		brand.add(lblUni, gbc_uni);
		topBar.add(brand, BorderLayout.WEST);

		JLabel lblUser = new JLabel(loggedInUser + " (" + Roles.displayName(loggedInRole) + ")");
		lblUser.setForeground(new Color(180, 200, 195));
		lblUser.setFont(new Font("Arial", Font.PLAIN, 13));
		topBar.add(lblUser, BorderLayout.EAST);

		// ---- Body ----
		JPanel body = new JPanel(new BorderLayout(0, 20));
		body.setOpaque(false);
		body.setBorder(new EmptyBorder(25, 40, 25, 40));
		contentPane.add(body, BorderLayout.CENTER);

		JLabel lblHeader = new JLabel("Enrollment Summary");
		lblHeader.setFont(new Font("Arial", Font.BOLD, 26));
		lblHeader.setForeground(DARK_TEAL);
		body.add(lblHeader, BorderLayout.NORTH);

		// ---- Table card ----
		StudentsFrame.RoundedPanel card = new StudentsFrame.RoundedPanel(Color.WHITE, 20);
		card.setLayout(new BorderLayout(0, 15));
		card.setBorder(new EmptyBorder(20, 20, 20, 20));
		body.add(card, BorderLayout.CENTER);

		JLabel lblTableTitle = new JLabel("Selected Subjects");
		lblTableTitle.setFont(new Font("Arial", Font.BOLD, 18));
		lblTableTitle.setForeground(DARK_TEAL);
		card.add(lblTableTitle, BorderLayout.NORTH);

		String[] cols = {"Course Code", "Title", "Units", "Department"};
		DefaultTableModel model = new DefaultTableModel(cols, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		int totalUnits = 0;
		for (Object[] r : rows) {
			model.addRow(r);
			totalUnits += Integer.parseInt(String.valueOf(r[2]));
		}

		JTable table = new JTable(model);
		table.setFont(new Font("Arial", Font.PLAIN, 13));
		table.setRowHeight(38);
		table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
		table.getTableHeader().setBackground(new Color(240, 240, 240));
		table.getTableHeader().setForeground(DARK_TEAL);
		table.getTableHeader().setReorderingAllowed(false);
		table.setShowVerticalLines(false);
		table.setGridColor(new Color(230, 230, 230));

		DefaultTableCellRenderer center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);
		table.getColumnModel().getColumn(2).setCellRenderer(center);

		JScrollPane scroll = new JScrollPane(table);
		scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
		scroll.getViewport().setBackground(Color.WHITE);
		card.add(scroll, BorderLayout.CENTER);

		// ---- Totals + buttons ----
		JPanel bottom = new JPanel(new GridBagLayout());
		bottom.setOpaque(false);
		card.add(bottom, BorderLayout.SOUTH);

		JLabel lblTotals = new JLabel("Total Subjects: " + rows.size() + "     |     Total Units: " + totalUnits);
		lblTotals.setFont(new Font("Arial", Font.BOLD, 15));
		lblTotals.setForeground(DARK_TEAL);
		GridBagConstraints gbc_totals = new GridBagConstraints();
		gbc_totals.gridx = 0;
		gbc_totals.gridy = 0;
		gbc_totals.weightx = 1.0;
		gbc_totals.anchor = GridBagConstraints.WEST;
		bottom.add(lblTotals, gbc_totals);

		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Arial", Font.BOLD, 13));
		btnBack.setBackground(Color.WHITE);
		btnBack.setForeground(DARK_TEAL);
		btnBack.setFocusPainted(false);
		btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnBack.setPreferredSize(new Dimension(110, 38));
		btnBack.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EnrollmentSummaryFrame.this.parent.setBounds(getBounds());
				EnrollmentSummaryFrame.this.parent.setVisible(true);
				dispose();
			}
		});
		GridBagConstraints gbc_back = new GridBagConstraints();
		gbc_back.gridx = 1;
		gbc_back.gridy = 0;
		gbc_back.insets = new Insets(0, 0, 0, 10);
		bottom.add(btnBack, gbc_back);

		JButton btnConfirm = new JButton("Confirm Enrollment");
		btnConfirm.setFont(new Font("Arial", Font.BOLD, 13));
		btnConfirm.setBackground(ACCENT_GREEN);
		btnConfirm.setForeground(Color.WHITE);
		btnConfirm.setFocusPainted(false);
		btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnConfirm.setPreferredSize(new Dimension(180, 38));
		btnConfirm.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		btnConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				confirmEnrollment(rows);
			}
		});
		GridBagConstraints gbc_confirm = new GridBagConstraints();
		gbc_confirm.gridx = 2;
		gbc_confirm.gridy = 0;
		bottom.add(btnConfirm, gbc_confirm);
	}

	/**
	 * Saves the enrollment as "Pending" (EnrollmentService does the database work),
	 * then moves on: students / applicants go to the Exit screen and the system closes,
	 * staff go back to their own home screen.
	 */
	private void confirmEnrollment(List<Object[]> rows) {
		if (EnrollmentService.submit(Session.studentKey(), Session.fullName(), Session.course(), Session.yearLevel(), rows) == null) {
			return; // not saved - stay here so nothing is lost
		}

		JFrame next;
		if (Roles.endsSessionAfterEnrollment(loggedInRole)) {
			next = new ExitFrame(Session.studentKey());
		} else {
			JOptionPane.showMessageDialog(this, "Enrollment submitted successfully!", "Success",
					JOptionPane.INFORMATION_MESSAGE);
			Session.clearEnrollTarget();
			next = Roles.landingFrame(loggedInUser, loggedInRole);
		}
		next.setBounds(getBounds());
		next.setVisible(true);
		parent.dispose();
		dispose();
	}
}
