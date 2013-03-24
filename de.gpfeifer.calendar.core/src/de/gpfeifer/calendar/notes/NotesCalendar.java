package de.gpfeifer.calendar.notes;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.NotesThread;
import lotus.domino.Session;

import com.google.gdata.data.extensions.When;

import de.gpfeifer.calendar.core.CalendarUtil;
import de.gpfeifer.calendar.core.ICalendarEvent;
import de.gpfeifer.calendar.core.Log;
import de.gpfeifer.calendar.core.VCalendar;
import de.gpfeifer.calendar.core.VCalendarEvent;

public class NotesCalendar {
	
	public static class StreamListener extends Thread {
		
	    protected InputStream inputStream;

		protected PrintStream out;
	    public StreamListener(InputStream inputStream, PrintStream out) {
			super();
			this.inputStream = inputStream;
			this.out = out;
		}
	    
	    public void run() {

            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            try {
				while ( (line = br.readLine()) != null) {
					out.println(line);
				}
			} catch (IOException e) {
				out.println(e.getMessage());
			}
	    }
	    
	}

	static public final int ERR_INAVLID_ARGS = 1;
	static public final int ERR_INAVLID_CLASSPATH = 2;
	static public final int ERR_INAVLID_LIBRARY_PATH = 2;
	
	

		private Database database;

	private String mailfile;
	private String password;
	private String server;

	String notesVersion;
	public NotesCalendar() {
		notesVersion = "unknown";
	}
//	private SNGCalendarEntry createSNGCalendarEntry(Document doc, Date now) {
//		VEvent event = new VEvent();
//		
//		Item lnItem = doc.getFirstItem("Subject");
//		if (!isItemEmpty(lnItem)) {
//			event.getProperties().addcal.title = lnItem.getText();
//		}
//
//		lnItem = doc.getFirstItem("Body");
//		if (!isItemEmpty(lnItem)) {
//			cal.description = lnItem.getText();
//		}
//
//		cal.location = "";
//		lnItem = doc.getFirstItem("Location");
//		if (!isItemEmpty(lnItem)) {
//			 cal.location = lnItem.getText();
//		}
//		lnItem = doc.getFirstItem("Room");
//		if (!isItemEmpty(lnItem)) {
//		 	cal.location += " - " +  lnItem.getText();
//		}
// 
//		// Get the type of Lotus calendar entry
//		// lnItem = doc.getFirstItem("Form");
//		// if (!isItemEmpty(lnItem))
//		// cal.setEntryType(lnItem.getText());
//		// else
//		// // Assume we have an appointment
//		// cal.setEntryType(NotesCalendarEntry.EntryType.APPOINTMENT);
//		//
//		// if (cal.getEntryType() == NotesCalendarEntry.EntryType.APPOINTMENT)
//		// {
//		// lnItem = doc.getFirstItem("AppointmentType");
//		// if (!isItemEmpty(lnItem))
//		// cal.setAppointmentType(lnItem.getText());
//		// }
//
//
//		// lnItem = doc.getFirstItem("$Alarm");
//		// if (!isItemEmpty(lnItem)) {
//		// cal.setAlarm(true);
//		// lnItem = doc.getFirstItem("$AlarmOffset");
//		// if (!isItemEmpty(lnItem))
//		// cal.setAlarmOffsetMins(Integer.parseInt(lnItem.getText()));
//		// }
//
//		lnItem = doc.getFirstItem("OrgRepeat");
//		// If true, this is a repeating calendar entry
//		if (!isItemEmpty(lnItem)) {
//			Vector<DateTime> lnStartDates = null;
//			Vector<DateTime> lnEndDates = null;
//
//			lnItem = doc.getFirstItem("StartDateTime");
//			if (!isItemEmpty(lnItem)) {
//				lnStartDates = lnItem.getValueDateTimeArray();
//				Date[] startDates = new Date[lnStartDates.size()];
//				for (int i = 0; i < startDates.length; i++) {
//					startDates[i] = lnStartDates.get(i).toJavaDate();
//				}
//				cal.setStartDates(startDates);
//			}
//			lnItem = doc.getFirstItem("EndDateTime");
//			if (!isItemEmpty(lnItem)) {
//				lnEndDates = lnItem.getValueDateTimeArray();
//				Date[] endDates = new Date[lnEndDates.size()];
//				for (int i = 0; i < endDates.length; i++) {
//					endDates[i] = lnEndDates.get(i).toJavaDate();
//				}
//				cal.setEndDates(endDates);
//			}
//
//		} else {
//			lnItem = doc.getFirstItem("StartDateTime");
//			if (!isItemEmpty(lnItem)) {
//				Date start = lnItem.getDateTimeValue().toJavaDate();
//				if (now.after(start)) {
//					return null;
//				}
//				cal.setStartDate(lnItem.getDateTimeValue().toJavaDate());
//			}
//
//			// For To Do tasks, the EndDateTime doesn't exist, but there is an
//			// EndDate value
//			lnItem = doc.getFirstItem("EndDateTime");
//			if (isItemEmpty(lnItem)) {
//				lnItem = doc.getFirstItem("EndDate");
//			}
//			if (!isItemEmpty(lnItem)) {
//				cal.setEndDate(lnItem.getDateTimeValue().toJavaDate());
//			}
//
//		}
//
//
//		event.getProperty(Property.SUMMARY).
//		return null;
//	}

