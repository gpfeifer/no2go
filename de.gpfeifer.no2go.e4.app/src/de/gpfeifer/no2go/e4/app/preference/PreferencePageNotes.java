package de.gpfeifer.no2go.e4.app.preference;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import de.gpfeifer.no2go.e4.app.part.ProgressDialog;
import de.gpfeifer.no2go.notes.NotesProcess;
import de.gpfeifer.no2go.securestore.SecurePreferenceStoreConstants;

public class PreferencePageNotes extends FieldEditorPreferencePage {
	
	class NotesVerify implements Runnable {

		private String notesDir;
		private String server;
		private String mail;
		private String pwd;
		private String errorString;

		public NotesVerify(String notesDir, String server, String mail, String pwd) {
			this.notesDir = notesDir;
			this.server = server;
			this.mail = mail;
			this.pwd = pwd;
		}
		@Override
		public void run() {
			errorString = NotesProcess.verify(notesDir, server, mail, pwd);
		}

		public String getErrorString() {
			return errorString;
		}

		
		
	}

	public PreferencePageNotes() {
		super(GRID);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		Composite composite = getFieldEditorParent();
		final DirectoryFieldEditor notesPatField = new DirectoryFieldEditor(SecurePreferenceStoreConstants.P_NOTES_PATH, "&Notes directory:", getFieldEditorParent());
		addField(notesPatField);

		final StringFieldEditor notesServerField = new StringFieldEditor(SecurePreferenceStoreConstants.P_NOTES_SERVER, "Notes server:", getFieldEditorParent());
		addField(notesServerField);

		final StringFieldEditor mailField = new StringFieldEditor(SecurePreferenceStoreConstants.P_NOTES_MAIL, "Notes mail file:", getFieldEditorParent());
		addField(mailField);

		final StringFieldEditor pwdField = new StringFieldEditor(SecurePreferenceStoreConstants.P_NOTES_PWD, "Notes password:", getFieldEditorParent());
		pwdField.getTextControl(composite).setEchoChar('*');
		addField(pwdField);

		Button verify = new Button(composite, SWT.PUSH);
		verify.setText("Verify Settings");
		verify.addSelectionListener(new SelectionAdapter() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				String notesDir = notesPatField.getStringValue();
				String server = notesServerField.getStringValue();
				String mail = mailField.getStringValue();
				String pwd = pwdField.getStringValue();
				NotesVerify notesVerify = new NotesVerify(notesDir,server,mail,pwd);
				ProgressDialog dialog = new ProgressDialog();
				dialog.open(getShell(),notesVerify);
				if (!dialog.isCanceled()) {
					String errorString = notesVerify.getErrorString();
					if (errorString == null || errorString.isEmpty()) {
						MessageDialog.openInformation(getShell(), "Notes Settings", "Notes Settings are valid.");
					} else {
							MessageDialog.openError(getShell(), "Notes Settings", "Notes Settings are invalid.\n" + errorString);
					}
				}
			}
		});
	}


	@Override
	public String getTitle() {
		return "Lotus Notes";
	}

//	public void propertyChange(PropertyChangeEvent event) {
//		if (event.getProperty().equals(SecurePreferenceStoreConstants.P_NOTES_PATH)) {
//			// field for which validation is required
//			if (event.getSource() == "ttt") {
//				// validation is successful
//				if (true) {
//					setValid(true);
//					setErrorMessage(null);
//					super.performApply();
//					super.propertyChange(event);
//				}
//				// validation fails
////				else {
////					setValid(false);
////					setErrorMessage("Error message");
////				}
//			}
//		}
//	}
}