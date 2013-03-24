package de.gpfeifer.calendar.core;

public class SynchCommand {
	public SynchCommand(CommandType type, ICalendarEvent event) {
		super();
		this.event = event;
		this.type = type;
	}
	public enum CommandType {
		INSERT,
		UPDATE;

	}
	private ICalendarEvent event;
	private CommandType type;
	public ICalendarEvent getEvent() {
		return event;
	}
	public CommandType getType() {
		return type;
	}
	
	@Override
	public String toString() {
		String result = "";
		switch (type) {
		case INSERT:
			result = "INSERT: ";
			break;
		case UPDATE:
			result = "UPDATE: ";
			break;

		default:
			break;
		}
		return result + event.getTitle();
	}

}
