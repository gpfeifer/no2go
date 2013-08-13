package de.gpfeifer.no2go.test;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.gdata.util.ServiceException;

import de.gpfeifer.no2go.core.No2goCalendar;
import de.gpfeifer.no2go.core.No2goCalendarEvent;
import de.gpfeifer.no2go.google3.GoogleCalendarV3;
import de.gpfeifer.no2go.google3.GoogleUtil;

public class TestGoogle3 {

	public void test() throws IOException, ServiceException, InterruptedException {
		System.out.println(new Date());
		GoogleCalendarV3 v3 = new GoogleCalendarV3();
		No2goCalendar no2goCalendar = v3.getNo2goCalendar(10);
		System.out.println(no2goCalendar);
		no2goCalendar = v3.getNo2goCalendar(10);
		System.out.println(no2goCalendar);

		for (int i = 0; i < 100; i++) {
			Thread.sleep(1000 * 60);
			System.out.println(new Date());
			v3 = new GoogleCalendarV3();
			no2goCalendar = v3.getNo2goCalendar(10);
			System.out.println(no2goCalendar);
		}

	}


	@Test
	public void testInsert() throws Exception {
		GoogleCalendarV3 v3 = new GoogleCalendarV3();
		List<No2goCalendarEvent> events = v3.getCalendarEvents(1);
		print(events);
		v3.insert(newEvent());
		events = v3.getCalendarEvents(1);
		print(events);

	}

	private void print(List<No2goCalendarEvent> events) {
		System.out.println("Number: " + events.size());
		for (No2goCalendarEvent event: events) {
			System.out.println(event);
		}
		
	}


	private static Event newEvent() {
		Event event = new Event();
		event.setSummary("New Event");
		Date startDate = new Date();
		Date endDate = new Date(startDate.getTime() + 3600000);
		DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
		event.setStart(new EventDateTime().setDateTime(start));
		DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
		event.setEnd(new EventDateTime().setDateTime(end));
		GoogleUtil.setNodesId(event, "notesid");
		return event;
	}
}
