package de.gpfeifer.calendar.google;

import java.util.List;

import net.fortuna.ical4j.model.component.VEvent;

import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.extensions.When;
import com.google.gdata.data.extensions.Where;

import de.gpfeifer.calendar.core.CalendarUtil;
import de.gpfeifer.calendar.core.ICalendarEvent;
import de.gpfeifer.calendar.core.VCalendarEvent;

public class GoogleCalendarEvent implements ICalendarEvent {
	public GoogleCalendarEvent(CalendarEventEntry event) {
		super();
		this.event = event;
	}

	private CalendarEventEntry event; 

	@Override
	public String getTitle() {
		return event.getTitle().getPlainText();
	}

	@Override
	public void setTitle(String title) {
		event.setTitle(TextConstruct.plainText(title));

	}

	@Override
	public String getDescription() {
		return event.getPlainTextContent();
	}

	@Override
	public void setDescription(String descr) {
		event.setContent(TextConstruct.plainText(descr));
	}

	@Override
	public String getLocation() {
		List<Where> locations = event.getLocations();
		StringBuffer buffer = new StringBuffer();
		for (Where where : locations) {
			buffer.append(where.getValueString()+ "\n");
		}
		return buffer.toString();
	}

	@Override
	public void setLocation(String location) {
		event.getLocations().clear();
		Where where = new Where();
		where.setValueString(location);
		event.getLocations().add(where);

	}

	@Override
	public boolean isRepeating() {
		return getTimes().size() > 1;
	}

	
	@Override
	public List<When> getTimes() {
//		if (event.getRecurrence() != null) {
//			String recString = event.getRecurrence().getValue();
//			DateList list = CalendarUtil.getWhenList(recString);
////			System.out.println(list);
//		}
		return event.getTimes();
	}

	@Override
	public String getID() {
		return event.getIcalUID();
	}

	@Override
	public void setID(String id) {
		event.setIcalUID(id);
	}

	@Override
	public VEvent asVEvent() {
		VCalendarEvent v = new VCalendarEvent();
		v.setID(getID());
		v.setTitle(getTitle());
		v.setDescription(getDescription());
		v.setLocation(getLocation());

		CalendarUtil.sort(getTimes());
		for (When when : getTimes()) {
			v.getTimes().add(when);
		}
		
		return v.asVEvent();
	}
	
	public CalendarEventEntry asGoogleEvent() {
		return event;
	}

	@Override
	public String toString() {

		return asVEvent().toString();
	}

}
