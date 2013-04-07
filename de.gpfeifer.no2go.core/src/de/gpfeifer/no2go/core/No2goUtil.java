package de.gpfeifer.no2go.core;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class No2goUtil {
//	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");

	static final SimpleDateFormat dateFormatWithSeconds = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

	static File no2goDir;
	
	public static File getNo2goDir() {
		if (no2goDir == null) {
			no2goDir = new File(System.getProperty("user.home"),".no2go");
			no2goDir.mkdirs();
		}
		return no2goDir;
	}
	public static java.util.Date createDateOffset(java.util.Date date, int days) {
		long v = date.getTime();
		long r = v + ((long) days  * 24l * 60l * 60l * 1000l);
		return new java.util.Date(r);
	}
	
	public static String printDate(Date date, boolean printSeconds) {
	
		return printSeconds ? dateFormatWithSeconds.format(date) : dateFormat.format(date);
	}

	public static String printTime(Date date) {
		return timeFormat.format(date);
	}




}
