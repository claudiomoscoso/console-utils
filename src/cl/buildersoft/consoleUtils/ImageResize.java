package cl.buildersoft.consoleUtils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

public class ImageResize {
	public static void main(String[] args) {
		ImageResize ir = new ImageResize();
		ir.execute(args);
	}

	public void execute(String[] args) {
		if (args.length != 2) {
			System.out.println("Params:");
			int i = 0;
			for (String arg : args) {
				System.out.println("\t" + (i++) + " " + arg);
			}

		} else {
			String path = args[0];
			String destiny = args[1];

			path = fixPath(path);
			destiny = fixPath(destiny);

			if (path.equalsIgnoreCase(destiny)) {
				System.out.println(String.format("Paths '%s' and '%s' must be diferentes", path, destiny));
			} else {

				if (pathExists(path) && pathExists(destiny)) {
					File pathO = new File(path);
					if (!pathO.exists()) {
						System.out.println(String.format("Path '%s' not exists", path));
					} else {
						String[] files = pathO.list();
						for (String file : files) {
							// System.out.println("File:'" + file + "'");
							if (isFile(path, file)) {
								try {
									resizeOneFile(path, file, destiny);
								} catch (Exception e) {
									System.err.println(String.format("Error procesing '%s'", path + file));
									e.printStackTrace();
								}
							} else {
								System.out.println(String.format("Ignored '%s'", path + file));
							}
						}
						System.out.println("Process Finish");
					}
				}
			}
		}
	}

	private boolean isFile(String path, String fileName) {
		File file = new File(path + fileName);
		return file.isFile();
	}

	private boolean pathExists(String path) {
		File pathO = new File(path);
		boolean exists = pathO.exists();
		if (!exists) {
			System.out.println(String.format("Path '%s' not exists", path));
		}
		return exists;
	}

	private void resizeOneFile(String path, String fileName, String destiny) throws IOException {
		String fn = path + fileName;
		File file = new File(fn);
		if (!file.exists()) {
			System.out.println(String.format("No existe '%s'", fn));
		} else {
			BufferedImage image = ImageIO.read(file);
			String format = getFormat(file);
			if (!isKnowFormat(format)) {
				copyFile(path, fileName, destiny);
				System.out.println(String.format("Formato no considerado '%s'", fn));
			} else if (!bigImage(image)) {
				copyFile(path, fileName, destiny);
				System.out.println(String.format("Is not big '%s'", fn));
			} else {
				int w = getWith(image);
				int h = getHeigh(image);

				int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();

				BufferedImage resizeImageJpg = resizeImage(w, h, image, type);

				ImageIO.write(resizeImageJpg, format, new File(destiny + fileName));

				System.out.println("Done: " + fn);
			}

		}

	}

	private void copyFile(String path, String fileName, String destiny) throws IOException {
		File f1 = new File(path + fileName);

		File f2 = new File(destiny + fileName);
		copyFileUsingStream(f1, f2);
	}

	private void copyFileUsingStream(File source, File dest) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			is.close();
			os.close();
		}
	}

	private static boolean isKnowFormat(String format) {
		// System.out.println(String.format("'%s'", format));
		return "jpg".equalsIgnoreCase(format) || "png".equalsIgnoreCase(format);
	}

	private static String getFormat(File file) {
		return file.getName().substring(file.getName().lastIndexOf('.') + 1);

	}

	private static int getHeigh(BufferedImage image) {
		int out;
		if (isWide(image)) {
			out = 768;
		} else {
			int parcial = image.getHeight() * 1024;
			out = parcial / image.getWidth();
		}
		return out;
	}

	private static int getWith(BufferedImage image) {
		int out;
		if (isWide(image)) {
			int parcial = image.getWidth() * 768;
			out = parcial / image.getHeight();
		} else {
			out = 1024;
		}
		return out;
	}

	private static boolean isWide(BufferedImage image) {
		return image.getWidth() > image.getHeight();
	}

	private static boolean bigImage(BufferedImage image) {
		return image.getWidth() > 1024 || image.getHeight() > 768;
	}

	private static BufferedImage resizeImage(int w, int h, BufferedImage image, int type) {
		BufferedImage resizedImage = new BufferedImage(w, h, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(image, 0, 0, w, h, null);
		g.dispose();

		return resizedImage;
	}

	public String fixPath(String path) {
		String fileSeparator = File.separator;
		if (path.lastIndexOf(fileSeparator) < path.length()) {
			path += fileSeparator;
		}
		return path;
	}

}
