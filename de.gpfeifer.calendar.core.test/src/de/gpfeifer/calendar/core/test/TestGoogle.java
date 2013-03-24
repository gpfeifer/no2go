package de.gpfeifer.calendar.core.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.NullProgressMonitor;

import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.extensions.OriginalEvent;
import com.google.gdata.util.ServiceException;

import de.gpfeifer.calendar.core.CalendarUtil;
import de.gpfeifer.calendar.core.ICalendarEvent;
import de.gpfeifer.calendar.core.VCalendar;
import de.gpfeifer.calendar.google.GoogleCalendar;
import junit.framework.TestCase;

public class TestGoogle extends TestCase {
	
	public void testGoogle() throws Exception {
		Properties properties = new Properties();
		properties.load(new FileInputStream("p_google.properties"));
		GoogleCalendar cal = new GoogleCalendar();
		cal.setGoogleAccountName(properties.getProperty("AccountName"));
		cal.setGooglePassword(properties.getProperty("pwd"));
		List<ICalendarEvent> calendarEntries = cal.getCalendarEntries(new Date(), 5);
		for (ICalendarEvent ce : calendarEntries) {
			System.out.println(ce);
			CalendarEventEntry ge = ce.asGoogleEvent();
			System.out.println("******************ID:"+ ge.getIcalUID());
			OriginalEvent org = ge.getOriginalEvent();
			System.out.println(org);
		}
			
//		cal.saveCalendar(new Date(), CalendarUtil.createDateWeekOffset(new Date(), 50), "googletest.ics");
////		VCalendar calendar = VCalendar.read("googletest.ics");
//		List<ICalendarEvent> calendarEntries = calendar.getCalendarEvents();
//		for (ICalendarEvent entry : calendarEntries) {
//			entry.getTimes();
//		}

	
	}

}
