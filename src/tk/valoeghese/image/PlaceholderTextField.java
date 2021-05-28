package tk.valoeghese.image;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JTextField;

public class PlaceholderTextField extends JTextField {
	private static final long serialVersionUID = 0L;

	public PlaceholderTextField(String placeholder) {
		this.placeholder = placeholder;
	}

	private final String placeholder;

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		if (!this.hasPassword()) {
			g.setColor(Color.gray);
			g.drawString(this.placeholder, 7, 2 * this.getHeight() / 3);
		}
	}

	private boolean hasPassword() {
		return !this.getText().isEmpty();
	}
}
