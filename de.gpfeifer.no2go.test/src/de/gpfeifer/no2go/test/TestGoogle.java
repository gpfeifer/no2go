package de.gpfeifer.no2go.test;

import org.junit.Test;

import de.gpfeifer.no2go.google.GoogleCalendar;
import de.gpfeifer.no2go.securestore.SecurePreferenceStore;
import de.gpfeifer.no2go.securestore.SecurePreferenceStoreConstants;

public class TestGoogle {

	@Test
	public void testProcess() throws Exception {
		SecurePreferenceStore store = SecurePreferenceStore.get();
		String account = store.getString(SecurePreferenceStoreConstants.P_GOOGLE_ACCOUNT);
		String pwd = store.getString(SecurePreferenceStoreConstants.P_GOOGLE_PWD);
		GoogleCalendar calendar = new GoogleCalendar();
		calendar.setGoogleAccountName(account);
		calendar.setGooglePassword(pwd);
		calendar.saveCalendar(2, "google.xml");
	}


}
