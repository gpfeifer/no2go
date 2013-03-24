package de.gpfeifer.no2go.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="no2go-calendar")
public class No2goCalendar {
	
	private static No2goCalendar read(Reader reader) throws JAXBException {
		// create JAXB context and instantiate marshaller
		JAXBContext context = JAXBContext.newInstance(No2goCalendar.class);
		Unmarshaller um = context.createUnmarshaller();
		return (No2goCalendar) um.unmarshal(reader);
	}

	public void printOn(OutputStream out) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(No2goCalendar.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
		marshaller.setProperty( Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.marshal(this, out);
	}

	
	public No2goCalendar() {
		super();
	}

	public No2goCalendar(List<No2goCalendarEvent> events) {
		this.events = events;
	}

	@XmlElementWrapper(name = "event-list")
	@XmlElement(name = "event")
	List<No2goCalendarEvent> events = new ArrayList<No2goCalendarEvent>();

	public List<No2goCalendarEvent> getCalendarEvents() {
		return events;
	}
	


	public void add(No2goCalendarEvent entry) {
		events.add(entry);
	}

	public static No2goCalendar read(String fileName) throws JAXBException, IOException {
		Reader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));
			return read(reader);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		 		
	}

	/**
	 * Return a list of No2goCalendarEvent where each No2goCalendarEvent 
	 * has only one when Event 
	 * @return
	 */
	public List<No2goCalendarEvent> getNormalizedCalendarEvents() {
		List<No2goCalendarEvent> result = new ArrayList<No2goCalendarEvent>();
		for (No2goCalendarEvent event : events) {
			result.addAll(event.normalize());
		}
		return result;
	}

	public boolean contains(No2goCalendarEvent event) {
		if (event.getWhenList().size() > 2) {
			throw new IllegalArgumentException("Event is not normalized");
		}
		for (No2goCalendarEvent calendarEvent : events) {
			if (calendarEvent.contains(event)) {
				return true;
			}
		}
		return false;
	}

	public void printOn(String string) throws JAXBException, IOException {
		printOn(new File(string));
	}

	public void printOn(File file) throws JAXBException, IOException {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			printOn(out);
		} finally {
			if (out != null) {
				out.close();
			}
		}
		
	}



}
