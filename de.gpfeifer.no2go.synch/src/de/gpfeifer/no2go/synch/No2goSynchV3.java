package de.gpfeifer.no2go.synch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.api.services.calendar.model.Event;

import de.gpfeifer.no2go.core.No2goCalendar;
import de.gpfeifer.no2go.core.No2goCalendarEvent;
import de.gpfeifer.no2go.core.No2goUtil;
import de.gpfeifer.no2go.google3.GoogleCalendarV3;
import de.gpfeifer.no2go.google3.GoogleConverter;
import de.gpfeifer.no2go.notes.NotesProcess;
import de.gpfeifer.no2go.securestore.SecurePreferenceStore;
import de.gpfeifer.no2go.securestore.SecurePreferenceStoreConstants;



class No2goSynchV3 implements No2goSynch{
	
	List<No2goSynchListener> listenerList = Collections.synchronizedList(new ArrayList<No2goSynchListener>());

	public void synch() throws Exception {
		
		
		
		File no2godir = new File(System.getProperty("user.home"), ".no2go");
		no2godir.mkdirs();

		SecurePreferenceStore store = SecurePreferenceStore.get();
		int numberOfDays = store.getInt(SecurePreferenceStoreConstants.P_GENERAL_NUMBER_DAYS);
		
		
		fireInfo("Reading Notes Calendar");
		No2goCalendar notesCalendar = getLotusNotesNo2goCalendar(store, no2godir, numberOfDays);
		int numberOfInserts = 0;
		int numberOfRepeating = 0;
		int numberOfUpdates = 0;
		int numberOfDelete = 0;
		
		if (!notesCalendar.getCalendarEvents().isEmpty()) {
			fireInfo("Reading Google Calendar");
			GoogleCalendarV3 googleCalendar = new GoogleCalendarV3();
			List<Event> googleEvents = googleCalendar.getEventsWithNotesId(numberOfDays);
			List<No2goCalendarEvent> notesEvents = notesCalendar.getCalendarEvents();
			for (No2goCalendarEvent notesEvent : notesEvents) {
				if (notesEvent.isRepeating()) {
					fireInfo("Repeating: " + notesEvent.getTitle());
					deleteAndRemove(googleCalendar, googleEvents, notesEvent.getNotesId());
					List<Event> googleEventsForRepeating = GoogleConverter.convertRepeatingEvent(notesEvent);
					int n  = 1;
					int size = googleEventsForRepeating.size();
					for (Event event : googleEventsForRepeating) {
						fireInfo("Repeating: (" + n + "/" + size + ")" +  notesEvent.getTitle());
						n++;
						googleCalendar.insert(event);
					}
					numberOfRepeating++;
				} else {
					Event googleEvent = getAndRemoveGoogleEvent(googleEvents,notesEvent.getNotesId());
					if (googleEvent != null) {
						numberOfUpdates += update(googleCalendar, googleEvent, notesEvent);
					} else {
						fireInfo("Insert " + notesEvent.getTitle());
						googleCalendar.insert(notesEvent);
						numberOfInserts++;
					}
				}
			}
			// Delete the remaining

			for (Event  event : googleEvents) {
				googleCalendar.delete(event);
				numberOfDelete++;
			}
			
		}
		fireInfo("Last Synchronize: " + printNow() + " - Insert: " + numberOfInserts + " Update: " + numberOfUpdates + " Rec: " + numberOfRepeating + " Del: " + numberOfDelete);
	}
	


	private void deleteAndRemove(GoogleCalendarV3 googleCalendar, List<Event> googleEvents, String notesId) throws IOException {
		List<Event> deleteList = new ArrayList<Event>();
		for (Event event : googleEvents) {
			String nId = GoogleConverter.getNodesId(event);
			if (nId != null && nId.equals(notesId)) {
				deleteList.add(event);
			}
		}
		for (Event delEvent : deleteList) {
			googleCalendar.delete(delEvent);
		}
		googleEvents.removeAll(deleteList);
		
	}



	private int update(GoogleCalendarV3 googleCalendar, Event googleEvent, No2goCalendarEvent notesEvent) throws Exception {
		int number = 0; 
		if (update(googleEvent,notesEvent)) {
			fireInfo("Update " + notesEvent.getTitle());
			googleCalendar.update(googleEvent);
			number++;
		}
		return number;
		
	}

	private boolean update(Event googleEvent, No2goCalendarEvent notesEvent) {
		boolean update = false;
		if (GoogleConverter.updateTitle(googleEvent, notesEvent)) {
			update = true;
		}
		if (GoogleConverter.updateDescription(googleEvent, notesEvent)) {
			update = true;
		}
		if (GoogleConverter.updateNoRepeatingWhen(googleEvent, notesEvent)) {
			update = true;
			
		}
		
		return update;
	}






	private Event getAndRemoveGoogleEvent(List<Event> googleEvents, String notesId) {
		Event result = null;
		for (Event event : googleEvents) {
			String id = GoogleConverter.getNodesId(event);
			if (id != null && id.equals(notesId)) {
				result = event;
				break;
			}
		}
		if (result != null) {
			googleEvents.remove(result);
		}
		return result;
	}

	String printNow() {
		return No2goUtil.printTime(new Date());
	}



	private void fireInfo(String info) {
		for (No2goSynchListener listener : listenerList) {
			listener.info(info);
		}
	}


	public CalendarDiff  diff(No2goCalendar notesCalendar, No2goCalendar googleCalendar) {
		CalendarDiff diff = new CalendarDiff();
		return diff;
		
	}


	private No2goCalendar getLotusNotesNo2goCalendar(SecurePreferenceStore store, File no2godir, int numberOfDays) throws Exception {
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
	
	@Override
	public void addListener(No2goSynchListener listener) {
		listenerList.add(listener);
	}

	@Override
	public void removeListener(No2goSynchListener listener) {
		listenerList.remove(listener);
		
	}



	@Override
	public int delete() throws Exception {

		SecurePreferenceStore store = SecurePreferenceStore.get();
		int numberOfDays = store.getInt(SecurePreferenceStoreConstants.P_GENERAL_NUMBER_DAYS);

		GoogleCalendarV3 googleCalendar = new GoogleCalendarV3();
		List<Event> googleEvents = googleCalendar.getEventsWithNotesId(numberOfDays);
		int total = googleEvents.size();
		int number = googleEvents.size();
		int i = 1;
		for (Event event : googleEvents) {
			fireInfo("Delete " + i + "/" + total + " "+ event.getSummary());
			i++;
			googleCalendar.delete(event);
		}

		fireInfo("Number deleted: " + number);

		return number;
	}



}
