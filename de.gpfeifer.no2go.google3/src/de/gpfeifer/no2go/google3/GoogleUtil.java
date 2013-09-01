package de.gpfeifer.no2go.google3;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.ExtendedProperties;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;

import de.gpfeifer.no2go.core.No2goAttendee;
import de.gpfeifer.no2go.core.No2goCalendarEvent;
import de.gpfeifer.no2go.core.No2goWhen;

public class GoogleUtil {

	private static final String LOTUS_NOTES_ID = "lotus-notes-id";
	
	public static List<Event> convertRepeatingEvent(No2goCalendarEvent no2goEvent, boolean includeAttendees) {
		List<No2goWhen> whenList = no2goEvent.getWhenList();
		if (whenList.size() <= 1) {
			throw new RuntimeException("whenList.size() != 1");
		}

		List<Event> result = new ArrayList<Event>();
		for (No2goWhen no2goWhen : whenList) {
			Event ge = createBaseEvent(no2goEvent, includeAttendees);
			setWhen(ge,no2goWhen);
			result.add(ge);

		}
		return result;
	}

	public static Event createEvent(No2goCalendarEvent ne, boolean includeAttendees) {
		Event ge = createBaseEvent(ne, includeAttendees);
		setWhen(ge,ne.getWhenList());
		return ge;
	}

	public static String getNodesId(Event event) {
		ExtendedProperties extendedProperties = event.getExtendedProperties();
		if (extendedProperties == null) {
			return null;
		}
		Map<String, String> map = extendedProperties.getShared();
		if (map == null) {
			return null;
		}
		return map.get(LOTUS_NOTES_ID);
	}

	public static long getTime(EventDateTime eventDateTime) {
		DateTime dateTime = eventDateTime.getDateTime();
		if (dateTime != null) {
			return dateTime.getValue() + dateTime.getTimeZoneShift();
		}
		DateTime date = eventDateTime.getDate();
		if (date != null) {
			return date.getValue() + date.getTimeZoneShift();
		}
		return 0;
	}

	public static List<No2goWhen> getWhenList(Event event) {
		List<No2goWhen> result = new ArrayList<No2goWhen>();
		No2goWhen when = new No2goWhen();
		
		EventDateTime start = event.getStart();
		EventDateTime end = event.getEnd();
		DateTime dateTime = start.getDateTime();
		if (dateTime == null) {
			when.setAllDayEvent(true);
			when.setStartTime(convert(start.getDate()));
			when.setEndTime(convert(end.getDate()));
		} else {
			when.setAllDayEvent(false);

			when.setStartTime(convert(start.getDateTime()));
			when.setEndTime(convert(end.getDateTime()));
		}
		result.add(when);
		return result;
	}

	public static boolean isAllDayEvent(Event event) {
		return event.getStart().getDateTime() == null;
	}

	public static boolean isPastEvent(Event event) {
		Date now = new Date();
		long nowTimeRaw = now.getTime();
//		long nowTime = nowTimeRaw + Calendar.getInstance().getTimeZone().getOffset(nowTimeRaw); 
		long endTime = GoogleUtil.getTime(event.getEnd());
		return nowTimeRaw > endTime;
	}

	public static void setNodesId(Event event, String notesID) {
		ExtendedProperties extendedProperties = event.getExtendedProperties();
		if (extendedProperties == null) {
			extendedProperties = new ExtendedProperties();
			event.setExtendedProperties(extendedProperties);
		}
		Map<String, String> map = extendedProperties.getShared();
		if (map == null) {
			map = new HashMap<String, String>();
			extendedProperties.setShared(map);
		}
		map.put(LOTUS_NOTES_ID,notesID);
	}

	
	public static Date toJavaDate(EventDateTime eventDateTime) {
		long time = getTime(eventDateTime);
		return new Date(time);
	}


	public static boolean updateAttendees(Event ge, No2goCalendarEvent ne) {
		List<EventAttendee> googleAttendees = ge.getAttendees();
		List<EventAttendee> notesAttendees = createAttendeeList(ne);
		if (equalsAttendees(googleAttendees,notesAttendees)) {
			return false;
		} else {
			ge.setAttendees(notesAttendees);
			return true;
		}
	}

	public static boolean updateDescription(Event ge, No2goCalendarEvent ne) {
		if (equals(ge.getDescription(), ne.getDescription())) {
			return false;
		} else {
			ge.setDescription(ne.getDescription());
			return true;
		}
	}

