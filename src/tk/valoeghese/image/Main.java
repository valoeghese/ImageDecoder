package tk.valoeghese.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;

import com.formdev.flatlaf.FlatLightLaf;

import tk.valoeghese.common.ArgsData;
import tk.valoeghese.common.ArgsParser;
import tk.valoeghese.common.IProgramArgs;
import tk.valoeghese.image.util.ImageUtil;
import tk.valoeghese.image.util.ModifiableImage;

public final class Main {
	// args
	public static Args programArgs;
	// for gui
	private static BufferedImage image;
	private static String imageLocMask;
	private static String imageLoc;

	public static void main(String[] args) {
		if (args.length == 0) {			
			FlatLightLaf.install();

			JFrame frame = new JFrame();
			JPanel panel = new JPanel();
			panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
			frame.add(panel);

			@SuppressWarnings("serial") JPanel preview = new JPanel() {
				@Override
				public void paint(Graphics g) {
					g.setColor(Color.WHITE);
					g.fillRect(0, 0, this.getWidth(), this.getHeight());

					if (image != null) {
						g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), 0, 0, image.getWidth(), image.getHeight(), panel);
					}
				}
			};
			preview.setPreferredSize(new Dimension(500, 500));
			panel.add(preview);

			JButton imageSelector = new JButton("Select an Image");
			panel.add(imageSelector);
			imageSelector.addActionListener(l -> {
				JTextField url = new PlaceholderTextField("Navigate to Directory...");

				@SuppressWarnings("serial") JFileChooser fileSelector = new JFileChooser() {
					@Override
					protected JDialog createDialog(Component parent) {
						// Create regular dialog
						JDialog result = super.createDialog(parent);

						// Add url to dialog
						result.getContentPane().add(url, BorderLayout.NORTH);
						result.pack();
						result.setLocationRelativeTo(parent);
						return result;
					}
				};

				url.addActionListener(k -> {
					// Setting the directory by pasting is a very important feature
					File file = new File(url.getText());
					// I could test for absolute directory as wellbut that's not so important so I'll leave it
					// Set text in dialog to nothing since it's been entered now
					url.setText("");

					// Make sure to check these and provide useful error messages
					if (file.exists()) {
						if (file.isDirectory()) {
							fileSelector.setCurrentDirectory(file);
						} else {
							JOptionPane.showMessageDialog(fileSelector, "File is not a directory.", "Problem loading File", JOptionPane.ERROR_MESSAGE);
						}
					} else {
						JOptionPane.showMessageDialog(fileSelector, "File does not exist.", "Problem loading File", JOptionPane.ERROR_MESSAGE);
					}
				});

				// Set pictures as default directory
				File pictures = new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Pictures");

				if (pictures.exists()) {
					fileSelector.setCurrentDirectory(pictures);
				}

				// Only select png images
				fileSelector.setFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						// This text appears in the option dialogue at the bottom
						return "PNG images";
					}

					@Override
					public boolean accept(File f) {
						try {
							// Look at the mime because it's more reliable than file extensions
							String mime = Files.probeContentType(f.toPath());
							return f.isDirectory() || (mime != null && mime.equals("image/png"));
						} catch (IOException e) {
							return false;
						}
					}
				});

				// Show. All customisation of the dialog must be done before this line is called
				int returnv = fileSelector.showOpenDialog(frame);

				if (returnv == JFileChooser.APPROVE_OPTION) {
					File file = fileSelector.getSelectedFile();

					if (file != null && file.getName().endsWith(".png")) {
						try {
							image = ImageIO.read(file);
							
							if (imageLocMask.equals("SETME")) {
								imageLocMask = file.getAbsolutePath();
							} else {
								imageLoc = file.getAbsolutePath();
							}
						} catch (IOException e) {
							e.printStackTrace();
							throw new UncheckedIOException(e); 
						}

						preview.repaint();
					}
				}
			});

			JButton decode = new JButton("Decode");
			panel.add(decode);
			decode.addActionListener(l -> {
				Main.main(new String[] {"-in", imageLoc});
				JOptionPane.showMessageDialog(frame, "Completed Decode.");
			});

			JButton encode = new JButton("Encode");
			panel.add(encode);
			decode.addActionListener(l -> {
				Main.main(new String[] {"-in", imageLoc});
				JOptionPane.showMessageDialog(frame, "Completed Decode.");
			});

			frame.setSize(600, 600);
			frame.setResizable(false);
			frame.setTitle("ImageDecoder");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		} else {
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
