package de.gpfeifer.calendar.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;

public class VCalendar {
	
	public static VCalendar read(String name) throws IOException, ParserException {
		
		InputStreamReader reader = new InputStreamReader(new FileInputStream(name),Charset.forName("UTF-8"));

		CalendarBuilder builder = new CalendarBuilder();
		
		Calendar calendar = builder.build(reader);
		reader.close();
		return new VCalendar(calendar);
		
	}
	
	Calendar calendar;
	UidGenerator uidGenerator = new UidGenerator(null,"1");; 

	
	public VCalendar(String name) {
		super();
		init(name);
	}

	public VCalendar(Calendar cal) {
		this.calendar = cal;
	}

	private void init(String name) {
		calendar =  new Calendar();
		calendar.getProperties().add(new ProdId("-//" + name));
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(CalScale.GREGORIAN);
	}
	
	public List<ICalendarEvent> getCalendarEvents() {
		List<ICalendarEvent> result = new ArrayList<ICalendarEvent>();
		ComponentList components = calendar.getComponents();
		
		for (int i = 0; i < components.size(); i++) {
			Object comp = components.get(i);
			if (comp instanceof VEvent) {
				VEvent event = (VEvent) comp;
				result.add(new VCalendarEvent(event));
				
			}
		}
		return result;
	}
	


	public void add(ICalendarEvent entry) {
		calendar.getComponents().add(createVEvent(entry));
	}


	private VEvent createVEvent(ICalendarEvent entry) {
		return entry.asVEvent();
	}
	
	public void printOn(OutputStream out) throws IOException, ValidationException {
		CalendarOutputter putter = new CalendarOutputter(true);
		putter.output(calendar, out);
	}

}
