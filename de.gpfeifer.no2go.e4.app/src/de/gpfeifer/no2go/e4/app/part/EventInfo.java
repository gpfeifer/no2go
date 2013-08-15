package de.gpfeifer.no2go.e4.app.part;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import de.gpfeifer.no2go.google3.GoogleUtil;

public class EventInfo {

	String notesId;

	public int insert = 0;
	public int update = 0;
	public int delete = 0;
	public int unchanged = 0;
	Map<String,Event> events = new HashMap<String,Event>();
	
	public EventInfo(String notesId) {
		this.notesId = notesId;
	}

	public void addEvent(Event event) {
		events.remove(null);
		events.put(event.getId(),event);
	}
	
	public void deletePastEvents() {
		List<String> keys = new ArrayList<String>();
		for (Entry<String, Event> entry : events.entrySet()) {
			if (GoogleUtil.isPastEvent(entry.getValue())) {
				keys.add(entry.getKey());
			}
		}
		for (String key : keys) {
			events.remove(key);
		}
	}
	
	public boolean hasEvents() {
		return !events.isEmpty();
	}
	
	public List<Event> getEvents() {
		Collection<Event> values = events.values();
		List<Event> list = new ArrayList<Event>(values);
		Collections.sort(list, new Comparator<Event>() {
			@Override
			public int compare(Event o1, Event o2) {
				return (int) (GoogleUtil.getTime(o1.getStart()) - GoogleUtil.getTime(o2.getStart()));
			}
		});
		return list;
	}


	public String getTitle() {
		if (events.values().isEmpty()) {
			return "No event";
		}
		Iterator<Event> iterator = events.values().iterator();
		return iterator.next().getSummary();
	}
	
	public String printInsert() {
		return "" + insert;
	}
	
	public String printUpdate() {
		return "" + update;
	}
	public String printDelete() {
		return "" + delete;
	}
	
	public String printUnchanged() {
		return "" + unchanged;
	}

	public String printNumberOfEvents() {
		return "" + events.values().size();
	}

	public String printStartTime() {
		Event event = getFirstEvent();
		if (event == null) {
			return null;
		}
		return printTime(event, event.getStart());
	}


	public String printEndTime() {
		Event event = getFirstEvent();
		if (event == null) {
			return null;
		}
		return printTime(event, event.getEnd());
	}
	
	private Event getFirstEvent() {
		List<Event> list = getEvents();
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}
	private String printTime(Event event, EventDateTime eventDateTime) {
		Date date = GoogleUtil.toJavaDate(eventDateTime);
		DateFormat dfmt; 
		if (GoogleUtil.isAllDayEvent(event)) {
			dfmt = new SimpleDateFormat( "E dd.MM");
		} else if (isToday(date)) {
			dfmt = new SimpleDateFormat( "HH:mm");
		} else {
			dfmt = new SimpleDateFormat( "E dd.MM ' - 'HH:mm");
		}
		return dfmt.format(date);
	}

	private boolean isToday(Date date) {
		// Ugly
		DateFormat dayFormat = new SimpleDateFormat("dd.MM.yy");
		String s1 = dayFormat.format(date);
		String s2 = dayFormat.format(new Date());
		return s1.equals(s2);
	}

}
