package oopSource;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Pop-up shown when a row of the Students table is clicked.
 *
 *   Modify Info    -> unlocks the fields; the button then becomes "Save Changes"
 *   Delete Record  -> asks for confirmation, then removes the student
 *
 * All data work goes through StudentService, so MySQL can be added there
 * without touching this class.
 */
public class StudentDetailDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private static final Color DARK_TEAL = new Color(11, 55, 49);
	private static final Color ACCENT_GREEN = new Color(38, 128, 98);
	private static final Color DELETE_RED = new Color(192, 57, 43);
	private static final Color FIELD_BORDER = new Color(210, 220, 218);
	private static final Color LOCKED_BG = new Color(245, 247, 247);

	private final StudentService.StudentRecord record;
	private final Runnable onChanged; // called after a save or delete so the table can reload

	private JTextField txtFirstName;
	private JTextField txtLastName;
	private JTextField txtEmail;
	private JComboBox<String> cmbCourse;
	private JComboBox<String> cmbYearLevel;
	private JComboBox<String> cmbStatus;
	private JButton btnModify;
	private JButton btnClose;
	private boolean editing = false;

	public StudentDetailDialog(JFrame owner, StudentService.StudentRecord record, Runnable onChanged) {
		super(owner, "Student Information", true);
		this.record = record;
		this.onChanged = onChanged;

		JPanel content = new JPanel(new BorderLayout(0, 0));
		content.setBackground(Color.WHITE);
		setContentPane(content);

		content.add(buildHeader(), BorderLayout.NORTH);
		content.add(buildForm(), BorderLayout.CENTER);
		content.add(buildButtons(), BorderLayout.SOUTH);

		showRecord();
		setEditing(false);

		setSize(560, 520);
		setResizable(false);
		setLocationRelativeTo(owner);
	}

	// =================================================================
	// LAYOUT
	// =================================================================
	private JPanel buildHeader() {
		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 233, 232)),
				new EmptyBorder(20, 30, 16, 30)));

		JLabel lblTitle = new JLabel("Student Information");
		lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
		lblTitle.setForeground(DARK_TEAL);
		header.add(lblTitle, BorderLayout.WEST);

		JLabel lblId = new JLabel("ID: " + record.studentId);
		lblId.setFont(new Font("Arial", Font.PLAIN, 14));
		lblId.setForeground(Color.GRAY);
		header.add(lblId, BorderLayout.EAST);
		return header;
	}

	private JPanel buildForm() {
		JPanel form = new JPanel(new GridBagLayout());
		form.setOpaque(false);
		form.setBorder(new EmptyBorder(20, 30, 10, 30));

		txtFirstName = newTextField();
		txtLastName = newTextField();
		txtEmail = newTextField();
		cmbCourse = newCombo(StudentService.getCourseID());
		cmbYearLevel = newCombo(StudentService.YEAR_LEVELS);
		cmbStatus = newCombo(StudentService.STATUSES);

		addField(form, "First Name", txtFirstName, 0, 0, 1);
		addField(form, "Last Name", txtLastName, 1, 0, 1);
		addField(form, "University Email", txtEmail, 0, 2, 2);
		addField(form, "Course", cmbCourse, 0, 4, 1);
		addField(form, "Year Level", cmbYearLevel, 1, 4, 1);
		addField(form, "Enrollment Status", cmbStatus, 0, 6, 2);

		// pushes everything to the top
		JPanel spacer = new JPanel();
		spacer.setOpaque(false);
		GridBagConstraints filler = new GridBagConstraints();
		filler.gridy = 8;
		filler.weighty = 1.0;
		form.add(spacer, filler);
		return form;
	}

	/** Puts a caption and its field into the grid (caption on row y, field on row y + 1). */
	private void addField(JPanel form, String caption, java.awt.Component field, int x, int y, int width) {
		JLabel lbl = new JLabel(caption);
		lbl.setFont(new Font("Arial", Font.BOLD, 13));
		GridBagConstraints gbcLabel = new GridBagConstraints();
		gbcLabel.gridx = x;
		gbcLabel.gridy = y;
		gbcLabel.gridwidth = width;
		gbcLabel.anchor = GridBagConstraints.WEST;
		gbcLabel.insets = new Insets(0, x == 0 ? 0 : 8, 5, x == 0 && width == 1 ? 8 : 0);
		form.add(lbl, gbcLabel);

		GridBagConstraints gbcField = new GridBagConstraints();
		gbcField.gridx = x;
		gbcField.gridy = y + 1;
		gbcField.gridwidth = width;
		gbcField.weightx = 1.0;
		gbcField.fill = GridBagConstraints.HORIZONTAL;
		gbcField.insets = new Insets(0, x == 0 ? 0 : 8, 16, x == 0 && width == 1 ? 8 : 0);
		form.add(field, gbcField);
	}

	private JTextField newTextField() {
		JTextField t = new JTextField();
		t.setFont(new Font("Arial", Font.PLAIN, 14));
		t.setPreferredSize(new Dimension(0, 40));
		t.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(FIELD_BORDER), new EmptyBorder(0, 10, 0, 10)));
		return t;
	}

	private JComboBox<String> newCombo(String[] items) {
		JComboBox<String> c = new JComboBox<String>(items);
		c.setFont(new Font("Arial", Font.PLAIN, 14));
		c.setBackground(Color.WHITE);
		c.setPreferredSize(new Dimension(0, 40));
		return c;
	}

	private JPanel buildButtons() {
		JPanel bar = new JPanel(new BorderLayout());
		bar.setOpaque(false);
		bar.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 233, 232)),
				new EmptyBorder(14, 30, 14, 30)));

		JButton btnDelete = styledButton("Delete Record", DELETE_RED, Color.WHITE, 140);
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteRecord();
			}
		});
		bar.add(btnDelete, BorderLayout.WEST);

		JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		right.setOpaque(false);

		btnClose = styledButton("Close", Color.WHITE, DARK_TEAL, 100);
		btnClose.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (editing) {
					showRecord();       // throw away unsaved typing
					setEditing(false);
				} else {
					dispose();
				}
			}
		});
		right.add(btnClose);

		btnModify = styledButton("Modify Info", ACCENT_GREEN, Color.WHITE, 140);
		btnModify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (editing) {
					saveChanges();
				} else {
					setEditing(true);
				}
			}
		});
		right.add(btnModify);

		bar.add(right, BorderLayout.EAST);
		return bar;
	}

	private JButton styledButton(String text, Color bg, Color fg, int width) {
		JButton b = new JButton(text);
		b.setFont(new Font("Arial", Font.BOLD, 13));
		b.setBackground(bg);
		b.setForeground(fg);
		b.setOpaque(true);
		b.setBorderPainted(false);
		b.setFocusPainted(false);
		b.setCursor(new Cursor(Cursor.HAND_CURSOR));
		b.setPreferredSize(new Dimension(width, 38));
		return b;
	}

	// =================================================================
	// LOGIC
	// =================================================================

	/** Copies the record into the fields. */
	private void showRecord() {
		txtFirstName.setText(record.firstName);
		txtLastName.setText(record.lastName);
		txtEmail.setText(record.email);
		cmbCourse.setSelectedItem(record.course);
		cmbYearLevel.setSelectedItem(record.yearLevel);
		cmbStatus.setSelectedItem(record.status);
	}

	/** Locks / unlocks the fields and renames the buttons to match. */
	private void setEditing(boolean on) {
		editing = on;
		JTextField[] texts = {txtFirstName, txtLastName}; // university email is generated, never typed
		for (JTextField t : texts) {
			t.setEditable(on);
			t.setBackground(on ? Color.WHITE : LOCKED_BG);
		}
		txtEmail.setEditable(false);
		txtEmail.setBackground(LOCKED_BG);
		cmbCourse.setEnabled(on);
		cmbYearLevel.setEnabled(on);
		cmbStatus.setEnabled(on);
		btnModify.setText(on ? "Save Changes" : "Modify Info");
		btnClose.setText(on ? "Cancel" : "Close");
	}

	private void saveChanges() {
		String first = txtFirstName.getText().trim();
		String last = txtLastName.getText().trim();

		if (first.isEmpty() || last.isEmpty()) {
			JOptionPane.showMessageDialog(this, "First name and last name are required.",
					"Missing Information", JOptionPane.WARNING_MESSAGE);
			return;
		}
		StudentService.StudentRecord edited = record.copy();
		edited.firstName = first;
		edited.lastName = last;
		edited.email = StudentService.univEmail(last, first, record.studentId);
		edited.course = String.valueOf(cmbCourse.getSelectedItem());
		edited.yearLevel = String.valueOf(cmbYearLevel.getSelectedItem());
		edited.status = String.valueOf(cmbStatus.getSelectedItem());
		if (!StudentService.update(edited)) {
			return;
		}

		ActivityLOg.log(ActivityLOg.Type.STUDENT, edited.fullName() + "'s record was updated");
		JOptionPane.showMessageDialog(this, "Student record updated.", "Saved", JOptionPane.INFORMATION_MESSAGE);
		onChanged.run();
		dispose();
	}

	private void deleteRecord() {
		int choice = JOptionPane.showConfirmDialog(this,
				"Delete the record of " + record.fullName() + " (" + record.studentId + ")?\nThis cannot be undone.",
				"Delete Record", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (choice != JOptionPane.YES_OPTION) {
			return;
		}
		if (!StudentService.delete(record.studentId)) {
			return;
		}
		ActivityLOg.log(ActivityLOg.Type.DELETED, record.fullName() + "'s record was deleted");
		onChanged.run();
		dispose();
	}
}
