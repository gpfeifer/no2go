package de.gpfeifer.calendar.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.gpfeifer.calendar.ui.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getSecurePreferenceStore();
		store.setDefault(PreferenceConstants.P_GOOGLE_SSL, true);
		store.setDefault(PreferenceConstants.P_GENERAL_AUTOSYNC_MIN, 10);
		store.setDefault(PreferenceConstants.P_GENERAL_IS_AUTOSYNC, false);
		store.setDefault(PreferenceConstants.P_GENERAL_NUMBER_DAYS, 30);
	}

}
