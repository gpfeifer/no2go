package de.gpfeifer.calendar.notes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.gpfeifer.calendar.notes.NotesCalendar.StreamListener;

public class NotesProcess {
	public static class StreamListener extends Thread {

		protected InputStream inputStream;

		protected PrintStream out;

		public StreamListener(InputStream inputStream, PrintStream out) {
			super();
			this.inputStream = inputStream;
			this.out = out;
		}

		public void run() {

			InputStreamReader isr = new InputStreamReader(inputStream);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			try {
				while ((line = br.readLine()) != null) {
					out.println(line);
				}
			} catch (IOException e) {
				out.println(e.getMessage());
			}
		}

	}

	public static boolean verifyNotesPath(String path) throws FileNotFoundException {
		if (!new File(path).exists()) {
			throw new FileNotFoundException(path);
		}

		String notesJar = getNotesJar(path);
		if (!new File(notesJar).exists()) {
			throw new FileNotFoundException(notesJar);
		}
		return true;
	}

	private static String getNotesJar(String path) {
		File file = new File(path, "jvm/lib/ext/notes.jar");
		return file.getAbsolutePath();
	}

	public static void main(String[] args) {
		if (args.length < 4) {
			System.err.println("Invalid number of arguments");
			return;
		}
		try {
			new NotesProcess().run(args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void startProcess(String notesDir) {

		String cp = getClasspath();
		cp += System.getProperty("path.separator") + getNotesJar(notesDir);
		ProcessBuilder pb = new ProcessBuilder();
		String path = pb.environment().get("Path");
		path = notesDir + ";" + path;
		pb.environment().put("Path", path);
		String java = System.getProperty("java.home") + "/bin/java";
		pb.command(java, "-cp", cp, "de.gpfeifer.calendar.notes.NotesProcess");
		// System.out.println("-----------");
		// System.out.println(args);
		// System.out.println("-----------");
		try {
			Process process = pb.start();
			new StreamListener(process.getErrorStream(), System.err).start();
			new StreamListener(process.getInputStream(), System.out).start();
			process.waitFor();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	static private String getClasspath() {
		String osgi = System.getProperty("java.class.path");
		// We assume we are running in osgi
		File parent = new File(osgi).getParentFile();
		String[] rtBundles = findBundles(parent, "de.gpfeifer", "org.mnode","commons");
		if (rtBundles.length != 0) {
			return getClassPathString(rtBundles);
		}
		
		rtBundles = findDevBundles();
		return getClassPathString(rtBundles);
	}
	
	private static String[] findDevBundles() {
		// TODO Auto-generated method stub
		URL url = NotesProcess.class.getProtectionDomain().getCodeSource().getLocation();
		String root;
		try {
			root = new File (url.toURI()).getParent();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
		String[] result = new String[9];
		result[0] = new File(root,"de.gpfeifer.calendar.core/bin").getAbsolutePath();
		result[1] = new File(root,"net.cal4j/lib/commons-codec-1.3.jar").getAbsolutePath();
		result[2] = new File(root,"net.cal4j/lib/commons-lang-2.4.jar").getAbsolutePath();
		result[3] = new File(root,"net.cal4j/lib/commons-logging-1.1.1.jar").getAbsolutePath();
		
		result[4] = new File(root,"de.gpfeifer.calendar.core/lib/gdata-calendar-2.0.jar").getAbsolutePath();
		result[5] = new File(root,"de.gpfeifer.calendar.core/lib/gdata-client-1.0.jar").getAbsolutePath();
		result[6] = new File(root,"de.gpfeifer.calendar.core/lib/gdata-core-1.0.jar").getAbsolutePath();
		result[7] = new File(root,"de.gpfeifer.calendar.core/lib/guava-r06.jar").getAbsolutePath();
		
		result[8] = new File(root,"net.cal4j/bin").getAbsolutePath();
		return result;
	}

	private static String getClassPathString(String[] rtBundles) {
		String result = "";

		for (int i = 0; i < rtBundles.length; i++) {
			result += rtBundles[i];
			if (i < rtBundles.length - 1) {
				result += System.getProperty("path.separator");
			}
		}
		return result;
	}

	static String[]  findBundles(File parent, final String... prefix) {

		String[] list = parent.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				for (String p : prefix) {
					if (name.startsWith(p)) {
						return true;
					}
				}
				return false;
			}
		});

		return list;
	}

	private void run(String... args) throws Exception {
		String cmd = args[0];
		String server = args[1];
		String mailDB = args[2];
		String pwd = args[3];

		NotesCalendar notesCalendar = new NotesCalendar();
		notesCalendar.setServer(server);
		notesCalendar.setMailFile(mailDB);
		notesCalendar.setPassword(pwd);
		if (cmd.equalsIgnoreCase("save")) {
			if (args.length != 6) {
				System.err.println("SAVE: Invalid number of arguments");
				return;
			}
			int weeks = Integer.parseInt(args[4]);
			notesCalendar.saveCalendar(new Date(), weeks, args[5]);
		}
	}

	public static void save(String notesDir, String server, String mail, String pwd, String weeks, String out) {
		String cp = getClasspath();
		cp += System.getProperty("path.separator") + getNotesJar(notesDir);
		ProcessBuilder pb = new ProcessBuilder();
		String path = pb.environment().get("Path");
		path = notesDir + ";" + path;
		pb.environment().put("Path", path);
		String java = System.getProperty("java.home") + "/bin/java";
		pb.command(java, "-cp", cp, NotesProcess.class.getName(), "save", server, mail, pwd, weeks, out);
		// System.out.println("-----------");
		// System.out.println(args);
		// System.out.println("-----------");
		try {
			Process process = pb.start();
			new StreamListener(process.getErrorStream(), System.err).start();
			new StreamListener(process.getInputStream(), System.out).start();
			process.waitFor();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
}
