package de.gpfeifer.calendar.ui.preferences;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;

public class SecurePreferenceStore implements IPreferenceStore {

	private ISecurePreferences securePreferences;
	private IPreferenceStore preferenceStore;
	public SecurePreferenceStore(IPreferenceStore iPreferenceStore) {
		init(iPreferenceStore);
	}


	private void init(IPreferenceStore iPreferenceStore) {
		ISecurePreferences root = SecurePreferencesFactory.getDefault();
		securePreferences = root.node("calendar.preference");
		preferenceStore = iPreferenceStore;
	}

	public String getString(String name) {
		try {
			return securePreferences.get(name, getDefaultString(name));
		} catch (StorageException e) {
			System.err.println(e.getMessage());
		}
		return getDefaultString(name);
	}

	public void setValue(String name, String value) {
		try {
			securePreferences.put(name, value,true);
		} catch (StorageException e) {
			System.err.println(e.getMessage());
		}
	}


	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		preferenceStore.addPropertyChangeListener(listener);
	}


	public boolean contains(String name) {
		return preferenceStore.contains(name);
	}


	public void firePropertyChangeEvent(String name, Object oldValue, Object newValue) {
		preferenceStore.firePropertyChangeEvent(name, oldValue, newValue);
	}


	public boolean getBoolean(String name) {
		return preferenceStore.getBoolean(name);
	}


	public boolean getDefaultBoolean(String name) {
		return preferenceStore.getDefaultBoolean(name);
	}


	public double getDefaultDouble(String name) {
		return preferenceStore.getDefaultDouble(name);
	}


	public float getDefaultFloat(String name) {
		return preferenceStore.getDefaultFloat(name);
	}


	public int getDefaultInt(String name) {
		return preferenceStore.getDefaultInt(name);
	}


	public long getDefaultLong(String name) {
		return preferenceStore.getDefaultLong(name);
	}


	public String getDefaultString(String name) {
		return preferenceStore.getDefaultString(name);
	}


	public double getDouble(String name) {
		return preferenceStore.getDouble(name);
	}


	public float getFloat(String name) {
		return preferenceStore.getFloat(name);
	}


	public int getInt(String name) {
		return preferenceStore.getInt(name);
	}


	public long getLong(String name) {
		return preferenceStore.getLong(name);
	}


	public boolean isDefault(String name) {
		return preferenceStore.isDefault(name);
	}


	public boolean needsSaving() {
		return preferenceStore.needsSaving();
	}


	public void putValue(String name, String value) {
		preferenceStore.putValue(name, value);
	}


	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		preferenceStore.removePropertyChangeListener(listener);
	}


	public void setDefault(String name, double value) {
		preferenceStore.setDefault(name, value);
	}


	public void setDefault(String name, float value) {
		preferenceStore.setDefault(name, value);
	}


	public void setDefault(String name, int value) {
		preferenceStore.setDefault(name, value);
	}


	public void setDefault(String name, long value) {
		preferenceStore.setDefault(name, value);
	}


	public void setDefault(String name, String defaultObject) {
		preferenceStore.setDefault(name, defaultObject);
	}


	public void setDefault(String name, boolean value) {
		preferenceStore.setDefault(name, value);
	}


	public void setToDefault(String name) {
		preferenceStore.setToDefault(name);
	}


	public void setValue(String name, double value) {
		preferenceStore.setValue(name, value);
	}


	public void setValue(String name, float value) {
		preferenceStore.setValue(name, value);
	}


	public void setValue(String name, int value) {
		preferenceStore.setValue(name, value);
	}


	public void setValue(String name, long value) {
		preferenceStore.setValue(name, value);
	}


	public void setValue(String name, boolean value) {
		preferenceStore.setValue(name, value);
	}

}
