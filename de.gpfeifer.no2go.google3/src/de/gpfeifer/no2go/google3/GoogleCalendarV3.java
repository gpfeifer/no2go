package de.gpfeifer.no2go.google3;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.gdata.util.ServiceException;

import de.gpfeifer.no2go.core.No2goCalendar;
import de.gpfeifer.no2go.core.No2goCalendarEvent;
import de.gpfeifer.no2go.core.No2goUtil;
import de.gpfeifer.no2go.google.AbstractGoogleCalendar;
import de.gpfeifer.no2go.google.IGoogleCalendar;

/**
 * This one use Version 3 of the Google Calendar API
 * 
 * @author gpfeifer
 * 
 */
public class GoogleCalendarV3 extends AbstractGoogleCalendar implements IGoogleCalendar {

	private static Logger logger = LoggerFactory.getLogger(GoogleCalendarV3.class);
	
	private Calendar calendar;
	private Credential credential;
	private GoogleCalendarListener listener;

	public No2goCalendar getNo2goCalendar(int days) throws IOException, ServiceException {
		Date now = new Date();
		List<No2goCalendarEvent> events = getCalendarEvents(now, No2goUtil.createDateOffset(now, days));
		return new No2goCalendar(events);
	}

	public No2goCalendar getNo2goCalendar(int days, boolean useHTTP) throws IOException, ServiceException {
		return getNo2goCalendar(days);
	}

	public List<No2goCalendarEvent> getCalendarEvents(int days) throws IOException, ServiceException {
		Date start = new Date();
		Date end = No2goUtil.createDateOffset(start, days);
		return getCalendarEvents(start, end);
	}

	public List<Event> getEventsWithNotesId(int days) throws IOException, ServiceException {
		Date start = new Date();
		Date end = No2goUtil.createDateOffset(start, days);
		List<Event> result = new ArrayList<Event>();
		List<Event> events = getGoogleEvents(start, end);

		for (Event event : events) {
			if (GoogleUtil.getNodesId(event) != null) {
//				GoogleUtil.i
				result.add(event);
			}
		}
		return result;
	}

	public List<No2goCalendarEvent> getCalendarEvents(Date start, Date end) throws IOException, ServiceException {

		List<No2goCalendarEvent> result = new ArrayList<No2goCalendarEvent>();
		List<Event> items = getGoogleEvents(start, end);
		if (items == null) {
			return null;
		}

		for (Event event : items) {
			No2goCalendarEvent no2goEvent = createNo2goEvent(event);
			if (no2goEvent != null) {
				result.add(no2goEvent);
			}
		}

		return result;
	}

	private List<Event> getGoogleEvents(Date start, Date end) throws IOException {
		Calendar calendar = getGoogleCalendar();
		if (calendar == null) {
			return null;
		}
		com.google.api.services.calendar.Calendar.Events.List eventList = calendar.events().list("primary");
		logger.debug("Google event list size " + eventList.size());
//		DateTime startTime = new DateTime(true, start.getTime(), TimeZone.getDefault().getOffset(start.getTime()));
//		DateTime endTime = new DateTime(true, end.getTime(), TimeZone.getDefault().getOffset(end.getTime()));
//		DateTime startTime = new  DateTime(end, TimeZone.getDefault());
//		DateTime endTime = new DateTime(end, TimeZone.getDefault());
		DateTime startTime = new  DateTime(dateWithoutTime(start));
		DateTime endTime = new DateTime(dateWithoutTime(end));

		eventList.setTimeMin(startTime);
		eventList.setTimeMax(endTime);
		List<Event> result = new ArrayList<Event>();
		String pageToken = null;
		logger.debug("End getGoogleEvents");
		do {
			Events events = eventList.setPageToken(pageToken).execute();
			List<Event> items = events.getItems();
			logger.debug("Read " + items.size());
			result.addAll(items);
			pageToken = events.getNextPageToken();
		} while (pageToken != null);
		logger.debug("End getGoogleEvents");
		return result;
	}

