package de.gpfeifer.no2go.notes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.DateTime;
import lotus.domino.Directory;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.NotesThread;
import lotus.domino.Session;
import de.gpfeifer.no2go.core.No2goAttendee;
import de.gpfeifer.no2go.core.No2goCalendar;
import de.gpfeifer.no2go.core.No2goCalendarEvent;
import de.gpfeifer.no2go.core.No2goUtil;
import de.gpfeifer.no2go.core.No2goWhen;

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
			String line = null;
			try {
				while ((line = br.readLine()) != null) {
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

//	private Database database;

	private String mailfile;
	private String password;
	private String server;
	

	String notesVersion;

	EmailValidator emailValidator = new EmailValidator(); 
	public NotesCalendar() {
		notesVersion = "unknown";
	}

	// private SNGCalendarEntry createSNGCalendarEntry(Document doc, Date now) {
	// VEvent event = new VEvent();
	//
	// Item lnItem = doc.getFirstItem("Subject");
	// if (!isItemEmpty(lnItem)) {
	// event.getProperties().addcal.title = lnItem.getText();
	// }
	//
	// lnItem = doc.getFirstItem("Body");
	// if (!isItemEmpty(lnItem)) {
	// cal.description = lnItem.getText();
	// }
	//
	// cal.location = "";
	// lnItem = doc.getFirstItem("Location");
	// if (!isItemEmpty(lnItem)) {
	// cal.location = lnItem.getText();
	// }
	// lnItem = doc.getFirstItem("Room");
	// if (!isItemEmpty(lnItem)) {
	// cal.location += " - " + lnItem.getText();
	// }
	//
	// // Get the type of Lotus calendar entry
	// // lnItem = doc.getFirstItem("Form");
	// // if (!isItemEmpty(lnItem))
	// // cal.setEntryType(lnItem.getText());
	// // else
	// // // Assume we have an appointment
	// // cal.setEntryType(NotesCalendarEntry.EntryType.APPOINTMENT);
	// //
	// // if (cal.getEntryType() == NotesCalendarEntry.EntryType.APPOINTMENT)
	// // {
	// // lnItem = doc.getFirstItem("AppointmentType");
	// // if (!isItemEmpty(lnItem))
	// // cal.setAppointmentType(lnItem.getText());
	// // }
	//
	//
	// // lnItem = doc.getFirstItem("$Alarm");
	// // if (!isItemEmpty(lnItem)) {
	// // cal.setAlarm(true);
	// // lnItem = doc.getFirstItem("$AlarmOffset");
	// // if (!isItemEmpty(lnItem))
	// // cal.setAlarmOffsetMins(Integer.parseInt(lnItem.getText()));
	// // }
	//
	// lnItem = doc.getFirstItem("OrgRepeat");
	// // If true, this is a repeating calendar entry
	// if (!isItemEmpty(lnItem)) {
	// Vector<DateTime> lnStartDates = null;
	// Vector<DateTime> lnEndDates = null;
	//
	// lnItem = doc.getFirstItem("StartDateTime");
	// if (!isItemEmpty(lnItem)) {
	// lnStartDates = lnItem.getValueDateTimeArray();
	// Date[] startDates = new Date[lnStartDates.size()];
	// for (int i = 0; i < startDates.length; i++) {
	// startDates[i] = lnStartDates.get(i).toJavaDate();
	// }
	// cal.setStartDates(startDates);
	// }
	// lnItem = doc.getFirstItem("EndDateTime");
	// if (!isItemEmpty(lnItem)) {
	// lnEndDates = lnItem.getValueDateTimeArray();
	// Date[] endDates = new Date[lnEndDates.size()];
	// for (int i = 0; i < endDates.length; i++) {
	// endDates[i] = lnEndDates.get(i).toJavaDate();
	// }
	// cal.setEndDates(endDates);
	// }
	//
	// } else {
	// lnItem = doc.getFirstItem("StartDateTime");
	// if (!isItemEmpty(lnItem)) {
	// Date start = lnItem.getDateTimeValue().toJavaDate();
	// if (now.after(start)) {
	// return null;
	// }
	// cal.setStartDate(lnItem.getDateTimeValue().toJavaDate());
	// }
	//
	// // For To Do tasks, the EndDateTime doesn't exist, but there is an
	// // EndDate value
	// lnItem = doc.getFirstItem("EndDateTime");
	// if (isItemEmpty(lnItem)) {
	// lnItem = doc.getFirstItem("EndDate");
	// }
	// if (!isItemEmpty(lnItem)) {
	// cal.setEndDate(lnItem.getDateTimeValue().toJavaDate());
	// }
	//
	// }
	//
	//
	// event.getProperty(Property.SUMMARY).
	// return null;
	// }

//	public void close() {
//		if (database != null) {
//			NotesThread.stermThread();
//			database = null;
//		}
//	}

	private List<No2goCalendarEvent> getCalendarEntries(Date start) throws Exception {
		return getCalendarEntries(start, null);
	}

	public List<No2goCalendarEvent> getCalendarEntries(Date now, int days) throws Exception {
		return getCalendarEntries(now, No2goUtil.createDateOffset(now, days));
	}

	public List<No2goCalendarEvent> getCalendarEntries(Date start, Date end) throws Exception {
		try {
			this.getClass().getClassLoader().loadClass("lotus.domino.NotesThread");
		} catch (ClassNotFoundException ex) {
			String msg = "Lotus Notes Java interface file (Notes.jar) could not be found.\n" + "Make sure Notes.jar is in your classpath.";

			System.err.println(msg);
			throw new ClassNotFoundException(msg, ex);
		}

		try {
			NotesThread.sinitThread();
			Session session = getNotesSession();
			Database db = getDatabase(session);
			return getCalendarEntries(session, db, start, end);
		} finally {
			NotesThread.stermThread();
		}

	}
	/**
	 * Retrieve a list of Lotus Notes calendar entries.
	 * @param session 
	 */

	private List<No2goCalendarEvent> getCalendarEntries(Session session, Database db, Date start, Date end) throws Exception {
		List<No2goCalendarEvent> calendarEntries = new ArrayList<No2goCalendarEvent>();
		
		// DocumentCollection entries = db
		// .search("@IsAvailable(StartDateTime) & (StartDateTime >= @Now)");
//		Calendar calc = Calendar.getInstance();
//		calc.setTime(start);
//		calc.add(Calendar.DAY_OF_MONTH, -1);
//		Date newStart = calc.getTime();
		DocumentCollection entries = db.search(getSearchString(start , end));
		int count = entries.getCount();
		Directory directory = session.getDirectory();
		// AAArrrrrrrgggghhhhhhhh
		// The index must start with 1!
		// It takes me 4 hours to figure it out
		for (int i = 1; i <= count; i++) {
			No2goCalendarEvent entry = createCalendarEntry(directory, entries.getNthDocument(i), start, end);
			if (entry != null) {
				calendarEntries.add(entry);
			}
		}
		return calendarEntries;
	}

	private String getNotesVersion() {
		return notesVersion;
	}

	public void saveCalendar(Date start, int days, String fileName) throws Exception {
		saveCalendar(start, No2goUtil.createDateOffset(start, days), fileName);
	}

	public void saveCalendar(Date start, Date stop, String fileName) throws Exception {
		new File(fileName).delete();
		List<No2goCalendarEvent> entries = getCalendarEntries(start, stop);
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
	private No2goCalendarEvent createCalendarEntry(Directory directory, Document doc, Date start, Date end) throws NotesException {
		if (doc == null) {
			return null;
		}
		No2goCalendarEvent cal = new No2goCalendarEvent();
		Item lnItem = doc.getFirstItem("ApptUNID");
		if (!isItemEmpty(lnItem)) {
			cal.setNotesId(lnItem.getText());
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

		lnItem = doc.getFirstItem("Chair");
		if (!isItemEmpty(lnItem)) {
			String chair = lnItem.getText();
			String email = getEMail(directory,chair);
			No2goAttendee a = new No2goAttendee();
			a.setDisplayName(getAttendeeName(chair + " (Chair)"));
			a.setEmail(email);
			cal.getAttendees().add(a);
		}

		lnItem = doc.getFirstItem("SendTo");
		if (!isItemEmpty(lnItem)) {
			String attendees = lnItem.getText();
			String[] alist = attendees.split(";");
			for (String name : alist) {
				String email = getEMail(directory,name);
				No2goAttendee a = new No2goAttendee();
				a.setDisplayName(getAttendeeName(name));
				a.setEmail(email);
				cal.getAttendees().add(a);
			}
		}

		lnItem = doc.getFirstItem("CopyTo");
		if (!isItemEmpty(lnItem)) {
			String attendees = lnItem.getText();
			String[] alist = attendees.split(";");
			for (String name : alist) {
				String email = getEMail(directory,name);
				No2goAttendee a = new No2goAttendee();
				a.setDisplayName(getAttendeeName(name) + " (optional)");
				a.setEmail(email);
				cal.getAttendees().add(a);
			}
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

//		Item repeatItem = doc.getFirstItem("RepeatUnit");
//		
//		if (!isItemEmpty(repeatItem)) {
//			String text = repeatItem.getText();
//			System.out.println(text);
//			Item repeatWeekensItem = doc.getFirstItem("RepeatWeekends");
//			if (!isItemEmpty(repeatWeekensItem)) {
//				String text2 = repeatWeekensItem.getText();
//				System.out.println(text2);
//			}
//
//			
//			
//		}
//		Item startItem = doc.getFirstItem("RepeatStartDate");
//		if (!isItemEmpty(startItem)) {
//			
//			System.out.println(startItem);
//			Vector itemValueDateTimeArray = doc.getItemValue("RepeatDates");
//			System.out.println(itemValueDateTimeArray);
//			doc.
//		}

		
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
				Date javaStartDate = startDate.toJavaDate();
				if (javaStartDate.compareTo(start) >= 0 && javaStartDate.compareTo(end) <= 0) {
					DateTime endDate = lnEndDates.get(i);
					cal.getWhenList().add(createWhen(startDate, endDate, isAllDayEvent));
					
				}

//				if (startDate.toJavaDate().getTime() < end.getTime()
//						&& startDate.toJavaDate().getTime() > start.getTime()
//					
//					) {
//					DateTime endDate = lnEndDates.get(i);
//					cal.getWhenList().add(createWhen(startDate, endDate, isAllDayEvent));
//				}
			}
		} else {
			lnItem = doc.getFirstItem("StartDateTime");
			No2goWhen when = new No2goWhen();
			when.setAllDayEvent(isAllDayEvent);
			if (!isItemEmpty(lnItem)) {
				DateTime startTime = lnItem.getDateTimeValue();
				when.setStartTime(startTime.toJavaDate());
			}

			// For To Do tasks, the EndDateTime doesn't exist, but there is an
			// EndDate value
			lnItem = doc.getFirstItem("EndDateTime");
			if (isItemEmpty(lnItem)) {
				lnItem = doc.getFirstItem("EndDate");
			}
			if (!isItemEmpty(lnItem)) {
				when.setEndTime(lnItem.getDateTimeValue().toJavaDate());
				if (!when.getEndTime().before(yesterday())) {
					cal.getWhenList().add(when);
				}
			}
		}

		if (cal.getWhenList().size() < 1) {
			// If a repeating event has no entry between start and end the whenList is empty
			return null;
		}
		return cal;
	}

	private Date yesterday() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return calendar.getTime();
	}

	private String getAttendeeName(String name) {
		if (name.startsWith("CN=")) {
			String result = name.substring(3);
			int index = result.indexOf("/");
			if (index > 0) {
				result = result.substring(0, index);
						
			}
			return result;
		}
		return name;
	}

	/**
	 * Get an eMail
	 * 
	 * @param directory
	 * @param name
	 * @return
	 */
	private String getEMail(Directory directory, String name) {
		if (emailValidator.validate(name)) {
			return name;
		}

		try {
			String str = name;
			int index = name.indexOf("@");
			if (index > 0) {
				str = name.substring(0,index);
			}
			@SuppressWarnings("rawtypes")
			Vector mailInfo = directory.getMailInfo(str);
			for (Object object : mailInfo) {
				if (object instanceof String) {
					str = (String) object;
					if (emailValidator.validate(str)) {
						return str;
					}
				}
				
			}
		} catch (NotesException e) {
		}
		return "no.email@null.com";
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

	private No2goWhen createWhen(DateTime startDate, DateTime endDate, boolean isAllDayEvent) throws NotesException {
		No2goWhen when = new No2goWhen();
		when.setStartTime(startDate.toJavaDate());
		if (endDate != null) {
			when.setEndTime(endDate.toJavaDate());
		}
		when.setAllDayEvent(isAllDayEvent);
		return when;
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
			if (lnItem == null || (lnItem != null && lnItem.getText().isEmpty()))
				return true;
		} catch (Exception ex) {
			// An error means we couldn't read the Item, so consider it empty
			return true;
		}

		return false;
	}

	Session getNotesSession() throws NotesException {
		Session notesSession = NotesFactory.createSessionWithFullAccess(password);
		if (notesSession == null) {
			throw new RuntimeException("Could not create Lotus Notes Session.\nServer: " + server + "\nDatabase: " + mailfile);
		}
		return notesSession;
	}

	Database getDatabase(Session notesSession) throws NotesException {
		Database database = notesSession.getDatabase(server, mailfile, false);
		if (database == null) {
			throw new RuntimeException("Could not connect to Lotus Notes Database.\nServer: " + server + "\nDatabase: " + mailfile);
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
		buffer.append(" & StartDateTime >= @Date(").append(getNotesDate(startDay)).append(")");
		if (endDay != null) {
			buffer.append(" & EndDateTime <= @Date(").append(getNotesDate(endDay)).append(")");
		}
		// searchFormulaStringBuffer.append(" & (AppointmentType = \"3\" | AppointmentType = \"0\")");
		return buffer.toString();
	}
	
	String getNotesDate(GregorianCalendar d) {
		StringBuffer buffer = new StringBuffer("");
		buffer
			.append(d.get(1))
			.append("; ")
			.append(d.get(2) + 1)
			.append("; ")
			.append(d.get(5));

//			.append("; ")
//			.append(d.get(10))
//			.append("; ")
//			.append(d.get(12))
//			.append("; ")
//			.append(d.get(13));
		return buffer.toString();
	}
}