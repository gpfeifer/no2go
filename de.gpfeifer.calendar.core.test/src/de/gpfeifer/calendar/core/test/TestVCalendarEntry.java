package de.gpfeifer.calendar.core.test;


import java.util.GregorianCalendar;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.RDate;
import junit.framework.TestCase;
import de.gpfeifer.calendar.core.VCalendarEvent;

public class TestVCalendarEntry extends TestCase {

	public void testVCalendarEntry() {
		VCalendarEvent event = new VCalendarEvent();
		event.setTitle("t1");
		assertEquals("t1", event.getTitle());
	}
	
	public void testVEventRDate() {
		VEvent vEvent = new VEvent(new Date(), "title");
		PeriodList periods = new PeriodList(true);
		DateTime start = new DateTime(new Date());
		DateTime end = new DateTime(new Date());
		periods.add(new Period(start, end));
		periods.add(new Period(start, end));
		vEvent.getProperties().add(new RDate(periods));
		System.out.println(vEvent);
	}

	public void testTimezoe() {
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		TimeZone timezone = registry.getTimeZone("Australia/Melbourne");

		java.util.Calendar cal = GregorianCalendar.getInstance();
		cal.set(java.util.Calendar.YEAR, 2005);
		cal.set(java.util.Calendar.MONTH, java.util.Calendar.NOVEMBER);
		cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
		cal.set(java.util.Calendar.HOUR_OF_DAY, 15);
		cal.clear(java.util.Calendar.MINUTE);
		cal.clear(java.util.Calendar.SECOND);

		DateTime dt = new DateTime(cal.getTime());
		dt.setTimeZone(timezone);
		VEvent ausTime = new VEvent(dt, "aus");
		
		VCalendarEvent ve = new VCalendarEvent(ausTime);
		ve.getTimes();
		
	}
}
