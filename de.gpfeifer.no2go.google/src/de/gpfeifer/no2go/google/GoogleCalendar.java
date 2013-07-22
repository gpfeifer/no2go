package de.gpfeifer.no2go.google;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.google.gdata.client.ClientLoginAccountType;
import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import de.gpfeifer.no2go.core.No2goCalendar;
import de.gpfeifer.no2go.core.No2goCalendarEvent;
import de.gpfeifer.no2go.core.No2goUtil;


public class GoogleCalendar extends AbstractGoogleCalendar implements IGoogleCalendar {
	private static Logger logger = Logger.getLogger(GoogleCalendar.class.getName());
	

	private CalendarService calendarService;
	private boolean useHTTPS = true;
	
	public No2goCalendar getNo2goCalendar(int days, boolean useHTTP) throws IOException, ServiceException {
		Date now = new Date();
		List<No2goCalendarEvent> events = getCalendarEvents(now, No2goUtil.createDateOffset(now, days));
		return new No2goCalendar(events);
	}

	public List<No2goCalendarEvent> getCalendarEvents(Date start, Date end) throws IOException, ServiceException {

		List<No2goCalendarEvent> result = new ArrayList<No2goCalendarEvent>();
		CalendarService cs = getCalendarService();
		CalendarQuery myQuery = new CalendarQuery(getFeedURL());

		myQuery.setMinimumStartTime(new DateTime(start,TimeZone.getDefault()));
		myQuery.setMaximumStartTime(new DateTime(end,TimeZone.getDefault()));
		myQuery.setStringCustomParameter("orderby", "starttime");
		myQuery.setStringCustomParameter("sortorder", "ascending");
		myQuery.setMaxResults(65535);
		//myQuery.setStringCustomParameter("singleevents", "false");

		CalendarEventFeed workFeed;
		try {
			workFeed = (CalendarEventFeed) cs.query(myQuery, CalendarEventFeed.class);
		} catch (ServiceException e) {
			logger.severe(e.toXmlErrorMessage());
			throw e;
		}
		
		List<CalendarEventEntry> entries = workFeed.getEntries();
		for (CalendarEventEntry entry : entries) {
			result.add(Converter.createNo2goEvent(entry));
		}
		
		return result;
	}
	
	

	private URL getFeedURL() throws MalformedURLException {
		String url = useHTTPS ? "https:" : "http:"; 
		 url += "//www.google.com/calendar/feeds/" + googleAccountName + "/private/full";
		try {
			URL feedURL = new URL(url);
			return feedURL;
		} catch (MalformedURLException e) {
			logger.severe(e.toString());
			throw e;
		}
	}


	private CalendarService getCalendarService() throws AuthenticationException {

		if (calendarService == null) {
			calendarService = new CalendarService("no2go-2");
			try {
				calendarService.setUserCredentials(googleAccountName,googlePassword, ClientLoginAccountType.GOOGLE);
			} catch (AuthenticationException e) {
				if (e.getCause() instanceof ConnectException) {
					logger.severe(e.toXmlErrorMessage());
					try {
						calendarService.setUserCredentials(googleAccountName,googlePassword);
						logger.severe("Second try successfull");
					} catch (AuthenticationException e1) {
						logger.severe(e1.toXmlErrorMessage());
						throw e1;
					}
				} else {
					logger.severe(e.toXmlErrorMessage());
					throw e;
				}
			} 
		}

		return calendarService;
	}

//	public void update(List<ICalendarEvent> notesEntries,	List<ICalendarEvent> googleEntries) throws MalformedURLException, IOException, ServiceException {
//		System.out.println("-------------------------------");
//		for (ICalendarEvent notesEntry : notesEntries) {
//			if (notesEntry.isRepeating()) {
//				System.out.println("TODO: repeating:");
//				System.out.println(notesEntry);
////				updateRepeatingCalendarEntry(notesEntry,googleEntries);
//			} else {
//				if (!googleEntries.contains(notesEntry)) {
//					insert(notesEntry);
//				}
//			}
//		}
//	}

//	private void updateRepeatingCalendarEntry(ICalendarEntry notesEntry,
//			List<ICalendarEntry> googleEntries) throws MalformedURLException, IOException, ServiceException {
//		Date[] startDates = notesEntry.getStartDates();
//		Date[] endDates = notesEntry.getEndDates();
//		if (startDates.length != endDates.length) {
//			System.err.println("ERROR: startDates.length != endDates.length");
//			System.err.println(notesEntry);
//			return;
//		}
//		SNGCalendarEntry clone = notesEntry.clone();
//		for (int i = 0; i < startDates.length; i++) {
//			clone.setStartDate(startDates[i]);
//			clone.setEndDate(endDates[i]);
//			if (!googleEntries.contains(clone)) {
//				insert(clone);
//			}
//		}
//	}
	
	public  List<No2goCalendarEvent> saveCalendar(int days, String fileName) throws Exception {
		Date now = new Date();
		return saveCalendar(now, No2goUtil.createDateOffset(now, days), fileName);
	}
	
	private  List<No2goCalendarEvent> saveCalendar(Date start, Date end, String fileName) throws Exception {
		List<No2goCalendarEvent>  entries = getCalendarEvents(start, end);
		No2goCalendar calendar = new No2goCalendar();
		for (No2goCalendarEvent calendarEntry : entries) {
			calendar.add(calendarEntry);
		}
		
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(fileName);
			calendar.printOn(out);
		} finally {
			if (out != null) {
				out.close();
			}
		}
		return entries;
	}
	

	public void insert(No2goCalendarEvent event) throws MalformedURLException, IOException, ServiceException {
		CalendarEventEntry googleEntry = Converter.createGoogleEvent(event);
		getCalendarService().insert(getFeedURL(), googleEntry);
	}

	private void update(No2goCalendarEvent event) throws MalformedURLException, IOException, ServiceException {
		CalendarEventEntry googleEntry = Converter.createGoogleEvent(event);
		getCalendarService().insert(getFeedURL(), googleEntry);
		URL editUrl = new URL(googleEntry.getEditLink().getHref());
		getCalendarService().update(editUrl, googleEntry);
	}

	
//	public void execute(SynchCommand cmd) throws MalformedURLException, IOException, ServiceException {
// 		switch (cmd.getType()) {
//		case INSERT:
//			insert(cmd.getEvent());
//			break;
//		case UPDATE: 
//			CalendarEventEntry event = cmd.getEvent().asGoogleEvent();
//			URL editUrl = new URL(event.getEditLink().getHref());
////			String id = event.getIcalUID();
//			getCalendarService().update(editUrl, event);
//			break;
//		default:
//			break;
//		}
//	}



	public List<No2goCalendarEvent> getCalendarEntries(Date now, int days) throws IOException, ServiceException {
		return getCalendarEvents(now, No2goUtil.createDateOffset(now, days));
	}

	public void useHTTPS(boolean value) {
		useHTTPS = value;
	}

}
