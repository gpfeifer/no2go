package de.gpfeifer.no2go.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="when")
@XmlAccessorType(XmlAccessType.FIELD)
public class No2goWhen {

	static final SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	@XmlAttribute
	private boolean isAllDayEvent;

	@XmlAttribute
	private Date startTime;
	
	@XmlAttribute
	private Date endTime;
	
	public boolean isAllDayEvent() {
		return isAllDayEvent;
	}
	public void setAllDayEvent(boolean isAlldayEvent) {
		this.isAllDayEvent = isAlldayEvent;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result + (isAllDayEvent ? 1231 : 1237);
		result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		No2goWhen other = (No2goWhen) obj;
		if (endTime == null) {
			if (other.endTime != null) {
				return false;
			}
		} else if (!equals(endTime, other.endTime)) {
			return false;
		}
		if (isAllDayEvent != other.isAllDayEvent) {
			return false;
		}
		if (startTime == null) {
			if (other.startTime != null) {
				return false;
			}
		} else if (!equals(startTime, other.startTime)) {
			return false;
		}
		return true;
	}
	private boolean equals(Date d1, Date d2) {
		// d1.equals(d2) does not work 
		// (I guess because of different time zone)
        return dt.format(d1).equals(dt.format(d2));
	}


}
