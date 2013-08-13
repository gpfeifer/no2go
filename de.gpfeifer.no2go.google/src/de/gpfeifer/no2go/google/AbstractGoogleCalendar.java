package de.gpfeifer.no2go.google;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;

import com.google.gdata.util.ServiceException;

import de.gpfeifer.no2go.core.No2goCalendarEvent;
import de.gpfeifer.no2go.core.No2goUtil;

public abstract class AbstractGoogleCalendar implements IGoogleCalendar {

	protected String googleAccountName;
	protected String googlePassword;

	public String getGoogleAccountName() {
		return googleAccountName;
	}

	public void setGoogleAccountName(String googleAccountName) {
		this.googleAccountName = googleAccountName;
	}

	public String getGooglePassword() {
		return googlePassword;
	}

	public void setGooglePassword(String googlePassword) {
		this.googlePassword = googlePassword;
	}

//	abstract public void insert(No2goCalendarEvent event) throws Exception;
	
	public List<No2goCalendarEvent> getCalendarEntries(Date now, int days) throws Exception {
		return getCalendarEvents(now, No2goUtil.createDateOffset(now, days));
	}

	abstract public List<No2goCalendarEvent> getCalendarEvents(Date now, Date createDateOffset) throws Exception;


}
