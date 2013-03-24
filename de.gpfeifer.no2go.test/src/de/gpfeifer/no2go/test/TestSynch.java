package de.gpfeifer.no2go.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import de.gpfeifer.no2go.core.No2goCalendar;
import de.gpfeifer.no2go.google.GoogleCalendar;
import de.gpfeifer.no2go.notes.NotesProcess;
import de.gpfeifer.no2go.securestore.SecurePreferenceStore;
import de.gpfeifer.no2go.securestore.SecurePreferenceStoreConstants;
import de.gpfeifer.no2go.synch.CalendarDiff;
import de.gpfeifer.no2go.synch.No2goSynchImpl;

public class TestSynch {


	@Test
	public void test() throws JAXBException, IOException {
		No2goCalendar notes = No2goCalendar.read("data/notes.xml");
		No2goCalendar google = No2goCalendar.read("data/google.xml");
	
		CalendarDiff diff = new No2goSynchImpl().diff(notes,  google);
		new No2goCalendar(diff.insertList).printOn("data/diff.xml");
		assertEquals(1, diff.insertList.size());
	}

	@Test
	public void testSynch2() throws Exception {
		int days = 10;
		SecurePreferenceStore store = SecurePreferenceStore.get();
		String account = store.getString(SecurePreferenceStoreConstants.P_GOOGLE_ACCOUNT);
		String gpwd = store.getString(SecurePreferenceStoreConstants.P_GOOGLE_PWD);
		GoogleCalendar calendar = new GoogleCalendar();
		calendar.setGoogleAccountName(account);
		calendar.setGooglePassword(gpwd);
		calendar.saveCalendar(days, "data/google2.xml");
		
		String path = store.getString(SecurePreferenceStoreConstants.P_NOTES_PATH);
		String server = store.getString(SecurePreferenceStoreConstants.P_NOTES_SERVER);
		String mail = store.getString(SecurePreferenceStoreConstants.P_NOTES_MAIL);
		String pwd = store.getString(SecurePreferenceStoreConstants.P_NOTES_PWD);
		
		String result = NotesProcess.save(path,"localhost",mail,pwd, "" + days, "data/notes2.xml");
//		new NotesProcess().run("save", server,mail,pwd, "" + days, "data/notes2.xml");
		System.out.println(result);
		No2goCalendar notes = No2goCalendar.read("data/notes2.xml");
		No2goCalendar google = No2goCalendar.read("data/google2.xml");
	
		CalendarDiff diff = new No2goSynchImpl().diff(notes,  google);
		new No2goCalendar(diff.insertList).printOn("data/diff2.xml");


	}

}
