package de.gpfeifer.calendar.core;

import java.util.List;

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;

import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.extensions.When;

public class VCalendarEvent implements ICalendarEvent {
	private WhenList whenList;

	public VCalendarEvent() {
		this(new VEvent());
	}
	public VCalendarEvent(VEvent event) {
		super();
		this.event = event;
		this.whenList = new WhenList(event);
	}

	VEvent event;
	
	
	@Override
	public String getTitle() {
		if(event.getSummary() == null) {
			return null;
		}
		return event.getSummary().getValue();
	}

	@Override
	public void setTitle(String title) {
		Summary summary = event.getSummary();
		if (summary == null) {
			event.getProperties().add(new Summary(title));
		} else {
			summary.setValue(title);
		}
	}

	@Override
	public String getDescription() {
		if(event.getDescription() == null) {
			return null;
		}
		return event.getDescription().getValue();
	}

	@Override
	public void setDescription(String descr) {
		Description descrpition = event.getDescription();
		if (descrpition == null) {
			event.getProperties().add(new Description(descr));
		} else {
			descrpition.setValue(descr);
		}

	}

	@Override
	public String getLocation() {
		Location loc = event.getLocation();
		return (loc == null) ? null : loc.getValue();
	}

	@Override
	public void setLocation(String location) {
		Location loc = event.getLocation();
		if (loc == null) {
			event.getProperties().add(new Location(location));
		} else {
			loc.setValue(location);
		}
	}

	@Override
	public void setID(String id) {
		Uid uid = event.getUid();
		if (uid == null) {
			event.getProperties().add(new Uid(id));
		} else {
			uid.setValue(id);
		}
			
	}
	

	public String getID() {
		Uid uid = event.getUid();
		return (uid == null) ? null : uid.getValue();
	}


	@Override
	public boolean isRepeating() {
		return getTimes().size() > 1;
	}

	
	public CalendarEventEntry asGoogleEvent() {
		throw new UnsupportedOperationException("asGoogleEvent");
	}

	@Override
	public List<When> getTimes() {
		return whenList;
	}
	
	@Override
	public String toString() {
		return "VCalendarEvent [event=" + event + "]";
	}
	
	@Override
	public VEvent asVEvent() {
		return event;
	}
	



}
