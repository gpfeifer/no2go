package de.gpfeifer.no2go.e4.app.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;

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

public class PreferencePageGeneral
	extends FieldEditorPreferencePage
	 {

	private IntegerFieldEditor time;
	private BooleanFieldEditor isAutoSync;
	private IntegerFieldEditor day;
	private BooleanFieldEditor includeAttendees;

	public PreferencePageGeneral() {
		super(GRID);
		
//		SecurePreferenceStore store = Activator.getDefault().getSecurePreferenceStore();
//		setPreferenceStore(store);
//		setDescription("");
		
//		 PreferenceStore store = new PreferenceStore("showprefs.properties");
//		 setPreferenceStore(store);
// 		setDescription("");
//			
	}
	
	public void createFieldEditors() {
		Composite composite = getFieldEditorParent();
//		GridLayout layout = new GridLayout(3, false);

		day = new IntegerFieldEditor(
				SecurePreferenceStoreConstants.P_GENERAL_NUMBER_DAYS,
				"Number of days to synch:", 
				composite);

		addField(day);

		isAutoSync = new BooleanFieldEditor(
					SecurePreferenceStoreConstants.P_GENERAL_IS_AUTOSYNC, 
					"Automatic synchronization", 
					composite);
		
		addField(isAutoSync);
		time = new IntegerFieldEditor(
				SecurePreferenceStoreConstants.P_GENERAL_AUTOSYNC_MIN,
				"Minutes:", 
				composite);
		time.setEnabled(getPreferenceStore().getBoolean(SecurePreferenceStoreConstants.P_GENERAL_IS_AUTOSYNC), composite);
		addField(time);
		
		includeAttendees  = new BooleanFieldEditor(
				SecurePreferenceStoreConstants.P_INCLUDE_ATTENDEES, 
				"Include Attendees", 
				composite);
		addField(includeAttendees);
	
		
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

	@Override
	public String getTitle() {
		return "General";
	}
	
}