package de.gpfeifer.calendar.core.test;

import java.io.IOException;
import java.util.List;

import com.google.gdata.data.extensions.When;

import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.property.RDate;
import de.gpfeifer.calendar.core.ICalendarEvent;
import de.gpfeifer.calendar.core.VCalendar;
import junit.framework.TestCase;

public class TestCalendar extends TestCase {
	
	public void testReadNotes() throws IOException, ParserException {
		VCalendar calendar = VCalendar.read("notes.ics");
		List<ICalendarEvent> calendarEntries = calendar.getCalendarEvents();
		for (ICalendarEvent entry : calendarEntries) {
			entry.getTimes();
		}
	}
	
	public void testReadGoogle() throws IOException, ParserException {
		VCalendar calendar = VCalendar.read("googletest.ics");
		List<ICalendarEvent> calendarEntries = calendar.getCalendarEvents();
		for (ICalendarEvent entry : calendarEntries) {
			entry.getTimes();
		}
	}

	public void testReadNotesexport() throws IOException, ParserException {
		VCalendar calendar = VCalendar.read("rdate.ics");
		List<ICalendarEvent> calendarEntries = calendar.getCalendarEvents();
		for (ICalendarEvent entry : calendarEntries) {
			RDate rdate = (RDate) entry.asVEvent().getProperty(Property.RDATE);
			if (rdate != null) {
				DateList dates = rdate.getDates();
				System.out.println(dates);
			}
			List<When> times = entry.getTimes();
			for (When when : times) {
				System.out.println(when.getStartTime() + " " + when.getEndTime());
			}
			
		}
	}

}
