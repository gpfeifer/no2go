package de.gpfeifer.no2go.synch;

import com.google.api.services.calendar.model.Event;

public interface No2goSynchListener {

	void info(String info);

	void insert(Event event);
	
	void update(Event event);
	
	void delete(Event event);

	void unchanged(Event googleEvent);

	void synchBegin();
	void synchEnd();

//	void googleInsert(List<No2goCalendarEvent> insertList);

}
