package de.gpfeifer.calendar.ui.actions;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import de.gpfeifer.calendar.core.CalendarUtil;
import de.gpfeifer.calendar.core.ICalendarEvent;
import de.gpfeifer.calendar.core.SimpleSynchronizer;
import de.gpfeifer.calendar.core.SynchCommand;
import de.gpfeifer.calendar.core.VCalendar;
import de.gpfeifer.calendar.google.GoogleCalendar;
import de.gpfeifer.calendar.ui.Activator;
import de.gpfeifer.calendar.ui.NotesApplication;
import de.gpfeifer.calendar.ui.preferences.PreferenceConstants;
import de.gpfeifer.calendar.ui.preferences.SecurePreferenceStore;

public class SyncRunnable {



	public void sync() throws Exception {
		System.out.println("Sync begin. " + new Date());
		Date start = new Date();
		int days = Activator.getDefault().getSecurePreferenceStore().getInt(PreferenceConstants.P_GENERAL_NUMBER_DAYS);
		Date stop = CalendarUtil.createDateWeekOffset(start, days);
		String home = System.getProperty("user.home");
		String cpath = home + "/.no2go";
		new File(cpath).mkdirs();
		final String googleFileName = cpath + "/google.ics";
		final String notesFileName = cpath + "/notes.ics";
		delete(googleFileName);
		delete(notesFileName);
		saveNotes(start, stop, notesFileName);
		if (!(new File(notesFileName).exists())) {
			return;
		}

		List<ICalendarEvent> googleEntries = saveGoogle(start, stop, googleFileName);
		// VCalendar googleCal = VCalendar.read(googleFileName);
		VCalendar notesCal = VCalendar.read(notesFileName);
		List<SynchCommand> commands = new SimpleSynchronizer().getSynchCommands(googleEntries, notesCal.getCalendarEvents());
		
		GoogleCalendar google = getGoogle();
		for (SynchCommand cmd : commands) {
			System.out.println(cmd);
			google.execute(cmd);
		}
		System.out.println("Sync end. " + new Date());

	}

	private void delete(String name) {
		File file = new File(name);
		if (file.exists()) {
			file.delete();
		}

	}

	private List<ICalendarEvent> saveGoogle(final Date start, final Date stop, final String fileName) throws Exception {
		SecurePreferenceStore store = Activator.getDefault().getSecurePreferenceStore();
		final String account = store.getString(PreferenceConstants.P_GOOGLE_ACCOUNT);

		try {
			String mail = URLEncoder.encode(account, "UTF-8");
			URL url = new URL("https://gregor-pfeifer.appspot.com/calendar_ping?mail=" + mail);
			// url.openConnection();
			InputStream in = url.openStream();
			boolean eof = false;
			StringBuffer buffer = new StringBuffer();
			while (!eof) {
				int i = in.read();
				if (i < 0) {
					eof = true;
				} else {
					buffer.append((char) i);
				}
			}
			// System.out.println(buffer.toString());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		GoogleCalendar google = getGoogle();
		return google.saveCalendar(start, stop, fileName);
	}
	
	GoogleCalendar getGoogle() {
		SecurePreferenceStore store = Activator.getDefault().getSecurePreferenceStore();
		final String account = store.getString(PreferenceConstants.P_GOOGLE_ACCOUNT);
		final String password = store.getString(PreferenceConstants.P_GOOGLE_PWD);
		GoogleCalendar google = new GoogleCalendar();
		google.setGoogleAccountName(account);
		google.setGooglePassword(password);
		return google;

		
	}

	private void saveNotes(final Date start, final Date stop, final String fileName) {
		SecurePreferenceStore store = Activator.getDefault().getSecurePreferenceStore();
		final String path = store.getString(PreferenceConstants.P_NOTES_PATH);
		final String server = store.getString(PreferenceConstants.P_NOTES_SERVER);
		final String mail = store.getString(PreferenceConstants.P_NOTES_MAIL);
		final String password = store.getString(PreferenceConstants.P_NOTES_PWD);
		NotesApplication.start(path, server, mail, password, fileName, start, stop);
	}


}
