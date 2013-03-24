package de.gpfeifer.calendar.core;

import java.util.ArrayList;
import java.util.List;

import com.google.gdata.data.DateTime;
import com.google.gdata.data.extensions.When;

import de.gpfeifer.calendar.core.SynchCommand.CommandType;

public class SimpleSynchronizer {

	public List<SynchCommand> getSynchCommands(List<ICalendarEvent> target, List<ICalendarEvent> source) {
		List<ICalendarEvent> googleNormalized = normalize(target);
		List<ICalendarEvent> lotusNormalized = normalize(source);
		List<SynchCommand> result = new ArrayList<SynchCommand>();
		for (ICalendarEvent event : lotusNormalized) {
			SynchCommand command = getSynchCommand(googleNormalized, event);
			if (command != null) {
				result.add(command);
			}
		}
		return result;
	}

	private List<ICalendarEvent> normalize(List<ICalendarEvent> source) {
		List<ICalendarEvent> result = new ArrayList<ICalendarEvent>();
		for (ICalendarEvent event : source) {
			if (event.isRepeating()) {
				result.addAll(normalize(event));
			} else {
				result.add(event);
			}
		}
		return result;
	}

	private List<ICalendarEvent> normalize(ICalendarEvent event) {
		List<ICalendarEvent> result = new ArrayList<ICalendarEvent>();
		if (event.isRepeating()) {
			List<When> times = event.getTimes();
			for (When when : times) {
				DateTime start = when.getStartTime();
				if ( start.compareTo(DateTime.now()) >= 0 ) {
					VCalendarEvent vevent = new VCalendarEvent();
					vevent.setDescription(event.getDescription());
					vevent.setLocation(event.getLocation());
					vevent.setTitle(event.getTitle());
					vevent.getTimes().add(when);
					result.add(vevent);
				}
			}
		} else {
			result.add(event);
		}
		return result;
	}

	private SynchCommand getSynchCommand(List<ICalendarEvent> googleList, ICalendarEvent lotusEvent) {
		for (ICalendarEvent googleEvent : googleList) {
			if (isEqual(googleEvent, lotusEvent)) {
				return null;
			}
		}
		return new SynchCommand(CommandType.INSERT, lotusEvent);
	}

	private boolean isEqual(ICalendarEvent e1, ICalendarEvent e2) {
		if (!e1.getTitle().trim().equalsIgnoreCase(e2.getTitle().trim())) {
			return false;
		}

		if (!isEqual(e1.getTimes(), e2.getTimes())) {
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
