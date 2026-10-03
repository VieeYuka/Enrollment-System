package oopSource;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PaymentFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private String loggedInUser;
	private String loggedInRole;
	private TuitionFrame.TuitionRecord record;

	private JComboBox<String> cmbMethod;
	private JTextField txtAmount;
	private JLabel lblChange;
	
	private static final String PESO = TuitionFrame.PESO;

	public PaymentFrame(String username, TuitionFrame.TuitionRecord record, String role) {
		this.loggedInUser = username;
		this.record = record;
		this.loggedInRole = role;
		setTitle("Rey University - Process Payment");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1280, 720);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		// ---- Left sidebar (shared with TuitionFrame) ----
		contentPane.add(TuitionFrame.buildSidebar(this, loggedInUser, loggedInRole), BorderLayout.WEST);

		// =============================================================
		// RIGHT MAIN CONTENT AREA
		// =============================================================
		JPanel mainContentPanel = new JPanel();
		mainContentPanel.setBackground(TuitionFrame.LIGHT_BG);
		mainContentPanel.setLayout(new BorderLayout(0, 0));
		contentPane.add(mainContentPanel, BorderLayout.CENTER);

		// ---- Top bar: "Cashier" + staff ----
		JPanel topBar = new JPanel();
		topBar.setBackground(Color.WHITE);
		topBar.setPreferredSize(new Dimension(0, 70));
		topBar.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
				new EmptyBorder(0, 30, 0, 30)));
		topBar.setLayout(new BorderLayout());
		mainContentPanel.add(topBar, BorderLayout.NORTH);

		JLabel lblCashier = new JLabel("Cashier");
		lblCashier.setFont(new Font("Arial", Font.BOLD, 20));
		topBar.add(lblCashier, BorderLayout.WEST);

		JLabel lblStaff = new JLabel(loggedInUser);
		lblStaff.setFont(new Font("Arial", Font.PLAIN, 13));
		lblStaff.setForeground(Color.GRAY);
		topBar.add(lblStaff, BorderLayout.EAST);

		// ---- Body ----
		JPanel body = new JPanel();
		body.setOpaque(false);
		body.setBorder(new EmptyBorder(25, 25, 25, 25));
		body.setLayout(new BorderLayout(0, 20));
		mainContentPanel.add(body, BorderLayout.CENTER);

		JLabel lblHeader = new JLabel("Process Payment");
		lblHeader.setFont(new Font("Arial", Font.BOLD, 26));
		lblHeader.setForeground(TuitionFrame.DARK_TEAL);
		body.add(lblHeader, BorderLayout.NORTH);

		StudentsFrame.RoundedPanel card = new StudentsFrame.RoundedPanel(Color.WHITE, 20);
		card.setLayout(new BorderLayout(0, 20));
		card.setBorder(new EmptyBorder(25, 30, 25, 30));
		body.add(card, BorderLayout.CENTER);

		card.add(buildInfoSection(), BorderLayout.NORTH);

		JPanel fieldsWrapper = new JPanel(new BorderLayout());
		fieldsWrapper.setOpaque(false);
		fieldsWrapper.add(buildFieldsSection(), BorderLayout.NORTH);
		card.add(fieldsWrapper, BorderLayout.CENTER);

		card.add(buildButtonSection(), BorderLayout.SOUTH);

		updateChange();
	}

	// =================================================================
	// STUDENT INFO + TOTAL DUE + PAID INDICATOR
	// =================================================================
	private JPanel buildInfoSection() {
		JPanel info = new JPanel(new BorderLayout(0, 15));
		info.setOpaque(false);

		// ---- Header: photo, name, details, paid badge ----
		JPanel header = new JPanel(new GridBagLayout());
		header.setOpaque(false);
		info.add(header, BorderLayout.NORTH);

		JLabel lblPhoto = new JLabel("", SwingConstants.CENTER);
		lblPhoto.setPreferredSize(new Dimension(80, 80));
		URL photoUrl = this.getClass().getResource("/Profile4.png");
		if (photoUrl != null) {
			Image photoImg = new ImageIcon(photoUrl).getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
			lblPhoto.setIcon(new ImageIcon(photoImg));
		} else {
			lblPhoto.setText("PHOTO");
			lblPhoto.setOpaque(true);
			lblPhoto.setBackground(new Color(200, 205, 208));
			lblPhoto.setForeground(Color.WHITE);
			lblPhoto.setFont(new Font("Arial", Font.BOLD, 12));
		}
		GridBagConstraints gbc_photo = new GridBagConstraints();
		gbc_photo.gridx = 0;
		gbc_photo.gridy = 0;
		gbc_photo.gridheight = 2;
		gbc_photo.insets = new Insets(0, 0, 0, 20);
		header.add(lblPhoto, gbc_photo);

		JLabel lblName = new JLabel(record.name);
		lblName.setFont(new Font("Arial", Font.BOLD, 20));
		lblName.setForeground(TuitionFrame.DARK_TEAL);
		GridBagConstraints gbc_name = new GridBagConstraints();
		gbc_name.gridx = 1;
		gbc_name.gridy = 0;
		gbc_name.weightx = 1.0;
		gbc_name.anchor = GridBagConstraints.SOUTHWEST;
		gbc_name.insets = new Insets(0, 0, 4, 0);
		header.add(lblName, gbc_name);

		JLabel lblDetails = new JLabel(record.studentId + "   |   " + record.course + "   |   " + record.yearLevel);
		lblDetails.setFont(new Font("Arial", Font.PLAIN, 14));
		lblDetails.setForeground(Color.GRAY);
		GridBagConstraints gbc_details = new GridBagConstraints();
		gbc_details.gridx = 1;
		gbc_details.gridy = 1;
		gbc_details.weightx = 1.0;
		gbc_details.anchor = GridBagConstraints.NORTHWEST;
		header.add(lblDetails, gbc_details);

		// Paid indicator (only when already paid)
		if (record.paid) {
			JPanel badgePanel = new JPanel(new GridLayout(2, 1, 0, 4));
			badgePanel.setOpaque(false);

			JLabel lblBadge = new JLabel("PAID", SwingConstants.CENTER);
			lblBadge.setOpaque(true);
			lblBadge.setBackground(TuitionFrame.PAID_BG);
			lblBadge.setForeground(TuitionFrame.PAID_GREEN);
			lblBadge.setFont(new Font("Arial", Font.BOLD, 16));
			lblBadge.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(TuitionFrame.PAID_GREEN, 2),
					new EmptyBorder(6, 24, 6, 24)));
			badgePanel.add(lblBadge);

			JLabel lblDate = new JLabel("Paid on " + record.datePaid, SwingConstants.CENTER);
			lblDate.setFont(new Font("Arial", Font.PLAIN, 12));
			lblDate.setForeground(Color.GRAY);
			badgePanel.add(lblDate);

			GridBagConstraints gbc_badge = new GridBagConstraints();
			gbc_badge.gridx = 2;
			gbc_badge.gridy = 0;
			gbc_badge.gridheight = 2;
			gbc_badge.anchor = GridBagConstraints.NORTHEAST;
			header.add(badgePanel, gbc_badge);
		}

		// ---- Total due (units x rate) ----
		JPanel totals = new JPanel(new GridBagLayout());
		totals.setOpaque(false);
		totals.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(230, 230, 230)),
				new EmptyBorder(12, 0, 12, 0)));
		info.add(totals, BorderLayout.CENTER);

		JLabel lblDueCaption = new JLabel("Total Due");
		lblDueCaption.setFont(new Font("Arial", Font.PLAIN, 13));
		lblDueCaption.setForeground(Color.GRAY);
		GridBagConstraints gbc_dueCap = new GridBagConstraints();
		gbc_dueCap.gridx = 0;
		gbc_dueCap.gridy = 0;
		gbc_dueCap.anchor = GridBagConstraints.WEST;
		totals.add(lblDueCaption, gbc_dueCap);

		JLabel lblDue = new JLabel(PESO + TuitionFrame.MONEY.format(record.getTotalDue()));
		lblDue.setFont(new Font("Arial", Font.BOLD, 26));
		lblDue.setForeground(Color.BLACK);
		GridBagConstraints gbc_due = new GridBagConstraints();
		gbc_due.gridx = 0;
		gbc_due.gridy = 1;
		gbc_due.anchor = GridBagConstraints.WEST;
		totals.add(lblDue, gbc_due);

		JLabel lblBreakdown = new JLabel(record.units + " units  x  " + PESO
				+ TuitionFrame.MONEY.format(TuitionFrame.RATE_PER_UNIT) + " per unit");
		lblBreakdown.setFont(new Font("Arial", Font.PLAIN, 13));
		lblBreakdown.setForeground(Color.GRAY);
		GridBagConstraints gbc_break = new GridBagConstraints();
		gbc_break.gridx = 1;
		gbc_break.gridy = 1;
		gbc_break.weightx = 1.0;
		gbc_break.anchor = GridBagConstraints.SOUTHWEST;
		gbc_break.insets = new Insets(0, 20, 5, 0);
		totals.add(lblBreakdown, gbc_break);

		return info;
	}

	// =================================================================
	// PAYMENT METHOD / AMOUNT RECEIVED / CHANGE
	// =================================================================
	private JPanel buildFieldsSection() {
		JPanel row = new JPanel(new GridLayout(1, 3, 25, 0));
		row.setOpaque(false);

		// Payment method
		cmbMethod = new JComboBox<String>(new String[] {"Cash", "GCash", "Credit / Debit Card", "Bank Transfer"});
		cmbMethod.setFont(new Font("Arial", Font.PLAIN, 14));
		cmbMethod.setPreferredSize(new Dimension(0, 42));
		row.add(captioned("Payment Method", cmbMethod));

		// Amount received (with peso prefix)
		JPanel amountBox = new JPanel(new BorderLayout());
		amountBox.setOpaque(false);
		JLabel lblPrefix = new JLabel(PESO, SwingConstants.CENTER);
		lblPrefix.setOpaque(true);
		lblPrefix.setBackground(new Color(240, 243, 244));
		lblPrefix.setForeground(Color.GRAY);
		lblPrefix.setFont(new Font("Arial", Font.BOLD, 14));
		lblPrefix.setPreferredSize(new Dimension(36, 42));
		amountBox.add(lblPrefix, BorderLayout.WEST);

		txtAmount = new JTextField();
		txtAmount.setFont(new Font("Arial", Font.PLAIN, 14));
		txtAmount.setPreferredSize(new Dimension(0, 42));
		txtAmount.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { updateChange(); }
			public void removeUpdate(DocumentEvent e) { updateChange(); }
			public void changedUpdate(DocumentEvent e) { updateChange(); }
		});
		amountBox.add(txtAmount, BorderLayout.CENTER);
		row.add(captioned("Amount Received", amountBox));

		// Change
		lblChange = new JLabel(PESO + "0.00");
		lblChange.setFont(new Font("Arial", Font.BOLD, 18));
		lblChange.setPreferredSize(new Dimension(0, 42));
		row.add(captioned("Change", lblChange));

		// Already paid -> show the saved payment, read-only
		if (record.paid) {
			cmbMethod.setSelectedItem(record.paymentMethod);
			cmbMethod.setEnabled(false);
			txtAmount.setText(TuitionFrame.MONEY.format(record.amountReceived));
			txtAmount.setEditable(false);
		}
		return row;
	}

	private JPanel captioned(String caption, java.awt.Component comp) {
		JPanel p = new JPanel(new BorderLayout(0, 6));
		p.setOpaque(false);
		JLabel l = new JLabel(caption);
		l.setFont(new Font("Arial", Font.PLAIN, 13));
		l.setForeground(Color.GRAY);
		p.add(l, BorderLayout.NORTH);
		p.add(comp, BorderLayout.CENTER);
		return p;
	}

	// =================================================================
	// BUTTONS
	// =================================================================
	private JPanel buildButtonSection() {
		JPanel buttons = new JPanel(new GridLayout(1, 2, 25, 0));
		buttons.setOpaque(false);
		buttons.setPreferredSize(new Dimension(0, 48));

		if (record.paid) {
			// Paid: just a way back to the list
			JButton btnBack = new JButton("Back to Tuition");
			styleSecondary(btnBack);
			btnBack.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					TuitionFrame.navigate(PaymentFrame.this, new TuitionFrame(loggedInUser, loggedInRole));
				}
			});
			buttons.add(btnBack);
			buttons.add(new JLabel(""));
			return buttons;
		}

		JButton btnConfirm = new JButton("Confirm Payment");
		btnConfirm.setFont(new Font("Arial", Font.BOLD, 14));
		btnConfirm.setBackground(TuitionFrame.ACCENT_GREEN);
		btnConfirm.setForeground(Color.WHITE);
		btnConfirm.setFocusPainted(false);
		btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnConfirm.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		btnConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				confirmPayment();
			}
		});
		buttons.add(btnConfirm);

		JButton btnCancel = new JButton("Cancel");
		styleSecondary(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TuitionFrame.navigate(PaymentFrame.this, new TuitionFrame(loggedInUser, loggedInRole));
			}
		});
		buttons.add(btnCancel);
		return buttons;
	}

	private void styleSecondary(JButton b) {
		b.setFont(new Font("Arial", Font.BOLD, 14));
		b.setBackground(new Color(243, 245, 246));
		b.setForeground(Color.BLACK);
		b.setFocusPainted(false);
		b.setCursor(new Cursor(Cursor.HAND_CURSOR));
		b.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
	}

	// =================================================================
	// LOGIC
	// =================================================================
	/** Reads the amount field; returns -1 if empty or invalid. */
	private double parseAmount() {
		String t = txtAmount.getText().replace(",", "").trim();
		if (t.isEmpty()) return -1;
		try {
			double v = Double.parseDouble(t);
			return v < 0 ? -1 : v;
		} catch (NumberFormatException ex) {
			return -1;
		}
	}

	private void updateChange() {
		if (record.paid) {
			lblChange.setText(PESO + TuitionFrame.MONEY.format(record.change));
			return;
		}
		double amount = parseAmount();
		double change = amount < 0 ? 0 : Math.max(0, amount - record.getTotalDue());
		lblChange.setText(PESO + TuitionFrame.MONEY.format(change));
	}

	private void confirmPayment() {
		double amount = parseAmount();
		double due = record.getTotalDue();

		if (amount < 0) {
			JOptionPane.showMessageDialog(this, "Please enter a valid amount received.",
					"Invalid Amount", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (amount < due) {
			JOptionPane.showMessageDialog(this,
					"Amount received is less than the total due (" + PESO + TuitionFrame.MONEY.format(due) + ").",
					"Insufficient Payment", JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Mark as paid (TODO: also save to MySQL here)
		record.paid = true;
		record.paymentMethod = String.valueOf(cmbMethod.getSelectedItem());
		record.amountReceived = amount;
		record.change = amount - due;
		record.datePaid = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

		JOptionPane.showMessageDialog(this,
				"Payment confirmed for " + record.name + ".\nChange: " + PESO
						+ TuitionFrame.MONEY.format(record.change),
				"Payment Successful", JOptionPane.INFORMATION_MESSAGE);

		// Back to the tuition table, which now shows "Paid"
		TuitionFrame.navigate(this, new TuitionFrame(loggedInUser, loggedInRole));
	}
}