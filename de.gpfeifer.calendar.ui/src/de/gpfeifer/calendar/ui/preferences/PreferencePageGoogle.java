package de.gpfeifer.calendar.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
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

public class PreferencePageGoogle
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public PreferencePageGoogle() {
		super(GRID);
		SecurePreferenceStore store = Activator.getDefault().getSecurePreferenceStore();
		setPreferenceStore(store);
		setDescription("");
	}
	
	public void createFieldEditors() {
		Composite composite = getFieldEditorParent();

		addField(
			new StringFieldEditor(
					PreferenceConstants.P_GOOGLE_ACCOUNT, 
					"Google account:", 
					getFieldEditorParent()));
		
		
		StringFieldEditor pwd = new StringFieldEditor(
				PreferenceConstants.P_GOOGLE_PWD, 
				"Google password:", 
				getFieldEditorParent());

		pwd.getTextControl( composite ).setEchoChar( '*' );
		addField(pwd);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}