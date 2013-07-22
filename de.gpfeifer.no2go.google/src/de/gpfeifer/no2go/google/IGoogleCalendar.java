package de.gpfeifer.no2go.google;

import java.util.List;

import de.gpfeifer.no2go.core.No2goCalendarEvent;

public interface IGoogleCalendar {
	
	String getGoogleAccountName();
	
	void setGoogleAccountName(String googleAccountName);
	
	String getGooglePassword();
	
	void setGooglePassword(String googlePassword);

	List<No2goCalendarEvent> saveCalendar(int i, String string) throws Exception ;
	

}
