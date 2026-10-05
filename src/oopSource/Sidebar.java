package oopSource;

import java.awt.BorderLayout;
import java.awt.Color;
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

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * The dark teal sidebar shared by every screen.
 * Which menu items appear is decided by Roles.canSee(role, item).
 *
 * Usage inside a frame:
 *     contentPane.add(Sidebar.build(this, user, role, Roles.NAV_STUDENTS), BorderLayout.WEST);
 */
public final class Sidebar {

	static final Color DARK_TEAL = new Color(11, 55, 49);

	private Sidebar() {
	}

	/** Shows the next frame at the same position / size and closes the current one. */
	public static void navigate(JFrame from, JFrame next) {
		next.setBounds(from.getBounds());
		next.setVisible(true);
		from.dispose();
	}

	/** Creates the REY UNIVERSITY logo label (falls back to a "LOGO" box if the image is missing). */
	public static JLabel createLogo(int size) {
		JLabel lblLogo = new JLabel("");
		lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
		URL imgUrl = Sidebar.class.getResource("/RUlogo (1).png");
		if (imgUrl != null) {
			Image img = new ImageIcon(imgUrl).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
			lblLogo.setIcon(new ImageIcon(img));
		} else {
			lblLogo.setText("LOGO");
			lblLogo.setFont(new Font("Arial", Font.BOLD, 10));
			lblLogo.setForeground(DARK_TEAL);
			lblLogo.setOpaque(true);
			lblLogo.setBackground(Color.WHITE);
			lblLogo.setPreferredSize(new Dimension(size, size));
		}
		return lblLogo;
	}

	/**
	 * @param owner     the frame that shows the sidebar
	 * @param activeNav which item is highlighted (one of Roles.NAV_*)
	 */
	public static JPanel build(final JFrame owner, final String user, final String role, String activeNav) {
		JPanel sidebar = new JPanel(new BorderLayout(0, 0));
		sidebar.setBackground(DARK_TEAL);
		sidebar.setPreferredSize(new Dimension(280, 720));

		sidebar.add(buildLogoPanel(), BorderLayout.NORTH);
		sidebar.add(buildMenu(owner, user, role, activeNav), BorderLayout.CENTER);
		sidebar.add(buildProfile(user, role), BorderLayout.SOUTH);
		return sidebar;
	}

	// ---- logo + title ----
	private static JPanel buildLogoPanel() {
		JPanel logoPanel = new JPanel(new GridBagLayout());
		logoPanel.setOpaque(false);
		logoPanel.setBorder(new EmptyBorder(20, 15, 20, 15));

		GridBagConstraints gbcLogo = new GridBagConstraints();
		gbcLogo.insets = new Insets(0, 5, 0, 10);
		gbcLogo.gridx = 0;
		gbcLogo.gridy = 0;
		logoPanel.add(createLogo(45), gbcLogo);

		JLabel lblTitle = new JLabel("REY UNIVERSITY");
		lblTitle.setForeground(Color.WHITE);
		lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
		GridBagConstraints gbcTitle = new GridBagConstraints();
		gbcTitle.insets = new Insets(0, 5, 0, 10);
		gbcTitle.weightx = 1.0;
		gbcTitle.fill = GridBagConstraints.HORIZONTAL;
		gbcTitle.gridx = 1;
		gbcTitle.gridy = 0;
		logoPanel.add(lblTitle, gbcTitle);
		return logoPanel;
	}

	// ---- menu (only the items the role is allowed to see) ----
	private static JPanel buildMenu(JFrame owner, String user, String role, String activeNav) {
		JPanel menu = new JPanel(new GridLayout(8, 1, 0, 10));
		menu.setOpaque(false);
		menu.setBorder(new EmptyBorder(10, 15, 10, 15));

		for (final String nav : Roles.ALL_NAV) {
			if (!Roles.canSee(role, nav)) {
				continue;
			}
			boolean active = nav.equals(activeNav);
			StudentsFrame.NavItem item = new StudentsFrame.NavItem(nav, active);
			if (!active) { // the current page needs no click action
				item.addActionListener(new NavClick(owner, user, role, nav));
			}
			menu.add(item);
		}
		return menu;
	}

	/** Opens the screen that belongs to a menu item. */
	private static class NavClick implements ActionListener {
		private final JFrame owner;
		private final String user, role, nav;

		NavClick(JFrame owner, String user, String role, String nav) {
			this.owner = owner;
			this.user = user;
			this.role = role;
			this.nav = nav;
		}

		public void actionPerformed(ActionEvent e) {
			navigate(owner, createFrame());
		}

		private JFrame createFrame() {
			if (nav.equals(Roles.NAV_DASHBOARD)) return new DashboardFrame(user, role);
			if (nav.equals(Roles.NAV_STUDENTS)) return new StudentsFrame(user, role);
			if (nav.equals(Roles.NAV_ENROLLMENT)) return new EnrollmentStudent(user, role);
			if (nav.equals(Roles.NAV_COURSES)) return new CoursesFrame(user, role);
			return new TuitionFrame(user, role);
		}
	}

	// ---- bottom user profile ----
	private static JPanel buildProfile(String user, String role) {
		JPanel profile = new JPanel(new GridBagLayout());
		profile.setOpaque(false);
		profile.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(255, 255, 255, 40)),
				new EmptyBorder(15, 15, 15, 15)));

		JLabel lblPfp = new JLabel("", SwingConstants.CENTER);
		lblPfp.setPreferredSize(new Dimension(40, 40));
		URL pfpUrl = Sidebar.class.getResource("/Profile1.png");
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
		GridBagConstraints gbcPfp = new GridBagConstraints();
		gbcPfp.gridheight = 2;
		gbcPfp.insets = new Insets(0, 0, 0, 12);
		gbcPfp.gridx = 0;
		gbcPfp.gridy = 0;
		profile.add(lblPfp, gbcPfp);

		JLabel lblUsername = new JLabel(user);
		lblUsername.setForeground(Color.WHITE);
		lblUsername.setFont(new Font("Arial", Font.BOLD, 15));
		GridBagConstraints gbcUser = new GridBagConstraints();
		gbcUser.fill = GridBagConstraints.HORIZONTAL;
		gbcUser.weightx = 1.0;
		gbcUser.insets = new Insets(0, 0, 2, 0);
		gbcUser.gridx = 1;
		gbcUser.gridy = 0;
		profile.add(lblUsername, gbcUser);

		JLabel lblRole = new JLabel(Roles.displayName(role));
		lblRole.setForeground(new Color(180, 200, 195));
		lblRole.setFont(new Font("Arial", Font.PLAIN, 12));
		GridBagConstraints gbcRole = new GridBagConstraints();
		gbcRole.fill = GridBagConstraints.HORIZONTAL;
		gbcRole.weightx = 1.0;
		gbcRole.gridx = 1;
		gbcRole.gridy = 1;
		profile.add(lblRole, gbcRole);
		return profile;
	}
}
