package de.gpfeifer.calendar.core;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lotus.domino.NotesException;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.RRule;

import com.google.gdata.data.extensions.When;

public class CalendarUtil {
	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");

	public static void printOn(ICalendarEvent entry, PrintStream out) {
		out.println(entry.getTitle() + " " + entry.getTimes());
	}

	public static ICalendarEvent createICalendarEntry(VEvent event) {
		return new VCalendarEvent(event);
	}

	static String getUTCString(com.google.gdata.data.DateTime dt) {
		Integer tz = dt.getTzShift();
		long value = dt.getValue() - (tz * 60 * 1000);
		java.util.Date date = new java.util.Date(value);
		return dateFormat.format(date);
	}

	public static When createWhen(net.fortuna.ical4j.model.property.DateProperty start, net.fortuna.ical4j.model.property.DateProperty end) {

		When when = new When();
		when.setStartTime(createGoogleDateTime(start));
		when.setEndTime(createGoogleDateTime(end));
		return when;
	}

	public static com.google.gdata.data.DateTime createGoogleDateTime(lotus.domino.DateTime date, boolean dateOnly) throws NotesException {
		java.util.Date javaDate = date.toJavaDate();
		com.google.gdata.data.DateTime result = new com.google.gdata.data.DateTime(javaDate.getTime());
		result.setDateOnly(dateOnly);
		return result;
	}

	public static com.google.gdata.data.DateTime createGoogleDateTime(net.fortuna.ical4j.model.property.DateProperty date) {
		Date d = date.getDate();
		boolean isDateOnly = !(d instanceof net.fortuna.ical4j.model.DateTime);
		return createGoogleDateTime(d, date.getTimeZone(), date.isUtc(), isDateOnly);
	}

	// public static net.fortuna.ical4j.model.DateTime
	// createICalDateTime(com.google.gdata.data.DateTime dt) {
	public static net.fortuna.ical4j.model.Date createICalDateTime(com.google.gdata.data.DateTime dt) {
		dt.isDateOnly();
		// Integer tz = dt.getTzShift();
		// long value = (tz == null) ? dt.getValue() : dt.getValue() + ((long)tz
		// * (long)60 * (long)1000);
		// dt.getValue() scheint timezone zu berücksichtigen
		long value = dt.getValue();
		if (dt.isDateOnly()) {
			return new net.fortuna.ical4j.model.Date(value);
		} else {
			net.fortuna.ical4j.model.DateTime result = new net.fortuna.ical4j.model.DateTime();
			result.setUtc(true);
			result.setTime(value);
			return result;
		}
	}

	public static com.google.gdata.data.DateTime createGoogleDateTime(net.fortuna.ical4j.model.DateTime dt) {
		java.util.Date date = new java.util.Date(dt.getTime());
		return createGoogleDateTime(date, dt.getTimeZone(), dt.isUtc(), false);
	}

	public static com.google.gdata.data.DateTime createGoogleDateTime(java.util.Date date, net.fortuna.ical4j.model.TimeZone timeZone, boolean isUTC,
			boolean isDateOnly) {
		com.google.gdata.data.DateTime result;
		if (isUTC) {
			result = new com.google.gdata.data.DateTime(date, java.util.TimeZone.getDefault());
		} else {
			if (timeZone == null) {
				result = new com.google.gdata.data.DateTime(date);
			} else {
				long value = date.getTime();
				int offset = timeZone.getOffset(date.getTime());
				result = new com.google.gdata.data.DateTime(value + offset);
			}
		}
		result.setDateOnly(isDateOnly);
		return result;
	}

	public static When createWhen(net.fortuna.ical4j.model.DateTime s, net.fortuna.ical4j.model.DateTime e) {
		When when = new When();
		when.setStartTime(createGoogleDateTime(s));
		when.setEndTime(createGoogleDateTime(e));
		return when;
	}

	public static java.util.Date createDateWeekOffset(java.util.Date date, int week) {
		long v = date.getTime();
		long r = v + ((long) week * 7l * 24l * 60l * 60l * 1000l);
		return new java.util.Date(r);
	}

	public static List<When> sort(List<When> list) {
		Collections.sort(list, new Comparator<When>() {

			@Override
			public int compare(When o1, When o2) {
				long s1 = o1.getStartTime().getValue();
				long s2 = o2.getStartTime().getValue();
				if (s1 == s2) {
					return 0;
				}

				return s1 < s2 ? -1 : 1;
			}
		});
		return list;
	}

	public static boolean isEqual(When w1, When w2) {
		if (!(w1.getStartTime().getValue() == w2.getStartTime().getValue())) {
			if (w1.equals(w2)) {
				return true;
			}
			return false;
		}
		if (!(w1.getEndTime().getValue() == w2.getEndTime().getValue())) {
			return false;
		}

		return true;
	}

	public static String getRecurrenceData(ICalendarEvent entry) {
		StringBuffer buffer = new StringBuffer();
		VEvent event = entry.asVEvent();

		buffer.append(event.getStartDate().toString());
		buffer.append("\r\n");
		buffer.append(event.getEndDate().toString());
		buffer.append("\r\n");
		Property prop = event.getProperty(Property.RDATE);
		buffer.append(prop.toString());
		buffer.append("\r\n");

		return buffer.toString();
	}
	static public String getRecurrenceData2(ICalendarEvent entry) {
		StringBuffer buffer = new StringBuffer();
		VEvent event = entry.asVEvent();

		buffer.append(event.getStartDate().toString());
		buffer.append("\r\n");
		buffer.append(event.getEndDate().toString());
		buffer.append("\r\n");

		List<When> times = entry.getTimes();
		if (times.size() < 2) {
			return null;
		}
		sort(times);

		Integer dist = getDistance(times);
//		When last = times.get(times.size() - 1);

//		net.fortuna.ical4j.model.Date lastDate = new net.fortuna.ical4j.model.Date(last.getEndTime().getValue());
		if (dist == null) {
			return null;
		}
		if (dist % 7 == 0) {

			Recur recur = null;
			recur = new Recur(Recur.WEEKLY, times.size());
			if (dist / 7 > 1) {
				recur.setInterval(dist / 7);
			}
			RRule rrule = new RRule(recur);
			buffer.append(rrule.toString());

		}
		return buffer.toString();
	}

	private static Integer getDistance(List<When> times) {
		int size = times.size();
		Integer result = null;

		for (int i = 1; i < size; i++) {
			When one = times.get(i - 1);
			When two = times.get(i);
			Integer dist = getDistance(one, two);
			if (dist != null) {
				if (result == null) {
					result = dist;
				} else {
					if (dist.intValue() != result.intValue()) {
						return null;
					}
				}
			} else {
//				System.err.println("ff");
			}
		}
		return result;
	}

	private static Integer getDistance(When one, When two) {
		com.google.gdata.data.DateTime s1 = one.getStartTime();
		com.google.gdata.data.DateTime s2 = two.getStartTime();
		long ms = s2.getValue() - s1.getValue();
		// One day in ms
		long div = 1000l * 60l * 60l * 24l;
		long mod = ms % div;
		// Wg Sommerzeit ist die Mod hier nicht unbedingt gleich 0
		if (mod != 0) {
			return null;
		}
		long days = ms / div;
		return new Integer((int) days);
	}

}
