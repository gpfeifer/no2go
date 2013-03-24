package de.gpfeifer.calendar.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.gdata.data.DateTime;
import com.google.gdata.data.extensions.When;

import de.gpfeifer.calendar.core.SynchCommand.CommandType;

public class Synchronizer {
	

	
	
//	private List<SynchCommand> getSynchCommands(VCalendar target, VCalendar source) {
//		return getSynchCommands(target.getCalendarEvents(), source.getCalendarEvents());
//	}
	
	public List<SynchCommand> getSynchCommands(List<ICalendarEvent> target, List<ICalendarEvent> source) {
		List<SynchCommand> result = new ArrayList<SynchCommand>();
		for (ICalendarEvent event : source) {
			SynchCommand command = getSynchCommand(target,event);
			if (command != null) {
				result.add(command);
			}
		}
		return result;
	}


	private SynchCommand getSynchCommand(List<ICalendarEvent> targetList, ICalendarEvent sourceEvent) {
		for (ICalendarEvent targetEvent : targetList) {
			if (isEqual(targetEvent, sourceEvent)) {
				return null;
			}
			SynchCommand cmd = getSynchCommand(targetEvent,sourceEvent);
			if (cmd != null) {
				return cmd;
			}
		}
		return new SynchCommand(CommandType.INSERT, sourceEvent);
	}

	private SynchCommand getSynchCommand(ICalendarEvent targetEvent, ICalendarEvent sourceEvent) {
		// Assume that targetEvent and sourceEvent are not Equal
		
		// Only update recurring events
		if (!(targetEvent.isRepeating() && sourceEvent.isRepeating())) {
			return null;
		}
		// Title must be the same for update
		if (!targetEvent.getTitle().equals(sourceEvent.getTitle())) {
			return null;
		}
		if (!isWhenListCompatible(targetEvent.getTimes(),sourceEvent.getTimes())) {
			return null;			
		}
		List<When> sourceTimes = sourceEvent.getTimes();
		List<When> targetTimes = targetEvent.getTimes();
		for (When when : sourceTimes) {
			if (!containsTime(targetTimes, when)) {
				targetTimes.add(when);
			}
		}
		return new SynchCommand(CommandType.UPDATE,targetEvent);
	}

	private boolean containsTime(List<When> targetTimes, When when) {
		for (When time : targetTimes) {
			if (CalendarUtil.isEqual(time, when)) {
				return true;
			}
		}
		return false;
	}

	private boolean isWhenListCompatible(List<When> target, List<When> source) {
		DateTime s1 = target.get(0).getStartTime();
		DateTime s2 = target.get(0).getStartTime();
		if (s1.isDateOnly() && s2.isDateOnly()) {
			return true;
		}
		if (s1.isDateOnly() ||  s2.isDateOnly()) {
			return false;
		}
		return isTimeEquals(s1,s2);
	}

	private boolean isTimeEquals(DateTime s1, DateTime s2) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(s1.getValue());    
		int h1 = calendar.get(Calendar.HOUR_OF_DAY); 
		int m1 = calendar.get(Calendar.MINUTE);
		calendar.setTimeInMillis(s2.getValue());
		int h2 = calendar.get(Calendar.HOUR_OF_DAY); 
		int m2 = calendar.get(Calendar.MINUTE);
		return (h1 == h2) && (m1 == m2);
	}

	private boolean isEqual(ICalendarEvent e1, ICalendarEvent e2) {
		if (!e1.getTitle().equals(e2.getTitle())) {
			return false;
		}
		
		if (!isEqual(e1.getTimes(),e2.getTimes())) {
			return false;
		}
		return true;
	}

	private boolean isEqual(List<When> times1, List<When> times2) {
		if (times1.size() != times2.size()) {
			return false;
		}
		if (times1.size() > 1) {
			CalendarUtil.sort(times1);
			CalendarUtil.sort(times2);
		}
		for (int i = 0; i < times1.size(); i++) {
			When w1 = times1.get(i);
			When w2 = times2.get(i);
			
			if (!CalendarUtil.isEqual(w1, w2)) {
				return false;
			}
			
		}
		return true;
	}
}
