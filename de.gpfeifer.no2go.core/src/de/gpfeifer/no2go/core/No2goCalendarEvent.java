package de.gpfeifer.no2go.core;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(name="event")
@XmlAccessorType(XmlAccessType.FIELD)
public class No2goCalendarEvent {
	@XmlAttribute
	private String title = "";
	@XmlAttribute
	private String description = "";
	@XmlAttribute
	private String location = "";
	@XmlAttribute
	private String eventId = "";

	@XmlElementWrapper(name = "when-list")
	@XmlElement(name = "when")
	private List<No2goWhen> whenList = new ArrayList<No2goWhen>();

	public No2goCalendarEvent() {
	}
	
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = trim(title);
	}




	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = trim(description);
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = trim(location);
	}

	public void setEventId(String id) {
		this.eventId = id;
	}
	
	public String getEventId() {
		return eventId;
	}

	
	public boolean isRepeating() {
		return false;
	}
	
	public List<No2goWhen> getWhenList() {
		return whenList;
	}

	public void setWhenList(List<No2goWhen> whenList) {
		this.whenList = whenList;
	}


	No2goCalendarEvent copy() {
		No2goCalendarEvent result = new No2goCalendarEvent();
		result.setTitle(this.getTitle());
		result.setDescription(this.getDescription());
		result.setEventId(this.getEventId());
		result.setLocation(this.getLocation());
		ArrayList<No2goWhen> whenListCopy = new ArrayList<No2goWhen>();
		whenListCopy.addAll(this.getWhenList());
		result.setWhenList(whenListCopy);
		return result;
	}

	List<No2goCalendarEvent> normalize() {
		List<No2goCalendarEvent> result = new ArrayList<No2goCalendarEvent>();
		for (No2goWhen when : getWhenList()) {
			No2goCalendarEvent e = copy();
			e.getWhenList().clear();
			e.getWhenList().add(when);
			result.add(e);
		}
		return result;
	}

	private String trim(String str) {
		if (str == null) {
			return "";
		}
		String result = str.trim();
		if (result.endsWith("\n")) {
			result = result.substring(0, result.length() - 2);
		}
		return result;
	}


	public boolean contains(No2goCalendarEvent event) {
		if (event.getWhenList().size() != 1) {
			throw new IllegalArgumentException("Event is not normalized");
		}
		if (!title.equals(event.getTitle())) {
			return false;
		}
		if (!description.equals(event.getDescription())) {
			return false;
		}
		if (!location.equals(event.getLocation())) {
			return false;
		}
		No2goWhen no2goWhen = event.getWhenList().get(0);
		for (No2goWhen when : getWhenList()) {
			if (no2goWhen.equals(when)) {
				return true;
			}
		}

		return false;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "No2goCalendarEvent [title=" + title + "]";
	}

}
