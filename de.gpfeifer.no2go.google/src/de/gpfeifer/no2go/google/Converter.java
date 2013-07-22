package de.gpfeifer.no2go.google;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.extensions.When;
import com.google.gdata.data.extensions.Where;

import de.gpfeifer.no2go.core.No2goCalendarEvent;
import de.gpfeifer.no2go.core.No2goWhen;


public class Converter {
	
	static CalendarEventEntry createGoogleEvent(No2goCalendarEvent event) {
		CalendarEventEntry googleEntry = new CalendarEventEntry();
//		googleEntry.setId(event.getEventId());
		googleEntry.setTitle(new PlainTextConstruct(event.getTitle()));
		if (event.getDescription() != null) {
			googleEntry.setContent(new PlainTextConstruct(event.getDescription()));
		}
		
		Where location = new Where();
		location.setValueString(event.getLocation());
		googleEntry.addLocation(location);
		
		if (event.isRepeating()) {
			throw new UnsupportedOperationException("Repeating not implemented");
//			googleEntry.getTimes().clear();
//			Recurrence recur = new Recurrence();
//			recur.setValue(CalendarUtil.getRecurrenceData2(event));
//			googleEntry.setRecurrence(recur);
		} else {
			googleEntry.getTimes().clear();
			googleEntry.getTimes().addAll(getTimes(event.getWhenList()));
		}
		
		return googleEntry;
	}
	
	private static List<When> getTimes(List<No2goWhen> whenList) {
		List<When> result = new ArrayList<When>();
		for (No2goWhen no2goWhen : whenList) {
			When when = new When();
			when.setStartTime(convert(no2goWhen.getStartTime(),no2goWhen.isAllDayEvent()));
			when.setEndTime(convert(no2goWhen.getEndTime(),no2goWhen.isAllDayEvent()));
			result.add(when);
		}
		return result;
	}

	private static DateTime convert(Date date, boolean dayOnly) {
		DateTime result = new DateTime(date,Calendar.getInstance().getTimeZone());
		result.setDateOnly(dayOnly);
		return result;
	}

	static No2goCalendarEvent createNo2goEvent(CalendarEventEntry event) {
		No2goCalendarEvent result = new No2goCalendarEvent();
		result.setGoogleId(event.getId());
//		String etag = event.getEtag();
//		String cid = event.getIcalUID();
//		System.out.println("--");
//		System.out.println(etag);
//		System.out.println(cid);
		result.setTitle(event.getTitle().getPlainText());
		result.setDescription(event.getPlainTextContent());
		result.setLocation(getLocation(event));
		result.setWhenList(getWhenList(event));
		return result;
	}
	
	static private String getLocation(CalendarEventEntry event) {
		List<Where> locations = event.getLocations();
		StringBuffer buffer = new StringBuffer();
		for (Where where : locations) {
			buffer.append(where.getValueString()+ "\n");
		}
		return buffer.toString();
	}

	static private List<No2goWhen> getWhenList(CalendarEventEntry event) {
		List<No2goWhen> result = new ArrayList<No2goWhen>();
		for (When googleWhen : event.getTimes()) {
			No2goWhen when = new No2goWhen();
			when.setStartTime(convert(googleWhen.getStartTime()));
			when.setEndTime(convert(googleWhen.getEndTime()));
			when.setAllDayEvent(googleWhen.getStartTime().isDateOnly());
			result.add(when);
		}
		return result;
	}

	static private Date convert(DateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		Integer tzShift = dateTime.getTzShift();
		return new Date(dateTime.getValue() + (tzShift == null ?  0 : tzShift));
	}


}
