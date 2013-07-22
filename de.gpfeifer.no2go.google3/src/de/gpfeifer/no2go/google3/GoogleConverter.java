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
import com.google.api.services.calendar.model.EventDateTime;

import de.gpfeifer.no2go.core.No2goCalendarEvent;
import de.gpfeifer.no2go.core.No2goWhen;

public class GoogleConverter {

	private static final String LOTUS_NOTES_ID = "lotus-notes-id";
	
	public static boolean updateTitle(Event ge, No2goCalendarEvent ne) {
		if (equals(ge.getSummary(), ne.getTitle())) {
			return false;
		} else {
			ge.setSummary(ne.getTitle());
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

	static Date convert(DateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		return new Date(dateTime.getValue() + dateTime.getTimeZoneShift());
//		return new Date(dateTime.getValue());
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

	
	static boolean equals(String googleString, String notesString) {
		if (googleString == null) {
			return false;
		}
		return normalize(googleString).equals(normalize(notesString));
	}


	private static boolean equals(List<No2goWhen> googleWhenList, List<No2goWhen> notesWhenList) {
		if (googleWhenList.size() != 1 ||  notesWhenList.size() != 1) {
			return false;
		}
		No2goWhen googleWhen = googleWhenList.get(0);
		No2goWhen notesWhen = notesWhenList.get(0);
		return googleWhen.equals(notesWhen);
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

	public static Event createEvent(No2goCalendarEvent ne) {
		Event ge = createBaseEvent(ne);
		setWhen(ge,ne.getWhenList());
		return ge;
	}

	private static Event createBaseEvent(No2goCalendarEvent ne) {
		Event ge = new Event();
		ge.setSummary(ne.getTitle());
		ge.setDescription(ne.getDescription());
		ge.setLocation(ne.getLocation());
		setNodesId(ge, ne.getNotesId());
		return ge;
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
			start.setDateTime(getDate(when.getStartTime()));
			end.setDateTime(getDate(when.getEndTime()));
		}
		ge.setStart(start);
		ge.setEnd(end);
	}

	private static DateTime getDate(Date startTime) {
		
		return new DateTime(startTime,TimeZone.getDefault());
	}

	public static List<Event> convertRepeatingEvent(No2goCalendarEvent no2goEvent) {
		List<No2goWhen> whenList = no2goEvent.getWhenList();
		if (whenList.size() <= 1) {
			throw new RuntimeException("whenList.size() != 1");
		}

		List<Event> result = new ArrayList<Event>();
		for (No2goWhen no2goWhen : whenList) {
			Event ge = createBaseEvent(no2goEvent);
			setWhen(ge,no2goWhen);
			result.add(ge);

		}
		return result;
	}

}