	/*
	 * { "created":"2013-04-08T18:13:00.000Z",
	 * "creator":{"displayName":"Gregor Pfeifer"
	 * ,"email":"gregor.pfeifer.gp@gmail.com","self":true},
	 * "end":{"dateTime":"2013-07-18T20:00:00.000+02:00"
	 * ,"timeZone":"Europe/Berlin"},
	 * "etag":"\"GOk3ibIuD_gKzsAhHIxtDNhbdzg/MjczMDg4OTU2MTgyNTAwMA\"",
	 * "htmlLink":
	 * "https://www.google.com/calendar/event?eid=MnQzbzRvOGpnY2J1aG4xOG5nYm0xYzBlaXMgZ3JlZ29yLnBmZWlmZXIuZ3BAbQ"
	 * , "iCalUID":"2t3o4o8jgcbuhn18ngbm1c0eis@google.com",
	 * "id":"2t3o4o8jgcbuhn18ngbm1c0eis", "kind":"calendar#event",
	 * "organizer":{"displayName"
	 * :"Gregor Pfeifer","email":"gregor.pfeifer.gp@gmail.com","self":true},
	 * "reminders"
	 * :{"overrides":[{"method":"popup","minutes":15}],"useDefault":false},
	 * "sequence":0,
	 * "start":{"dateTime":"2013-07-18T19:00:00.000+02:00","timeZone"
	 * :"Europe/Berlin"},
	 * "status":"confirmed","summary":"Lena Konzert","updated"
	 * :"2013-04-08T18:13:00.948Z"}
	 * 
	 * 
	 * 
	 * {"created":"2013-04-18T05:53:49.000Z",
	 * "creator":{"displayName":"Gregor Pfeifer"
	 * ,"email":"gregor.pfeifer.gp@gmail.com","self":true},
	 * "end":{"dateTime":"2013-04-18T12:30:00.000+02:00"
	 * ,"timeZone":"Europe/Berlin"},
	 * "etag":"\"GOk3ibIuD_gKzsAhHIxtDNhbdzg/MjczMjUyODg1OTY3NTAwMA\"",
	 * "htmlLink":
	 * "https://www.google.com/calendar/event?eid=cGF1YmVkMDRxajhoYmoydGFidnIxNWQ4Nm9fMjAxMzA0MThUMTAwMDAwWiBncmVnb3IucGZlaWZlci5ncEBt"
	 * , "iCalUID":"paubed04qj8hbj2tabvr15d86o@google.com",
	 * "id":"paubed04qj8hbj2tabvr15d86o", "kind":"calendar#event",
	 * "organizer":{"displayName"
	 * :"Gregor Pfeifer","email":"gregor.pfeifer.gp@gmail.com","self":true},
	 * "recurrence":["RRULE:FREQ=WEEKLY;WKST=MO;BYDAY=MO,TU,WE,TH,FR"],
	 * "reminders"
	 * :{"overrides":[{"method":"popup","minutes":5}],"useDefault":false},
	 * "sequence":0,
	 * "start":{"dateTime":"2013-04-18T12:00:00.000+02:00","timeZone"
	 * :"Europe/Berlin"},"status":"confirmed","summary":"High Noon","updated":
	 * "2013-04-18T05:53:49.968Z"}
	 */

	Date dateWithoutTime(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");      
	    try {
			return sdf.parse(sdf.format(date));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}


	private No2goCalendarEvent createNo2goEvent(Event event) {
		List<String> recurrence = event.getRecurrence();
		if (recurrence != null && !recurrence.isEmpty()) {
			// We ignore recurrence events, because NO2GO will not create one
			return null;
		}
		No2goCalendarEvent result = new No2goCalendarEvent();
		result.setGoogleId(event.getId());
		result.setTitle(event.getSummary());
		result.setDescription(event.getDescription());
		result.setLocation(event.getLocation());
		result.setWhenList(GoogleUtil.getWhenList(event));
		result.setNotesId(GoogleUtil.getNodesId(event));
		return result;
	}

	public Calendar getGoogleCalendar() {
		if (calendar == null) {
			initCalendar();
		}

		return calendar;
	}

	private void initCalendar() {
		credential = null;
		calendar = null;
		try {
			info("Get Credential");
			logger.debug("get credential");
			credential = CredentialProvider.INSTANCE.getCredential();
		} catch (IOException e) {
			logger.warn(e.toString());
		}
		
		if (credential != null) {
			HttpTransport httpTransport = Activator.getInstance().getHttpTransport(); 
			final JsonFactory jsonFactory = new JacksonFactory();
			info("Get Google service");
			logger.debug("Get Google Calendar service");
			calendar = new Calendar.Builder(httpTransport, jsonFactory, credential).setApplicationName("no2go").build();
			logger.debug("Google Calendar service: " + calendar.toString());
		} else {
			throw new RuntimeException("No credential");
		}
	}

	private void info(String string) {
		if (listener != null) {
			listener.info(string);
		}
		
	}



	public void insert(Event event) throws IOException {
		Calendar calendar = getGoogleCalendar();
		if (calendar == null) {
			return;
		}

		com.google.api.services.calendar.model.Calendar cal = calendar.calendars().get("primary").execute();
		calendar.events().insert(cal.getId(), event).execute();
	}

	@Override
	public List<No2goCalendarEvent> saveCalendar(int i, String string) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public void update(Event googleEvent) throws Exception {
		Calendar calendar = getGoogleCalendar();
		if (calendar == null) {
			return;
		}

		com.google.api.services.calendar.model.Calendar cal = calendar.calendars().get("primary").execute();
		calendar.events().update(cal.getId(), googleEvent.getId(), googleEvent).execute();

	}

	public void delete(Event event) throws IOException {
		Calendar calendar = getGoogleCalendar();
		if (calendar == null) {
			return;
		}

		com.google.api.services.calendar.model.Calendar cal = calendar.calendars().get("primary").execute();
		calendar.events().delete(cal.getId(), event.getId()).execute();
	}

	/**
	 * @param listener the listener to set
	 */
	public void setListener(GoogleCalendarListener listener) {
		this.listener = listener;
	}


}