	public void close() {
		if (database != null) {
			NotesThread.stermThread();
			database = null;
		}
	}
	
	public List<ICalendarEvent> getCalendarEntries(Date start) throws Exception {
		return getCalendarEntries(start, null);
	}
	
	public List<ICalendarEvent> getCalendarEntries(Date now, int weeks) throws Exception {
		return getCalendarEntries(now, CalendarUtil.createDateWeekOffset(now,50));
	}
	/**
	 * Retrieve a list of Lotus Notes calendar entries.
	 */

	public List<ICalendarEvent> getCalendarEntries(Date start, Date end) throws Exception {
		List<ICalendarEvent> calendarEntries = new ArrayList<ICalendarEvent>();
		Database db = getDatabase();
//		DocumentCollection entries = db	.search("@IsAvailable(StartDateTime) & (StartDateTime >= @Now)");
		Log.log("Get Notes calendar events");
		DocumentCollection entries = db	.search(getSearchString(start, end));
		int count = entries.getCount();
		// AAArrrrrrrgggghhhhhhhh
		// The index must start with 1!
		// It takes me 4 hours to figure it out
		for (int i = 1; i <= count; i++) {
			ICalendarEvent entry = createCalendarEntry(entries.getNthDocument(i));
			if (entry != null) {
				calendarEntries.add(entry);
			}
		}
		return calendarEntries;
	}
	

	public String getNotesVersion() {
		return notesVersion;
	}
	
