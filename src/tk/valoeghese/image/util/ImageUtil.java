package tk.valoeghese.image.util;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public final class ImageUtil {
	private ImageUtil() {
	}

	public static ModifiableImage loadImage(File file) {
		try {
			return ModifiableImage.of(ImageIO.read(file));
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	public static void writeImage(File file, ModifiableImage image) {
		ImageDataType dataType = ImageDataType.of(file);

		if (dataType == null) {
			throw new RuntimeException("Invalid or Unsupported image type in ImageUtil#writeImage!\n"
					+ "Supported Types:\n"
					+ "- png"
					+ "- jpg");
		}

		try {
			ImageIO.write(image.data, dataType.imgIOString, file);
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	public static class RuntimeIOException extends RuntimeException {
		private static final long serialVersionUID = -4575562495461021317L;

		public RuntimeIOException(IOException e) {
			super(e);
		}
	}
}
