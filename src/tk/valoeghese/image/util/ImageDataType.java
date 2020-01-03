package tk.valoeghese.image.util;

import java.io.File;

public enum ImageDataType {
	PNG("png"),
	JPG("jpg");

	private ImageDataType(String name) {
		this.imgIOString = name;
	}

	final String imgIOString;

	public static ImageDataType of(File file) {
		if (file.isDirectory()) {
			return null;
		} else {
			String filePath = file.getName().toLowerCase();

			if (filePath.endsWith(".png")) {
				return PNG;
			} else if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
				return JPG;
			} else {
				return null;
			}
		}
	}
}
