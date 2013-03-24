package de.gpfeifer.calendar.core;

import java.util.List;

import net.fortuna.ical4j.model.component.VEvent;

import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.extensions.When;

public interface ICalendarEvent {

	String getID();
	void setID(String id);
	
	String getTitle();
	void setTitle(String title);

	String getDescription();
	void setDescription(String descr);
	
	String getLocation();
	void setLocation(String location);
	
	boolean isRepeating();
	
	List<When> getTimes();
	
	VEvent asVEvent();
	
	CalendarEventEntry asGoogleEvent();
	
	
	


}
