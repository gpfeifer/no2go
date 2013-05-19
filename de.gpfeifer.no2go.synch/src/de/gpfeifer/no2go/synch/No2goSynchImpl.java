package de.gpfeifer.no2go.synch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.gdata.util.ServiceException;

import de.gpfeifer.no2go.core.No2goCalendar;
import de.gpfeifer.no2go.core.No2goCalendarEvent;
import de.gpfeifer.no2go.core.No2goUtil;
import de.gpfeifer.no2go.google.GoogleCalendar;
import de.gpfeifer.no2go.notes.NotesProcess;
import de.gpfeifer.no2go.securestore.SecurePreferenceStore;
import de.gpfeifer.no2go.securestore.SecurePreferenceStoreConstants;



public class No2goSynchImpl implements No2goSynch{
	
	GoogleCalendar googleCalendar;
	List<No2goSynchListener> listenerList = Collections.synchronizedList(new ArrayList<No2goSynchListener>());

	public void synch() throws Exception {
		File no2godir = new File(System.getProperty("user.home"), ".no2go");
		no2godir.mkdirs();

		SecurePreferenceStore store = SecurePreferenceStore.get();
		int numberOfDays = store.getInt(SecurePreferenceStoreConstants.P_GENERAL_NUMBER_DAYS);

		fireInfo("Reading Notes Calendar");
		No2goCalendar notesCalendar = getNoteNo2goCalendar(store, no2godir, numberOfDays);
		int numberOfInserts = 0;
		
		if (!notesCalendar.getCalendarEvents().isEmpty()) {
			fireInfo("Reading Google Calendar");
			No2goCalendar googleCalendar = getGoogleNo2goCalendar(no2godir, numberOfDays);
		
			// Store for debugging
			googleCalendar.printOn(new FileOutputStream(new File(no2godir,"google.xml")));
		
			CalendarDiff diff = diff(notesCalendar, googleCalendar);
			numberOfInserts = diff.insertList.size();
		
			// Store  for debugging
			new No2goCalendar(diff.insertList).printOn(new File(no2godir,"google.xml"));

			fireInfo("Update Google Calendar");
			upateGoogle(diff);
			fireGoogleInsert(diff.insertList);
		}
		fireInfo("Last Synchronize: " + printNow() + " - Inserts: " + numberOfInserts);
	}
	
	String printNow() {
		return No2goUtil.printTime(new Date());
	}


	private void fireGoogleInsert(List<No2goCalendarEvent> insertList) {
		for (No2goSynchListener listener : listenerList) {
			listener.googleInsert(insertList);
		}
		
	}


	private void fireInfo(String info) {
		for (No2goSynchListener listener : listenerList) {
			listener.info(info);
		}
	}

	private void upateGoogle(CalendarDiff diff) throws MalformedURLException, IOException, ServiceException {
		GoogleCalendar calendar = getGoogleCalendar();
		
		for ( No2goCalendarEvent event : diff.insertList) {
			calendar.insert(event);
		}
		
	}

	public CalendarDiff  diff(No2goCalendar notesCalendar, No2goCalendar googleCalendar) {
		CalendarDiff diff = new CalendarDiff();
		List<No2goCalendarEvent> notesEvents = notesCalendar.getNormalizedCalendarEvents();
		for (No2goCalendarEvent event : notesEvents) {
			if (!googleCalendar.contains(event)) {
				diff.insertList.add(event);
			}
		}
		return diff;
		
	}

	private No2goCalendar getGoogleNo2goCalendar(File no2godir, int numberOfDays) throws IOException, ServiceException {
		GoogleCalendar calendar = getGoogleCalendar();
		List<No2goCalendarEvent> events = calendar.getCalendarEntries(new Date(), numberOfDays);
		return new No2goCalendar(events);
	}

	private No2goCalendar getNoteNo2goCalendar(SecurePreferenceStore store, File no2godir, int numberOfDays) throws Exception {
		String fileName = new File(no2godir, "notes.xml").getAbsolutePath();
		saveNotesCalendar(fileName, store, numberOfDays);
		if (new File(fileName).exists()) {
			return No2goCalendar.read(fileName);
		} else {
			return new No2goCalendar();
		}
	}

	private void saveNotesCalendar(String out, SecurePreferenceStore store, int numberOfDays) throws Exception {
		String path = store.getString(SecurePreferenceStoreConstants.P_NOTES_PATH);
		String server = store.getString(SecurePreferenceStoreConstants.P_NOTES_SERVER);
		String mail = store.getString(SecurePreferenceStoreConstants.P_NOTES_MAIL);
		String pwd = store.getString(SecurePreferenceStoreConstants.P_NOTES_PWD);
		String str = NotesProcess.save(path, server, mail, pwd, "" + numberOfDays, out);
		if (str != null && !str.isEmpty()) {
			throw new Exception(str);
		}
	}
	
	GoogleCalendar getGoogleCalendar() {
		if (googleCalendar == null) {
			SecurePreferenceStore store = SecurePreferenceStore.get();
			String account = store.getString(SecurePreferenceStoreConstants.P_GOOGLE_ACCOUNT);
			String pwd = store.getString(SecurePreferenceStoreConstants.P_GOOGLE_PWD);
			boolean useHTTPS = store.getBoolean(SecurePreferenceStoreConstants.P_GOOGLE_SSL);
			googleCalendar = new GoogleCalendar();
			googleCalendar.setGoogleAccountName(account);
			googleCalendar.setGooglePassword(pwd);
			googleCalendar.useHTTPS(useHTTPS);
		}
		return googleCalendar;
	}

	@Override
	public void addListener(No2goSynchListener listener) {
		listenerList.add(listener);
	}

	@Override
	public void removeListener(No2goSynchListener listener) {
		listenerList.remove(listener);
		
	}

}
