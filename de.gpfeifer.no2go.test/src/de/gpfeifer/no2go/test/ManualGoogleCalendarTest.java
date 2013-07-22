package de.gpfeifer.no2go.test;

import java.util.Date;
import java.util.List;

import de.gpfeifer.no2go.core.No2goCalendarEvent;
import de.gpfeifer.no2go.google3.GoogleCalendarV3;

public class ManualGoogleCalendarTest {

	public static void main(String[] args) {
		GoogleCalendarV3 v3 = new GoogleCalendarV3();
		try {
			List<No2goCalendarEvent> list = v3.getCalendarEntries(new Date(), 10);
			for (No2goCalendarEvent e : list) {
				System.out.println(e);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
