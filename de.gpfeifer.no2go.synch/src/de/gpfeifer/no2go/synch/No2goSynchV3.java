package de.gpfeifer.no2go.synch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import de.gpfeifer.no2go.core.No2goCalendar;
import de.gpfeifer.no2go.core.No2goCalendarEvent;
import de.gpfeifer.no2go.core.No2goUtil;
import de.gpfeifer.no2go.google3.GoogleCalendarListener;
import de.gpfeifer.no2go.google3.GoogleCalendarV3;
import de.gpfeifer.no2go.google3.GoogleUtil;
import de.gpfeifer.no2go.notes.NotesProcess;
import de.gpfeifer.no2go.securestore.SecurePreferenceStore;
import de.gpfeifer.no2go.securestore.SecurePreferenceStoreConstants;



class No2goSynchV3 implements No2goSynch, GoogleCalendarListener {
	static private Logger logger = LoggerFactory.getLogger(No2goSynchV3.class);
	
	List<No2goSynchListener> listenerList = Collections.synchronizedList(new ArrayList<No2goSynchListener>());

	private boolean includeAttendees;

	public void synch() throws Exception {
		fireSynchBegin();
		File no2godir = new File(System.getProperty("user.home"), ".no2go");
		no2godir.mkdirs();

		SecurePreferenceStore store = SecurePreferenceStore.get();
		int numberOfDays = store.getInt(SecurePreferenceStoreConstants.P_GENERAL_NUMBER_DAYS);
		includeAttendees = store.getBoolean(SecurePreferenceStoreConstants.P_INCLUDE_ATTENDEES);
	
		fireInfo("Reading Notes Calendar");
		No2goCalendar notesCalendar = getLotusNotesNo2goCalendar(store, no2godir, numberOfDays);
		
		if (!notesCalendar.getCalendarEvents().isEmpty()) {
			fireInfo("Reading Google Calendar");
			GoogleCalendarV3 googleCalendar = new GoogleCalendarV3();
			googleCalendar.setListener(this);
			List<Event> googleEvents = googleCalendar.getEventsWithNotesId(numberOfDays);
			List<No2goCalendarEvent> notesEvents = notesCalendar.getCalendarEvents();
			synch(notesEvents, googleCalendar, googleEvents);
		} else {
			fireInfo("No Events found in Lotus Notes");
		}
		fireSynchEnd();

	}



	private void synch(List<No2goCalendarEvent> notesEvents, GoogleCalendarV3 googleCalendar, List<Event> googleEvents) throws IOException, Exception {
		for (No2goCalendarEvent notesEvent : notesEvents) {
			if (notesEvent.isRepeating()) {
				synchRepeating2(notesEvent, googleCalendar, googleEvents);
			} else {
				Event googleEvent = getAndRemoveGoogleEvent(googleEvents,notesEvent.getNotesId());
				if (googleEvent != null) {
					updateOrUnchanged(googleCalendar, googleEvent, notesEvent);
				} else {
					insert(googleCalendar, notesEvent);

				}
			}
		}
		// Delete the remaining
		for (Event  event : googleEvents) {
			delete(googleCalendar,event);
		}
		fireInfo("Last Synchronize: " + printNow());
	}



	private void synchRepeating2(No2goCalendarEvent notesEvent, GoogleCalendarV3 googleCalendar, List<Event> googleEvents) throws IOException {
		fireInfo("Repeating: " + notesEvent.getTitle());
		List<Event> existingGoogleEvents = removeRepeating(googleEvents, notesEvent.getNotesId());
		List<Event> notesEvents = GoogleUtil.convertRepeatingEvent(notesEvent, includeAttendees);
		for (Event event : notesEvents) {
			Event existingEvent = getEvent(event,existingGoogleEvents);
			if (existingEvent == null) {
				insert(googleCalendar, event);
			} else {
				existingGoogleEvents.remove(existingEvent);
				fireUnchanged(existingEvent);
			}
		}
		for (Event event : existingGoogleEvents) {
			delete(googleCalendar,event);
		}
	}




	private Event getEvent(Event event, List<Event> exitingGoogleEvents) {
		for (Event ge : exitingGoogleEvents) {
			if (equals(ge,event)) {
				return ge;
			}
		}
		return null;
	}



	private boolean equals(Event e1, Event e2)  {
		if (!equals(e1.getSummary(),e2.getSummary())) {
			return false;
		}
		if (!equals(e1.getDescription(),e2.getDescription())) {
			return false;
		}
		if (!equals(e1.getLocation(),e2.getLocation())) {
			return false;
		}
		if (!equals(e1.getStart(),e2.getStart())) {
			return false;
		}
		if (!equals(e1.getEnd(),e2.getEnd())) {
			return false;
		}
		if (!GoogleUtil.equalsAttendees(e1.getAttendees(), e2.getAttendees())) {
			return false;
		}
		return true;
	}



