package de.gpfeifer.no2go.e4.app.preference;

import java.util.Date;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import de.gpfeifer.no2go.google.GoogleCalendar;
import de.gpfeifer.no2go.securestore.SecurePreferenceStoreConstants;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class PreferencePageGoogle extends FieldEditorPreferencePage {

	public PreferencePageGoogle() {
		super(GRID);
	}
	
	public void createFieldEditors() {
		Composite composite = getFieldEditorParent();


		final StringFieldEditor accountField = new StringFieldEditor(
					SecurePreferenceStoreConstants.P_GOOGLE_ACCOUNT, 
					"Google account:", 
					getFieldEditorParent());
		addField(accountField);
				
		final StringFieldEditor pwd = new StringFieldEditor(
				SecurePreferenceStoreConstants.P_GOOGLE_PWD, 
				"Google password:", 
				getFieldEditorParent());

		pwd.getTextControl( composite ).setEchoChar( '*' );
		addField(pwd);
		
		Button verify = new Button(composite, SWT.PUSH);
		verify.setText("Verify Settings");
		verify.addSelectionListener(new SelectionAdapter() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				GoogleCalendar googleCalendar = new GoogleCalendar();
				googleCalendar.setGoogleAccountName(accountField.getStringValue());
				googleCalendar.setGooglePassword(pwd.getStringValue());
				try {
					googleCalendar.getCalendarEntries(new Date(), 10);
					MessageDialog.openInformation(getShell(), "Google Settings", "Google Settings are valid." );
				} catch (Exception e) {
					MessageDialog.openError(getShell(), "Google Settings", "Google Settings are invalid.\n" + e.getMessage());
				} 
				
			}
		});

	}

	@Override
	public String getTitle() {
		return "Google";
	}
		
}