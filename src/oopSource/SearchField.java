package oopSource;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Text box with a grey hint ("Search student ID or name...") that disappears
 * when the user types. getText() is always the REAL text (never the hint),
 * and onChange runs on every keystroke - so searching is live.
 */
public class SearchField extends JTextField {

	private static final long serialVersionUID = 1L;
	private final String hint;

	public SearchField(String hint, int width, final Runnable onChange) {
		this.hint = hint;
		setFont(new Font("Arial", Font.PLAIN, 13));
		setPreferredSize(new Dimension(width, 38));
		getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { onChange.run(); }
			public void removeUpdate(DocumentEvent e) { onChange.run(); }
			public void changedUpdate(DocumentEvent e) { onChange.run(); }
		});
	}

	public String getQuery() {
		return getText().trim();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (getText().isEmpty()) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setColor(Color.GRAY);
			g2.setFont(getFont());
			int y = (getHeight() + g2.getFontMetrics().getAscent() - g2.getFontMetrics().getDescent()) / 2;
			g2.drawString(hint, getInsets().left + 4, y);
			g2.dispose();
		}
	}

	/**
	 * Does the search text match? Every word typed must appear in at least one of the fields,
	 * in any order, ignoring case and commas - so "maria santos" finds "Santos, Maria A.".
	 */
	public static boolean matches(String query, String... fields) {
		if (query == null || query.trim().isEmpty()) {
			return true;
		}
		StringBuilder all = new StringBuilder();
		for (String f : fields) {
			all.append(f).append(' ');
		}
		String haystack = all.toString().toLowerCase().replace(",", " ");
		for (String word : query.toLowerCase().replace(",", " ").trim().split("\\s+")) {
			if (!haystack.contains(word)) {
				return false;
			}
		}
		return true;
	}
}
