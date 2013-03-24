package de.gpfeifer.calendar.core.test;

import java.util.Date;

import de.gpfeifer.calendar.core.CalendarUtil;
import junit.framework.TestCase;

public class TestCalendarUtil extends TestCase {
	
	public void testWeekOffset() {
		Date date = CalendarUtil.createDateWeekOffset(new Date(), 50);
		System.out.println(date);
	}

}
