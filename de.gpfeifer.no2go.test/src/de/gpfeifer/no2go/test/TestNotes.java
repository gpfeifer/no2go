package de.gpfeifer.no2go.test;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.gpfeifer.no2go.notes.NotesProcess;
import de.gpfeifer.no2go.securestore.SecurePreferenceStore;
import de.gpfeifer.no2go.securestore.SecurePreferenceStoreConstants;

public class TestNotes {

//	@Test
	public void testProcess() throws Exception {
		SecurePreferenceStore store = SecurePreferenceStore.get();
		String path = store.getString(SecurePreferenceStoreConstants.P_NOTES_PATH);
		String server = store.getString(SecurePreferenceStoreConstants.P_NOTES_SERVER);
		String mail = store.getString(SecurePreferenceStoreConstants.P_NOTES_MAIL);
		String pwd = store.getString(SecurePreferenceStoreConstants.P_NOTES_PWD);
		
		String errorString = NotesProcess.verifyNotesPath(path);
		assertNull(errorString);
		new NotesProcess().run("save", server,mail,pwd, "10", "notes-process.txt");
//		NotesProcess.save(path,server,mail,pwd, "4", "notes.txt");

	}

//	@Test
	public void testSave() throws Exception {
		SecurePreferenceStore store = SecurePreferenceStore.get();
		String path = store.getString(SecurePreferenceStoreConstants.P_NOTES_PATH);
		String server = store.getString(SecurePreferenceStoreConstants.P_NOTES_SERVER);
		String mail = store.getString(SecurePreferenceStoreConstants.P_NOTES_MAIL);
		String pwd = store.getString(SecurePreferenceStoreConstants.P_NOTES_PWD);


		
		
		String errorString = NotesProcess.verifyNotesPath(path);
		assertNull(errorString);
		new NotesProcess().run("save", server,mail,pwd, "30", "notes.txt");
	}

	@Test
	public void testVerify() throws Exception {
		SecurePreferenceStore store = SecurePreferenceStore.get();
		String path = store.getString(SecurePreferenceStoreConstants.P_NOTES_PATH);
		String server = store.getString(SecurePreferenceStoreConstants.P_NOTES_SERVER);
		String mail = store.getString(SecurePreferenceStoreConstants.P_NOTES_MAIL);
		String pwd = store.getString(SecurePreferenceStoreConstants.P_NOTES_PWD);
		new NotesProcess().run("save", server,mail,pwd+"ss", "30", "notes.txt");
		NotesProcess.verify(path, server, mail, pwd + "ss");
	}

}
