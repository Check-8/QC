package it.uniroma3.marco.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

public class Logger {
	private static final String LOG_FILE = "QCLog";
	public static final Logger instance = new Logger();

	private File logFile;

	private Logger() {
		Calendar cal = Calendar.getInstance(Locale.ITALY);
		logFile = new File(LOG_FILE + " - " + cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-"
				+ cal.get(Calendar.DAY_OF_MONTH) + " " + cal.get(Calendar.HOUR) + cal.get(Calendar.MINUTE));
	}

	public void log(String daScrivere) {
		try (FileWriter fw = new FileWriter(logFile, true)) {
			fw.write(daScrivere);
			fw.write("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
