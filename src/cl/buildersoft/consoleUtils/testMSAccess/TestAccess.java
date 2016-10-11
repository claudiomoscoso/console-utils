package cl.buildersoft.consoleUtils.testMSAccess;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class TestAccess {
	private static final String accessDBURLPrefix = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
	private static final String accessDBURLSuffix = ";DriverID=22;READONLY=true";

	static {
		try {
			System.out.println("Testing driver");
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			System.out.println("Driver right");
		} catch (ClassNotFoundException e) {
			System.err.println("JdbcOdbc Bridge Driver not found!");
		}
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Indique un nombre de archivo MDB como parametro.");
			System.exit(0);
		}
		try {
			TestAccess ta = new TestAccess();
			ta.getAccessDBConnection(args[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Connection getAccessDBConnection(String filename) throws Exception {
		return getAccessDBConnection(filename, "", "");
	}

	public Connection getAccessDBConnection(String filename, String user, String password) throws Exception {
		filename = filename.replace('\\', '/').trim();

		if (!(exists(filename))) {
			throw new Exception("El archivo [" + filename + "] no existe");
		}

		String databaseURL = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + filename + ";DriverID=22;READONLY=true";

		System.out.println("Datebase URL: " + databaseURL);
		return DriverManager.getConnection(databaseURL, user, password);
	}

	private static boolean exists(String filename) {
		File file = new File(filename);
		boolean out = file.exists();
		file = null;
		return out;
	}
}