	private boolean equals(EventDateTime t1, EventDateTime t2) {
		String s1 = t1.getDateTime().toStringRfc3339();
		String s2 = t2.getDateTime().toStringRfc3339();
		return equals(s1,s2);
	}



	private boolean equals(String s1, String s2) {
		if (s1 == null && s2 == null) {
			return true;
		}
		if (s1 == null || s2 == null) {
			if (s1 == null && s2.equals("")) {
				return true;
			} else if (s1.equals("")) {
				return true;
			}
			return false;
		}
		return s1.equals(s2);
	}



	private List<Event> removeRepeating(List<Event> googleEvents, String notesId) {
		List<Event> result = new ArrayList<Event>();
		for (Event event : googleEvents) {
			String nId = GoogleUtil.getNodesId(event);
			if (nId != null && nId.equals(notesId)) {
				result.add(event);
			}
		}
		googleEvents.removeAll(result);
		return result;
	}



	private int insert(GoogleCalendarV3 googleCalendar, No2goCalendarEvent notesEvent) throws IOException {
		Event event = GoogleUtil.createEvent(notesEvent, includeAttendees);
		return insert(googleCalendar, event);
	}



	private int insert(GoogleCalendarV3 googleCalendar, Event event) throws IOException {
		fireInfo("Insert " + event.getSummary());

		fireInsert(event);
		googleCalendar.insert(event);
		return 1;
	}

	private int delete(GoogleCalendarV3 googleCalendar, Event event) throws IOException {
		fireInfo("Delete " + event.getSummary());

		fireDelete(event);
		googleCalendar.delete(event);
		return 1;
	}


//	private void deleteAndRemove(GoogleCalendarV3 googleCalendar, List<Event> googleEvents, String notesId) throws IOException {
//		List<Event> deleteList = new ArrayList<Event>();
//		for (Event event : googleEvents) {
//			String nId = GoogleUtil.getNodesId(event);
//			if (nId != null && nId.equals(notesId)) {
//				deleteList.add(event);
//			}
//		}
//		for (Event delEvent : deleteList) {
//			googleCalendar.delete(delEvent);
//		}
//		googleEvents.removeAll(deleteList);
//		
//	}



	private int updateOrUnchanged(GoogleCalendarV3 googleCalendar, Event googleEvent, No2goCalendarEvent notesEvent) throws Exception {
		int number = 0; 
		if (update(googleEvent,notesEvent)) {
			fireInfo("Update " + notesEvent.getTitle());
			fireUpdate(googleEvent);
			googleCalendar.update(googleEvent);
			number++;
		} else {
			fireUnchanged(googleEvent);
		}
		return number;
		
	}

	private boolean update(Event googleEvent, No2goCalendarEvent notesEvent) {
		boolean update = false;
		if (GoogleUtil.updateTitle(googleEvent, notesEvent)) {
			update = true;
		}
		if (GoogleUtil.updateDescription(googleEvent, notesEvent)) {
			update = true;
		}
		if (GoogleUtil.updateAttendees(googleEvent, notesEvent)) {
			update = true;
		}

		if (GoogleUtil.updateNoRepeatingWhen(googleEvent, notesEvent)) {
			update = true;
			
		}
		
		return update;
	}






	/**
	 * Get the google event for the given notesID. If the event is found
	 * it will be removed from the given list.
	 * 
	 * @param googleEvents
	 * @param notesId
	 * @return the google event for the given notesID or null
	 */
	private Event getAndRemoveGoogleEvent(List<Event> googleEvents, String notesId) {
		Event result = null;
		for (Event event : googleEvents) {
			String id = GoogleUtil.getNodesId(event);
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
		logger.info(info);
		for (No2goSynchListener listener : listenerList) {
			listener.info(info);
		}
	}


	private void fireSynchBegin() {
		for (No2goSynchListener listener : listenerList) {
			listener.synchBegin();;
		}
	}

	private void fireSynchEnd() {
		for (No2goSynchListener listener : listenerList) {
			listener.synchEnd();;
		}
	}

	private void fireInsert(Event event) {
		for (No2goSynchListener listener : listenerList) {
			listener.insert(event);
		}
	}

	private void fireDelete(Event event) {
		for (No2goSynchListener listener : listenerList) {
			listener.delete(event);
		}
	}

	private void fireUpdate(Event event) {
		for (No2goSynchListener listener : listenerList) {
			listener.update(event);
		}
	}

	private void fireUnchanged(Event event) {
		for (No2goSynchListener listener : listenerList) {
			listener.unchanged(event);
		}
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



	@Override
	public void info(String string) {
		fireInfo(string);
		
	}



}
