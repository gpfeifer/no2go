package de.gpfeifer.calendar.ui.actions;

import java.io.File;
import java.util.Date;

import org.eclipse.jface.action.Action;

import de.gpfeifer.calendar.core.CalendarUtil;
import de.gpfeifer.calendar.google.GoogleCalendar;

public class GoogleImportAction extends Action {

	@Override
	public void run() {
		String home = System.getProperty("user.home");
		String cpath = home + "/de.gpfeifer.calendar";
		new File(cpath).mkdirs();
		final String fileName = cpath+"/google.ics" ;
		try {
			new GoogleCalendar().saveCalendar(new Date(), getEnd(), fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private Date getEnd() {
		return CalendarUtil.createDateWeekOffset(new Date(), 50);
	}

	@Override
	public String getText() {
		return "Save Google Calendar";
	}

}