	public  void saveCalendar(Date start, int weeks, String fileName) throws Exception {
		saveCalendar(start, CalendarUtil.createDateWeekOffset(start, weeks), fileName);
	}
	public  void saveCalendar(Date start,Date stop, String fileName) throws Exception {
		List<ICalendarEvent>  entries = getCalendarEntries(start,stop);
		VCalendar calendar = new VCalendar("Notes");
		for (ICalendarEvent calendarEntry : entries) {
			calendar.add(calendarEntry);
		}
		Log.log("Save Calendar " + fileName);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(fileName);
			calendar.printOn(out);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
	
	public void setMailFile(String mailfile) {
		this.mailfile = mailfile;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setServer(String server) {
		this.server = server;
	}

	@SuppressWarnings("unchecked")
	private ICalendarEvent createCalendarEntry(Document doc)
			throws NotesException {
		if (doc == null) {
			return null;
		}
		VCalendarEvent cal = new VCalendarEvent();
		Item lnItem = doc.getFirstItem("ApptUNID");
		if (!isItemEmpty(lnItem)) {
			cal.setID(lnItem.getText());
		}
		
		lnItem = doc.getFirstItem("AppointmentType");
		boolean isAllDayEvent = false;
		if (!isItemEmpty(lnItem)) {
			if (lnItem.getText().equals("2")) {
				isAllDayEvent = true;
			}
		}
		
		lnItem = doc.getFirstItem("Subject");
		if (!isItemEmpty(lnItem)) {
			cal.setTitle(lnItem.getText());
		}

		lnItem = doc.getFirstItem("Body");
		if (!isItemEmpty(lnItem)) {
			String desc = lnItem.getText();
			desc = removeLF(desc);
			cal.setDescription(desc);
		}


		lnItem = doc.getFirstItem("Location");
		if (!isItemEmpty(lnItem)) {
			 cal.setLocation(lnItem.getText());
		}
		lnItem = doc.getFirstItem("Room");
		if (!isItemEmpty(lnItem)) {
			String loc = cal.getLocation();
			cal.setLocation(loc + lnItem.getText());
		}
 
		// Get the type of Lotus calendar entry
		// lnItem = doc.getFirstItem("Form");
		// if (!isItemEmpty(lnItem))
		// cal.setEntryType(lnItem.getText());
		// else
		// // Assume we have an appointment
		// cal.setEntryType(NotesCalendarEntry.EntryType.APPOINTMENT);
		//
		// if (cal.getEntryType() == NotesCalendarEntry.EntryType.APPOINTMENT)
		// {
		// lnItem = doc.getFirstItem("AppointmentType");
		// if (!isItemEmpty(lnItem))
		// cal.setAppointmentType(lnItem.getText());
		// }


		// lnItem = doc.getFirstItem("$Alarm");
		// if (!isItemEmpty(lnItem)) {
		// cal.setAlarm(true);
		// lnItem = doc.getFirstItem("$AlarmOffset");
		// if (!isItemEmpty(lnItem))
		// cal.setAlarmOffsetMins(Integer.parseInt(lnItem.getText()));
		// }

		lnItem = doc.getFirstItem("OrgRepeat");
		// If true, this is a repeating calendar entry
		if (!isItemEmpty(lnItem)) {
			Vector<DateTime> lnStartDates = null;
			Vector<DateTime> lnEndDates = null;

			lnItem = doc.getFirstItem("StartDateTime");
			if (!isItemEmpty(lnItem)) {
				lnStartDates = lnItem.getValueDateTimeArray();
			}
			lnItem = doc.getFirstItem("EndDateTime");
			if (!isItemEmpty(lnItem)) {
				lnEndDates = lnItem.getValueDateTimeArray();
			}
			if (lnStartDates.size() != lnEndDates.size()) {
				System.err.println("lnStartDates.size() != lnEndDates.size()");
				return null;
			}
			
			for (int i = 0; i < lnStartDates.size(); i++) {
				DateTime startDate = lnStartDates.get(i);
				DateTime endDate = lnEndDates.get(i);
				cal.getTimes().add(createWhen(startDate,endDate,isAllDayEvent));
			}
		} else {
			lnItem = doc.getFirstItem("StartDateTime");
			When when = new When();
			if (!isItemEmpty(lnItem)) {
				DateTime start = lnItem.getDateTimeValue();
				when.setStartTime(createGoogleDateTime(start,isAllDayEvent));
			}

			// For To Do tasks, the EndDateTime doesn't exist, but there is an
			// EndDate value
			lnItem = doc.getFirstItem("EndDateTime");
			if (isItemEmpty(lnItem)) {
				lnItem = doc.getFirstItem("EndDate");
			}
			if (!isItemEmpty(lnItem)) {
				when.setEndTime(createGoogleDateTime(lnItem.getDateTimeValue(),isAllDayEvent));
			}
			cal.getTimes().add(when);
		}

//		System.out.println("--");
//		System.out.println(CalendarUtil.createVEvent(cal));
//		System.out.println("--");
		
		return cal;
	}

	private String removeLF(String desc) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < desc.length(); i++) {
			char c = desc.charAt(i);
			if (c != '\r') {
				buffer.append(c);
			}
		}
		return buffer.toString();
	}
	
