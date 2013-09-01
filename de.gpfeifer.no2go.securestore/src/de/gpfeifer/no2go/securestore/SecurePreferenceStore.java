package de.gpfeifer.no2go.securestore;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;

public class SecurePreferenceStore implements IPreferenceStore {

	private static SecurePreferenceStore  securePreferenceStore;

	static public SecurePreferenceStore get() {
		if (securePreferenceStore == null) {
			securePreferenceStore = new SecurePreferenceStore();
			securePreferenceStore.setDefault(SecurePreferenceStoreConstants.P_GOOGLE_SSL, true);
			securePreferenceStore.setDefault(SecurePreferenceStoreConstants.P_GENERAL_AUTOSYNC_MIN, 10);
			securePreferenceStore.setDefault(SecurePreferenceStoreConstants.P_GENERAL_IS_AUTOSYNC, false);
			securePreferenceStore.setDefault(SecurePreferenceStoreConstants.P_GENERAL_NUMBER_DAYS, 30);
			securePreferenceStore.setDefault(SecurePreferenceStoreConstants.P_INCLUDE_ATTENDEES, true);
		}
		return securePreferenceStore;
	}


	private ISecurePreferences secureStore;
	private IPreferenceStore preferenceStore;
	public SecurePreferenceStore() {
		init();
	}


	private void init() {
		ISecurePreferences root = SecurePreferencesFactory.getDefault();
		secureStore = root.node("no2go." + System.getProperty("os.arch"));
		preferenceStore = new PreferenceStore();
	}

	public String getString(String name) {
		try {
			return secureStore.get(name, getDefaultString(name));
		} catch (StorageException e) {
			System.err.println(e.getMessage());
		}
		return getDefaultString(name);
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
		try {
			return secureStore.getBoolean(name,getDefaultBoolean(name));
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		try {
			return secureStore.getDouble(name, getDefaultDouble(name));
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return preferenceStore.getDouble(name);
	}


	public float getFloat(String name) {
		try {
			return secureStore.getFloat(name, getDefaultFloat(name));
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return preferenceStore.getFloat(name);
	}


	public int getInt(String name) {
		try {
			return secureStore.getInt(name, getDefaultInt(name));
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return preferenceStore.getInt(name);
	}


	public long getLong(String name) {
		try {
			return secureStore.getLong(name, getDefaultInt(name));
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		try {
			secureStore.put(name,value,true);
			secureStore.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public void setValue(String name, String value) {
		try {

			secureStore.put(name, value,true);
			secureStore.flush();
			preferenceStore.putValue(name, value);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

	}

	public void setValue(String name, double value) {

		try {
			secureStore.putDouble(name, value, true);
			secureStore.flush();
			preferenceStore.setValue(name, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void setValue(String name, float value) {

		try {
			secureStore.putFloat(name, value, true);
			secureStore.flush();
			preferenceStore.setValue(name, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}


	public void setValue(String name, int value) {

		try {
			secureStore.putInt(name, value, true);
			secureStore.flush();
			preferenceStore.setValue(name, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public void setValue(String name, long value) {

		try {
			secureStore.putLong(name, value, true);
			secureStore.flush();
			preferenceStore.setValue(name, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public void setValue(String name, boolean value) {
		try {
			secureStore.putBoolean(name, value, true);
			secureStore.flush();
			preferenceStore.setValue(name, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