	public static boolean updateNoRepeatingWhen(Event ge, No2goCalendarEvent ne) {
		if (equals(getWhenList(ge), ne.getWhenList())) {
			return false;
		} else {
			setWhen(ge,ne.getWhenList());
			return true;
		}
	}

	
	public static boolean updateTitle(Event ge, No2goCalendarEvent ne) {
		if (equals(ge.getSummary(), ne.getTitle())) {
			return false;
		} else {
			ge.setSummary(ne.getTitle());
			return true;
		}
	}

	private static List<EventAttendee> createAttendeeList(No2goCalendarEvent ne) {
		List<EventAttendee> result = new ArrayList<EventAttendee>();
		List<No2goAttendee> attendees = ne.getAttendees();
		for (No2goAttendee no2goAttendee : attendees) {
			EventAttendee attendee = new EventAttendee();
			attendee.setDisplayName(no2goAttendee.getDisplayName());
			attendee.setEmail(no2goAttendee.getEmail());
			result.add(attendee);
			
		}
		return result;
	}

	private static Event createBaseEvent(No2goCalendarEvent ne, boolean includeAttendees) {
		Event ge = new Event();
		ge.setSummary(ne.getTitle());
		ge.setDescription(ne.getDescription());
		ge.setLocation(ne.getLocation());
		if (includeAttendees) {
			ge.setAttendees(createAttendeeList(ne));
		}
		setNodesId(ge, ne.getNotesId());
		return ge;
	}

	private static boolean equals(List<No2goWhen> googleWhenList, List<No2goWhen> notesWhenList) {
		if (googleWhenList.size() != 1 ||  notesWhenList.size() != 1) {
			return false;
		}
		No2goWhen googleWhen = googleWhenList.get(0);
		No2goWhen notesWhen = notesWhenList.get(0);
		return googleWhen.equals(notesWhen);
	}

	public static boolean equalsAttendees(List<EventAttendee> googleAttendees, List<EventAttendee> notesAttendees) {
		if (googleAttendees == null ) {
			return false;
			
		}
		if (googleAttendees.size() != notesAttendees.size()) {
			return false;
		}
		for (EventAttendee notesAttendee : notesAttendees) {
			if (!containAttendee(googleAttendees, notesAttendee)) {
				return false;
			}
		}
		return true;
	}

	private static boolean containAttendee(List<EventAttendee> googleAttendees, EventAttendee notesAttendee) {
		for (EventAttendee eventAttendee : googleAttendees) {
			if (eventAttendee.getDisplayName().equals(notesAttendee.getDisplayName())) {
				return true;
			}
		}
		return false;
	}

	private static DateTime getDate(Date startTime) {
		long time = startTime.getTime();
		return new DateTime(true, time, TimeZone.getDefault().getOffset(time));
	}

	private static DateTime getDateTime(Date startTime) {
		return new DateTime(startTime,TimeZone.getDefault());
	}

	private static void setWhen(Event ge, List<No2goWhen> whenList) {
		if (whenList.size() != 1) {
			throw new RuntimeException("whenList.size() != 1");
		}
		No2goWhen when = whenList.get(0);
		setWhen(ge, when);
	}
	
	private static void setWhen(Event ge, No2goWhen when) {
		EventDateTime start = new EventDateTime();
		EventDateTime end = new EventDateTime();
		if (when.isAllDayEvent()) {
			start.setDate(getDate(when.getStartTime()));
			end.setDate(getDate(when.getEndTime()));
		} else {
			start.setDateTime(getDateTime(when.getStartTime()));
			end.setDateTime(getDateTime(when.getEndTime()));
		}
		ge.setStart(start);
		ge.setEnd(end);
	}

	static Date convert(DateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		return new Date(dateTime.getValue() + dateTime.getTimeZoneShift());
//		return new Date(dateTime.getValue());
	}

	static boolean equals(String s1, String s2) {
		if (s1 == null && s2 == null) {
			return true;
		}
		if (s1 == null && s2.trim().equals("")) {
			return true;
		}
		if (s2 == null && s1.trim().equals("")) {
			return true;
		}

		return normalize(s1).equals(normalize(s2));
	}

	/**
	 * For unknown reasons \t get lost. To compare two strings remove \t, \n \r;
	 * @param s
	 * @return
	 */
	static String normalize(String s) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
				case '\t':
				case '\n':
				case '\r':				
					
					break;

				default:
					result.append(c);
				break;
			}
		}
		return result.toString();
	}


}
