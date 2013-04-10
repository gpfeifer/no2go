package de.gpfeifer.no2go.notes;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

import de.gpfeifer.no2go.core.No2goUtil;



/**
 * To access Lotus Notes we create a Process, because 
 * we use the Notes installation of the current machine. 
 *
 * @author gpfeifer
 *
 */
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

	public static String verifyNotesPath(String path)  {
		if (!new File(path).exists()) {
			return "Invalid Notes Directory.\nDirectory does not exists.";
		} 
		String notesJar = getNotesJar(path);
		if (!new File(notesJar).exists()) {
			return "Invalid Notes Directory.\nNotes Directory must point to the directory where notes.exe is located.\n" +
					"In addition a file named 'notes.jar' must exists in subdir jvm/lib/ext must exists";
		}
		return null;
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
			String msg = e.getMessage();
			if (msg == null) {
				System.err.println(e);
			} else {
				System.err.println(e.getMessage());
			}
		}
	}

	static private String getClasspath() {
		String osgi = System.getProperty("java.class.path");
		// We assume we are running in osgi
		File parent = new File(osgi).getParentFile();
		String[] rtBundles = findBundles(parent, "de.gpfeifer");
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
		String[] result = new String[2];
		result[0] = new File(root,"de.gpfeifer.no2go.core/bin").getAbsolutePath();
		result[1] = new File(root,"de.gpfeifer.no2go.notes/bin").getAbsolutePath();
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
		String[] result = new String[list.length];
		int i = 0;
		for (String string : list) {
//			result[i++] = "\"" + new File(parent,string).getAbsolutePath() + "\"" ; 
			result[i++] = new File(parent,string).getAbsolutePath();
		}

		return result;
	}

	public void run(String... args) throws Exception {
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
	

	public static String save(String notesDir, String server, String mail, String pwd, String days, String outFileName) {
		String errorString = verifyNotesPath(notesDir);
		if (errorString != null) {
			return errorString;
		}
		String cp = getClasspath();
		cp += System.getProperty("path.separator") + getNotesJar(notesDir);
		ProcessBuilder pb = new ProcessBuilder();
		String path = pb.environment().get("Path");
		path = notesDir + ";" + path;
		pb.environment().put("Path", path);
		String java = System.getProperty("java.home") + "/bin/java";
		pb.command(java, "-cp", cp, NotesProcess.class.getName(), "save", server, mail, pwd, days, outFileName);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	
		try {
			PrintStream stream = new PrintStream(outputStream);
			Process process = pb.start();
			new StreamListener(process.getErrorStream(), stream).start();
			new StreamListener(process.getInputStream(), stream).start();
			process.waitFor();

		} catch (IOException e) {
			return e.getMessage();
		} catch (InterruptedException e) {
			return e.getMessage();
		}

		return new String(outputStream.toByteArray());
	}

	public static String verify(String notesDir, String server, String mail, String pwd) {
		System.out.println("Verify");
		File outFile = new File(No2goUtil.getNo2goDir(),"notes.xml");
		String errorString = save(notesDir,server,mail,pwd,""+30,outFile.getAbsolutePath());
		outFile.delete();
		return errorString;
		
	}
}
