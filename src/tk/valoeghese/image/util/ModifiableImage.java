package tk.valoeghese.image.util;

import java.awt.image.BufferedImage;

public final class ModifiableImage {
	private ModifiableImage(BufferedImage image) {
		this.data = image;
		this.width = image.getWidth();
		this.height = image.getHeight();
	}

	final BufferedImage data;
	private final int width, height;

	static ModifiableImage of(BufferedImage image) {
		return new ModifiableImage(image);
	}

	public void modifyPixels(PixelModifierFunction function) {
		for (int x = 0; x < this.width; ++x) {
			for (int y = 0; y < this.height; ++y) {
				int rgb = function.modifyRGB(this.data.getRGB(x, y));
				this.data.setRGB(x, y, rgb);
			}
		}
	}
}
