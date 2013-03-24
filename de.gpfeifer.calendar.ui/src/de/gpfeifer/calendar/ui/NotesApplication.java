package de.gpfeifer.calendar.ui;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import de.gpfeifer.calendar.core.CalendarUtil;
import de.gpfeifer.calendar.core.Log;
import de.gpfeifer.calendar.notes.NotesCalendar;
import de.gpfeifer.calendar.notes.NotesCalendar.StreamListener;
import de.gpfeifer.calendar.ui.preferences.PreferenceConstants;
import de.gpfeifer.calendar.ui.preferences.SecurePreferenceStore;

public class NotesApplication implements IApplication {
	
	static boolean DEV_ENV = Boolean.getBoolean("gpf.dev.env");
//	static boolean DEV_ENV = false;
	
	public static void start(Date begin, Date end) {
		SecurePreferenceStore store = Activator.getDefault().getSecurePreferenceStore();
		final String path = store.getString(PreferenceConstants.P_NOTES_PATH);
		final String server = store.getString(PreferenceConstants.P_NOTES_SERVER);
		final String mail = store.getString(PreferenceConstants.P_NOTES_MAIL);
		final String password = store.getString(PreferenceConstants.P_NOTES_PWD);
		String home = System.getProperty("user.home");
		String cpath = home + "/de.gpfeifer.calendar";
		new File(cpath).mkdirs();
		final String fileName = cpath + "/notes.ics";
		NotesApplication.start(path, server, mail, password, fileName, begin, end);
		
	}
	public static void start() {
		Date begin = new Date();
		Date end = CalendarUtil.createDateWeekOffset(begin, 50);
		start(begin,end);
	}

	/*
	 * java -jar plugins\org.eclipse.equinox.launcher_1.0.0.v20070523.jar
	 * -consoleLog -console -nosplash -application Console.ConsoleStatistics
	 * file:///c:/temp/test.aaxl
	 */
	public static void start(String notesDir, String server, String mail, String password, String fileName, Date start, Date stop) {
		List<String> args = new ArrayList<String>();
		
		// args.add("de.gpfeifer.calendar.notes.NotesCalendar");
		args.add(MAGIC);
		args.add(server);
		args.add(mail);
		args.add(password);
		args.add(fileName);
		args.add("" + start.getTime());
		args.add("" + stop.getTime());
		
		if (DEV_ENV) {
			saveNotesCalendar((String[]) args.toArray(new String[args.size()]));
		} else {
			startProcess(notesDir, args);
		}

	}
	private static void startProcess(String notesDir, List<String> appArgs) {
		String launcher = getEquinoxLauncherName();
		System.out.println("Start Notes process with " + launcher);

		if (launcher == null) {
			System.out.println("No launcher found");
			return;
		} 
		List<String> args = new ArrayList<String>();
		args.add("java");

		args.add("-jar");
		args.add("plugins\\" + launcher);
		args.add("-consoleLog");
		args.add("-application");
		args.add("de.gpfeifer.calendar.ui.NotesApplicationID");
		
		args.addAll(appArgs);

		ProcessBuilder pb = new ProcessBuilder();
		String path = pb.environment().get("Path");
		path = notesDir + ";" + path;
		pb.environment().put("Path", path);
		pb.command(args);
//		System.out.println("-----------");
//		System.out.println(args);
//		System.out.println("-----------");
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

	private static String getEquinoxLauncherName() {
		File pluginsDir = new File("plugins");
		String[] list = pluginsDir.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("org.eclipse.equinox.launcher_");
			}
		});
		return list.length == 1 ? list[0] : null;
		
	}
	private static final String MAGIC = "notesprocess";

	@Override
	public Object start(IApplicationContext context) throws Exception {
		Log.log("Connect to Notes");
		String[] args = (String[]) context.getArguments().get("application.args");
		
		saveNotesCalendar(args);
		return IApplication.EXIT_OK;
	}
	public static void saveNotesCalendar(String[] args) {
		if (args.length > 0) {
			if (args[0].equals(MAGIC) && args.length == 7) {
				NotesCalendar notes = new NotesCalendar();
				notes.setServer(args[1]);
				notes.setMailFile(args[2]);
				notes.setPassword(args[3]);
				long start = Long.parseLong(args[5]);
				long stop = Long.parseLong(args[6]);
				try {
					notes.saveCalendar(new Date(start), new Date(stop), args[4]);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}
		}
		System.out.println("Inavlid args: " + args.length);
		for (String str : args) {
			System.out.println(str);
		}
		NotesCalendar notes = new NotesCalendar();
		try {
			notes.getClass().getClassLoader().loadClass("lotus.domino.NotesThread");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

}
