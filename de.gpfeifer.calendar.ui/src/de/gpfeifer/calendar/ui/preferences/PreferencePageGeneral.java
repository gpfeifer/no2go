package de.gpfeifer.calendar.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
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

public class PreferencePageGeneral
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	private IntegerFieldEditor time;
	private BooleanFieldEditor isAutoSync;
	private IntegerFieldEditor day;

	public PreferencePageGeneral() {
		super(GRID);
		
		SecurePreferenceStore store = Activator.getDefault().getSecurePreferenceStore();
		setPreferenceStore(store);
		setDescription("");
	}
	
	public void createFieldEditors() {
		Composite composite = getFieldEditorParent();
//		GridLayout layout = new GridLayout(3, false);

		day = new IntegerFieldEditor(
				PreferenceConstants.P_GENERAL_NUMBER_DAYS,
				"Number of days to synch:", 
				composite);

		addField(day);

		isAutoSync = new BooleanFieldEditor(
					PreferenceConstants.P_GENERAL_IS_AUTOSYNC, 
					"Automatic synchronization", 
					composite);
		
		addField(isAutoSync);
		time = new IntegerFieldEditor(
				PreferenceConstants.P_GENERAL_AUTOSYNC_MIN,
				"Minutes:", 
				composite);
		time.setEnabled(getPreferenceStore().getBoolean(PreferenceConstants.P_GENERAL_IS_AUTOSYNC), composite);
		addField(time);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		update();
		
	}

	private void update() {
		boolean isAutoSynchOn = isAutoSync.getBooleanValue();
		time.setEnabled(isAutoSynchOn , getFieldEditorParent());
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		update();
	}
	
}