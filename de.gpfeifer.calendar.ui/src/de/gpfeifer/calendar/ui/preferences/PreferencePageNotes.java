package de.gpfeifer.calendar.ui.preferences;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.gpfeifer.calendar.ui.Activator;

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

public class PreferencePageNotes
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public PreferencePageNotes() {
		super(GRID);
		SecurePreferenceStore store = Activator.getDefault().getSecurePreferenceStore();
//		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setPreferenceStore(store);
		setDescription("Notes Settings");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		Composite composite = getFieldEditorParent();
		addField(new DirectoryFieldEditor(PreferenceConstants.P_NOTES_PATH, 
				"&Notes directory:", getFieldEditorParent()));
		
		Button button = new Button(composite,SWT.PUSH);

//		addField(
//			new BooleanFieldEditor(
//				PreferenceConstants.P_BOOLEAN,
//				"&An example of a boolean preference",
//				getFieldEditorParent()));

		addField(
				new StringFieldEditor(
						PreferenceConstants.P_NOTES_SERVER, 
						"Notes server:", 
						getFieldEditorParent()));

		addField(
			new StringFieldEditor(
					PreferenceConstants.P_NOTES_MAIL, 
					"Notes mail file:", 
					getFieldEditorParent()));

		StringFieldEditor pwd = new StringFieldEditor(
				PreferenceConstants.P_NOTES_PWD, 
				"Notes password:", 
				getFieldEditorParent());

		pwd.getTextControl( composite ).setEchoChar( '*' );
//		pwd.setPreferenceStore(fooPreferences);

		addField(pwd);

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}


	
}