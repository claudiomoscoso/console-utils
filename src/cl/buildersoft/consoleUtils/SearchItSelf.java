package cl.buildersoft.consoleUtils;

import cl.buildersoft.consoleUtils.bean.MyFile;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchItSelf {
	// private static final String SOURCE_PATH =
	// "C:\\MisArchivos\\RESPALDO\\Descargas\\keyfinder";
	// private static final String TARGET_PATH = "C:\\MisArchivos\\RESPALDO";
	private static final Boolean DEV_MODE = Boolean.valueOf(false);

	private static Set<String> showedPair = new HashSet<String>();

	public static void main(String[] args) {
		Long start = Long.valueOf(System.currentTimeMillis());
		Boolean showDuplicated = false;
		String pattern = "*.*";
		if (DEV_MODE.booleanValue()) {
			args = new String[2];
			args[0] = "C:\\MisArchivos\\RESPALDO\\Descargas\\keyfinder";
			args[1] = "C:\\MisArchivos\\RESPALDO";
		}

		String folder = validParameters(args);
		if (folder == null) {
			return;
		}

		if (args.length >= 2) {
			showDuplicated = Boolean.parseBoolean(args[1]);
		}
		if (args.length >= 3) {
			pattern = args[2];
		}

		Map<Long, List<MyFile>> sourceMap = new HashMap<Long, List<MyFile>>();
		// Map<Long, List<MyFile>> targetMap = null;
		try {
			getInfoFiles(folder, sourceMap);

			// targetMap = new HashMap<Long, List<MyFile>>(sourceMap);
			System.out.println("----------------------------------------------");
			Set<Long> sizeSet = sourceMap.keySet();
			for (Long size : sizeSet) {
				List<MyFile> source = (List<MyFile>) sourceMap.get(size);
				listSources(source, sourceMap, showDuplicated);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sourceMap.clear();
			// targetMap.clear();
		}
		Long end = Long.valueOf(System.currentTimeMillis());
		System.out.println("Process finish in " + (end.longValue() - start.longValue()) + " mili-seconds");
	}

	public static byte[] createChecksum(String filename) throws Exception {
		InputStream fis = new FileInputStream(filename);

		byte[] buffer = new byte[1048576];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;
		do {
			numRead = fis.read(buffer);
			if (numRead > 0)
				complete.update(buffer, 0, numRead);
		} while (numRead != -1);

		fis.close();

		return complete.digest();
	}

	public static String getMD5Checksum(String filename) throws Exception {
		byte[] b = createChecksum(filename);
		String result = "";

		for (int i = 0; i < b.length; ++i) {
			result = result + Integer.toString((b[i] & 0xFF) + 256, 16).substring(1);
		}
		return result;
	}

	private static void listSources(List<MyFile> source, Map<Long, List<MyFile>> targetMap, Boolean showDuplicated)
			throws Exception {
		for (MyFile myFile : source) {
			listDuplicated(myFile, targetMap, showDuplicated);
		}
	}

	private static void listDuplicated(MyFile myFile, Map<Long, List<MyFile>> targetMap, Boolean showDuplicated) throws Exception {
		List<MyFile> target = (List<MyFile>) targetMap.get(myFile.getSize());
		if (target != null) {
			Boolean firstLoop = true;
			Boolean toBeShow = false;
			String sourceFile = null;
			String targetFile = null;
			String sourcePlusTarget = null;
			String targetPlusSource = null;

			for (MyFile myTargetFile : target) {
				if (!myFile.getPathName().equals(myTargetFile.getPathName())) {
					myTargetFile.setMd5(getMD5Checksum(myTargetFile.getPathName()));
					myFile.setMd5(getMD5Checksum(myFile.getPathName()));

					if (firstLoop.booleanValue()) {
						if (myTargetFile.getMd5().equals(myFile.getMd5())) {
							sourceFile = myFile.getPathName();
							targetFile = myTargetFile.getPathName();

							toBeShow = true;
						}

					}

					sourcePlusTarget = sourceFile + targetFile;
					targetPlusSource = targetFile + sourceFile;

					if ((toBeShow.booleanValue()) && (!showedPair.contains(sourcePlusTarget))) {
						if (firstLoop.booleanValue()) {
							if (!showDuplicated) {
								System.out.println("## " + sourceFile);
							}
							System.out.println("-- " + targetFile);
							firstLoop = false;
						}

						showedPair.add(sourcePlusTarget);
						showedPair.add(targetPlusSource);
						// showedPair.add(targetFile + sourceFile);

						toBeShow = false;
					}
				}
			}
		}
	}

	private static void getInfoFiles(String path, Map<Long, List<MyFile>> out) throws Exception {
		getInfoFiles(path, out, null, null);
	}

	private static void getInfoFiles(String path, Map<Long, List<MyFile>> out, String folderException, Set<Long> filesSize)
			throws Exception {
		if ((folderException != null) && (folderException.equalsIgnoreCase(path))) {
			System.out.println("Skiping " + path);
		} else {
			File source = new File(path);

//			FileFilter ; 
			
			File[] files = source.listFiles();

			for (File file : files) {
				if (filesSize == null) {
					verifyFile(out, file, folderException, filesSize);
				} else {
					Long fileLen = new Long(file.length());

					if ((!(filesSize.contains(fileLen))) && (!(file.isDirectory())))
						continue;
					verifyFile(out, file, folderException, filesSize);
				}
			}
		}
	}

	private static void verifyFile(Map<Long, List<MyFile>> out, File file, String exception, Set<Long> filesSize)
			throws Exception {
		if (file.isFile())
			if ((!(file.getName().startsWith("."))) && (file.length() > 0L)) {
				MyFile myFile = new MyFile(file, null);

				putInList(myFile, out);
			} else {
				System.out.println("Ignoring file '" + file.getAbsolutePath() + "'");
			}
		else if (file.isDirectory())
			getInfoFiles(file.getAbsolutePath(), out, exception, filesSize);
	}

	private static void putInList(MyFile myFile, Map<Long, List<MyFile>> out) {
		List<MyFile> list = null;
		Long size = myFile.getSize();
		if (!out.containsKey(size)) {
			list = new ArrayList<MyFile>();
			list.add(myFile);
		} else {
			list = (List<MyFile>) out.get(size);
			list.add(myFile);
		}
		out.put(size, list);
	}

	private static String validParameters(String[] args) {
		String out = null;
		String source = null;

		if (args.length < 1 || args.length > 3) {
			System.out.println("Parametros:");
			System.out.println("Path");
			System.out.println("Flag [true|false] para indicar si muestra solo los repetidos");
			System.out.println("Patrón, con el que se va a indicar los tipos de archivos a considerar, por ejemplo *.jpg");
//			System.out.		println("Indique una carpeta, opcionalmente un flag [true | false] para indicar si muestra solo los repetidos");
		} else {
			source = args[0];

			File f = new File(source);
			if (!f.isDirectory()) {
				System.out.println("Folder [" + source + "] not found...");
			} else {
				out = source;
			}
		}

		return out;
	}
}