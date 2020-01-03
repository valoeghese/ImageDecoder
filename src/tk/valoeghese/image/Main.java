package tk.valoeghese.image;

import java.io.File;

import tk.valoeghese.common.ArgsData;
import tk.valoeghese.common.ArgsParser;
import tk.valoeghese.common.IProgramArgs;
import tk.valoeghese.image.util.ModifiableImage;
import tk.valoeghese.image.util.ImageUtil;

public final class Main {
	public static Args programArgs;

	public static void main(String[] args) {
		programArgs = ArgsParser.of(args, new Args());

		if (programArgs.doEncode) {
			ModifiableImage maskImage = ImageUtil.loadImage(programArgs.maskFile);
			ModifiableImage encodingImage = ImageUtil.loadImage(programArgs.inFile);

			maskImage.mergePixels((rgb, otherRGB) -> {
				int r = encodeChannel((rgb >> 16) & 255, (otherRGB >> 16) & 255);
				int g = encodeChannel((rgb >> 8) & 255, (otherRGB >> 8) & 255);
				int b = encodeChannel(rgb & 255, otherRGB & 255);

				int result = 255;
				result = (result << 8) + r;
				result = (result << 8) + g;
				result = (result << 8) + b;

				return result;
			}, encodingImage);

			ImageUtil.writeImage(programArgs.outFile, maskImage);
		} else {
			ModifiableImage image = ImageUtil.loadImage(programArgs.inFile);

			image.modifyPixels(rgb -> {
				int r = decodeChannel((rgb >> 16) & 255);
				int g = decodeChannel((rgb >> 8) & 255);
				int b = decodeChannel(rgb & 255);

				int result = 255;
				result = (result << 8) + r;
				result = (result << 8) + g;
				result = (result << 8) + b;

				return result;
			});

			ImageUtil.writeImage(programArgs.outFile, image);
		}
	}

	private static int decodeChannel(int in) {
		return (in & 0b11) << 6;
	}
	
	private static int encodeChannel(int in, int other) {
		return (in & 0b11111100) | (other >> 6);
	}

	public static final class Args implements IProgramArgs {
		private File inFile, outFile, maskFile;
		private boolean doEncode;

		private Args() {
		}

		@Override
		public void setArgs(ArgsData data) {
			this.inFile = new File(data.getString("in", "./image.png"));
			this.outFile = new File(data.getString("out", "./output.png"));
			this.maskFile = new File(data.getString("mask", "./mask.png"));
			this.doEncode = data.getBoolean("encode");
		}
	}

}
