package de.gpfeifer.no2go.synch;

import java.util.List;

import de.gpfeifer.no2go.core.No2goCalendarEvent;

public interface No2goSynchListener {

	void info(String info);

	void googleInsert(List<No2goCalendarEvent> insertList);

}
