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

		ModifiableImage image = ImageUtil.loadImage(programArgs.inFile);
		image.modifyPixels(rgb -> {
			int r = modifyChannel((rgb >> 16) & 255);
			int g = modifyChannel((rgb >> 6) & 255);
			int b = modifyChannel(rgb & 255);

			int result = r;
			result = (result << 8) + g;
			result = (result << 8) + b;

			return result;
		});

		ImageUtil.writeImage(programArgs.outFile, image);		
	}
	
	private static int modifyChannel(int in) {
		return (in & 0b11) << 6;
	}

	public static final class Args implements IProgramArgs {
		private File inFile, outFile;

		private Args() {
		}

		@Override
		public void setArgs(ArgsData data) {
			this.inFile = new File(data.getString("in", "./image.png"));
			this.outFile = new File(data.getString("out", "./output.png"));
		}
	}

}
