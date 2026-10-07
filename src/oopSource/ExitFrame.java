package oopSource;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

/**
 * Last screen of the enrollment flow:  "Thank you for using the system"
 * plus the student's enrollment status (Pending or Enrolled).
 *
 * The status badge refreshes itself every few seconds from EnrollmentService,
 * so when the cashier confirms the payment it flips from Pending to Enrolled.
 * It can also be changed from code:  exitFrame.setEnrollmentStatus("Enrolled");
 *
 * Same look as the login screen (dark teal left panel, grey right panel).
 */
public class ExitFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final Color DARK_TEAL = new Color(11, 55, 49);
	private static final Color LIGHT_BG = new Color(235, 235, 235);
	private static final Color PENDING_FG = new Color(156, 101, 0);
	private static final Color PENDING_BG = new Color(255, 243, 205);
	private static final int REFRESH_MS = 5000;

	private final String studentKey;
	private final JLabel lblStatus = new JLabel("", SwingConstants.CENTER);
	private final Timer refreshTimer;

	/** @param studentKey the id used by EnrollmentService (Session.studentKey()) */
	public ExitFrame(String studentKey) {
		this.studentKey = studentKey;

		setTitle("Rey University - Thank You");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1280, 720);

		JPanel contentPane = new JPanel(new GridBagLayout());
		setContentPane(contentPane);

		contentPane.add(buildLeftPanel(), panelConstraints(0, 0.35));
		contentPane.add(buildRightPanel(), panelConstraints(1, 0.65));

		refreshStatus();
		refreshTimer = new Timer(REFRESH_MS, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshStatus();
			}
		});
		refreshTimer.start();
	}

	private GridBagConstraints panelConstraints(int column, double weight) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = column;
		gbc.gridy = 0;
		gbc.weightx = weight;
		gbc.weighty = 1.0;
		return gbc;
	}

	// ---- left: logo + university name (same as the login screen) ----
	private JPanel buildLeftPanel() {
		JPanel left = new JPanel(new GridBagLayout());
		left.setBackground(DARK_TEAL);

		GridBagConstraints gbcLogo = new GridBagConstraints();
		gbcLogo.gridx = 0;
		gbcLogo.gridy = 0;
		gbcLogo.insets = new Insets(10, 20, 10, 20);
		left.add(Sidebar.createLogo(150), gbcLogo);

		JLabel lblTitle = new JLabel("REY UNIVERSITY", SwingConstants.CENTER);
		lblTitle.setForeground(Color.WHITE);
		lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
		GridBagConstraints gbcTitle = new GridBagConstraints();
		gbcTitle.gridx = 0;
		gbcTitle.gridy = 1;
		gbcTitle.insets = new Insets(30, 20, 5, 20);
		left.add(lblTitle, gbcTitle);

		JLabel lblSubtitle = new JLabel("ENROLLMENT SYSTEM", SwingConstants.CENTER);
		lblSubtitle.setForeground(new Color(200, 220, 215));
		lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 16));
		GridBagConstraints gbcSub = new GridBagConstraints();
		gbcSub.gridx = 0;
		gbcSub.gridy = 2;
		gbcSub.insets = new Insets(0, 20, 50, 20);
		left.add(lblSubtitle, gbcSub);
		return left;
	}

	// ---- right: thank you + enrollment status + exit ----
	private JPanel buildRightPanel() {
		JPanel right = new JPanel(new GridBagLayout());
		right.setBackground(LIGHT_BG);

		JLabel lblThanks = new JLabel("Thank you for using the system!", SwingConstants.CENTER);
		lblThanks.setFont(new Font("Arial", Font.BOLD, 32));
		lblThanks.setForeground(DARK_TEAL);
		GridBagConstraints gbcThanks = new GridBagConstraints();
		gbcThanks.gridx = 0;
		gbcThanks.gridy = 0;
		gbcThanks.insets = new Insets(0, 40, 40, 40);
		right.add(lblThanks, gbcThanks);

		JLabel lblCaption = new JLabel("Enrollment Status", SwingConstants.CENTER);
		lblCaption.setFont(new Font("Arial", Font.PLAIN, 16));
		lblCaption.setForeground(Color.GRAY);
		GridBagConstraints gbcCaption = new GridBagConstraints();
		gbcCaption.gridx = 0;
		gbcCaption.gridy = 1;
		gbcCaption.insets = new Insets(0, 0, 10, 0);
		right.add(lblCaption, gbcCaption);

		lblStatus.setOpaque(true);
		lblStatus.setFont(new Font("Arial", Font.BOLD, 24));
		lblStatus.setPreferredSize(new Dimension(220, 60));
		GridBagConstraints gbcStatus = new GridBagConstraints();
		gbcStatus.gridx = 0;
		gbcStatus.gridy = 2;
		gbcStatus.insets = new Insets(0, 0, 50, 0);
		right.add(lblStatus, gbcStatus);

		JButton btnExit = new JButton("Exit System");
		btnExit.setFont(new Font("Arial", Font.BOLD, 16));
		btnExit.setBackground(DARK_TEAL);
		btnExit.setForeground(Color.WHITE);
		btnExit.setFocusPainted(false);
		btnExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnExit.setPreferredSize(new Dimension(220, 45));
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		GridBagConstraints gbcExit = new GridBagConstraints();
		gbcExit.gridx = 0;
		gbcExit.gridy = 3;
		right.add(btnExit, gbcExit);
		return right;
	}

	// =================================================================
	// STATUS
	// =================================================================

	/** Reads the current status from the database layer and shows it. */
	private void refreshStatus() {
		setEnrollmentStatus(EnrollmentService.getStatus(studentKey));
	}

	/** Shows Enrolled (green), Denied (red) or anything else as Pending (amber). */
	public void setEnrollmentStatus(String status) {
		boolean enrolled = EnrollmentService.ENROLLED.equalsIgnoreCase(status);
		boolean denied = EnrollmentService.DENIED.equalsIgnoreCase(status);
		Color fg = enrolled ? TuitionFrame.PAID_GREEN : denied ? TuitionFrame.UNPAID_RED : PENDING_FG;
		Color bg = enrolled ? TuitionFrame.PAID_BG : denied ? TuitionFrame.UNPAID_BG : PENDING_BG;

		lblStatus.setText(enrolled ? EnrollmentService.ENROLLED : denied ? EnrollmentService.DENIED : EnrollmentService.PENDING);
		lblStatus.setForeground(fg);
		lblStatus.setBackground(bg);
		lblStatus.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(fg, 2), new EmptyBorder(6, 24, 6, 24)));
	}

	@Override
	public void dispose() {
		refreshTimer.stop();
		super.dispose();
	}
}
