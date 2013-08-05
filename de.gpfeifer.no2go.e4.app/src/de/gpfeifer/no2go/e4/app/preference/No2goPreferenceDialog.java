package de.gpfeifer.no2go.e4.app.preference;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.net.ProxyPreferencePage;

import de.gpfeifer.no2go.securestore.SecurePreferenceStore;





public class No2goPreferenceDialog {
	

	public void open(Shell shell) {
		
		 PreferenceManager mgr = new PreferenceManager();

	
		mgr.addToRoot(new PreferenceNode("general", new PreferencePageGeneral()));
		mgr.addToRoot(new PreferenceNode("notes", new PreferencePageNotes()));
//		mgr.addToRoot(new PreferenceNode("google", new PreferencePageGoogle()));
		mgr.addToRoot(new PreferenceNode("google3", new PreferencePageGoogle3()));
		mgr.addToRoot(new PreferenceNode("network", new PreferencePageProxy()));

		PreferenceDialog dlg = new PreferenceDialog(shell, mgr);

	    // Set the preference store
	    IPreferenceStore ps = SecurePreferenceStore.get();
	    dlg.setPreferenceStore(ps);

	    // Open the dialog
	    dlg.open();

	}
}