	private com.google.gdata.data.DateTime createGoogleDateTime(DateTime dateTimeValue, boolean isAllDayEvent) throws NotesException {
		return CalendarUtil.createGoogleDateTime(dateTimeValue,isAllDayEvent);
	}

	private When createWhen(DateTime startDate, DateTime endDate, boolean dateOnly) throws NotesException {
		When when = new When();
		when.setStartTime(createGoogleDateTime(startDate,dateOnly));
		when.setEndTime(createGoogleDateTime(endDate,dateOnly));
		return when;
	}

	private void initNotes() throws Exception {
		
		try {
			this.getClass().getClassLoader().loadClass("lotus.domino.NotesThread");
		} catch (ClassNotFoundException ex) {
			String msg = "Lotus Notes Java interface file (Notes.jar) could not be found.\n" +
			"Make sure Notes.jar is in your classpath.";

			System.err.println(msg);
			throw new ClassNotFoundException(msg,ex);
		}
		try {
			NotesThread.sinitThread();
		} catch (Exception ex) {
			throw new Exception(
					"Lotus Notes native libraries could not be loaded.\n" +
					"Make sure that the path to Notes.jar is in your native library search path.",
					ex);
		}
	
	}

	/**
	 * Returns true if the Lotus Notes Item object is empty or null.
	 * 
	 * @param lnItem
	 *            The object to inspect.
	 */
	protected boolean isItemEmpty(Item lnItem) {
		try {
			// Lotus Notes Item objects are usually read by name, e.g. lnItem =
			// doc.getFirstItem("Subject").
			// If the name doesn't exist at all then null is returned.
			// If the name does exist, but doesn't have a value, then
			// lnItem.getText() returns "".
			// Check for both conditions.
			if (lnItem == null
					|| (lnItem != null && lnItem.getText().isEmpty()))
				return true;
		} catch (Exception ex) {
			// An error means we couldn't read the Item, so consider it empty
			return true;
		}

		return false;
	}

	
	Database getDatabase() throws Exception {
		if (database == null) {
			Log.log("Get Notes database "+ mailfile + " from " + server);
			initNotes();
			Session notesSession = NotesFactory
					.createSessionWithFullAccess(password);
			notesVersion = notesSession.getNotesVersion();
			database = notesSession.getDatabase(server, mailfile, false);
			if (database == null) {
				throw new Exception("Couldn't create Lotus Notes Database object.");
			}
			
		}
		return database;

	}

	String getSearchString(Date start, Date end) {

     TimeZone calTZ = TimeZone.getDefault();
     GregorianCalendar startDay = new GregorianCalendar(calTZ);
     startDay.setTime(start);
     GregorianCalendar endDay = null;
     if (end != null) {
    	 endDay = new GregorianCalendar(calTZ);
    	 endDay.setTime(end);
     }
     

     StringBuffer buffer = new StringBuffer("@IsAvailable(CalendarDateTime)");
     buffer.append(" & StartDateTime >= @Date(").append(startDay.get(1)).append("; ").append(startDay.get(2) + 1).append("; ").append(startDay.get(5)).append("; ").append(startDay.get(10)).append("; ").append(startDay.get(12)).append("; ").append(startDay.get(13)).append(")");
     if (endDay != null) {
    	 buffer.append(" & EndDateTime <= @Date(").append(endDay.get(1)).append("; ").append(endDay.get(2) + 1).append("; ").append(endDay.get(5)).append("; ").append(endDay.get(10)).append("; ").append(endDay.get(12)).append("; ").append(endDay.get(13)).append(")");
     }
//     searchFormulaStringBuffer.append(" & (AppointmentType = \"3\" | AppointmentType = \"0\")");
     return buffer.toString();
	}
}