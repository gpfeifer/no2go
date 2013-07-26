package de.gpfeifer.no2go.synch;

import de.gpfeifer.no2go.core.No2goCalendar;

public interface No2goSynch {

	void synch() throws Exception;
	
	void addListener(No2goSynchListener listener);
	
	void removeListener(No2goSynchListener listener);

	CalendarDiff diff(No2goCalendar notes, No2goCalendar google);

	int delete() throws Exception;
	
}
