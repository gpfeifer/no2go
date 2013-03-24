package de.gpfeifer.calendar.ui.actions;

import java.io.File;
import java.util.Date;

import org.eclipse.jface.action.Action;

import de.gpfeifer.calendar.core.CalendarUtil;
import de.gpfeifer.calendar.ui.Activator;
import de.gpfeifer.calendar.ui.NotesApplication;
import de.gpfeifer.calendar.ui.preferences.PreferenceConstants;
import de.gpfeifer.calendar.ui.preferences.SecurePreferenceStore;

public class NotesImportAction extends Action {

	@Override
	public void run() {
		Date start = new Date();
		Date stop = CalendarUtil.createDateWeekOffset(start, 50);
		String home = System.getProperty("user.home");
		String cpath = home + "/de.gpfeifer.calendar";
		new File(cpath).mkdirs();
		final String fileName = cpath + "/notes.ics";

		SecurePreferenceStore store = Activator.getDefault().getSecurePreferenceStore();
		final String path = store.getString(PreferenceConstants.P_NOTES_PATH);
		final String server = store.getString(PreferenceConstants.P_NOTES_SERVER);
		final String mail = store.getString(PreferenceConstants.P_NOTES_MAIL);
		final String password = store.getString(PreferenceConstants.P_NOTES_PWD);
		NotesApplication.start(path, server, mail, password, fileName, start, stop);
		
	}

	@Override
	public String getText() {
		return "Save Notes Calendar";
	}

}
