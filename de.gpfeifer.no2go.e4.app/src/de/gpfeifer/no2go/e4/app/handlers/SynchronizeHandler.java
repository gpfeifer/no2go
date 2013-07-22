package de.gpfeifer.no2go.e4.app.handlers;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import de.gpfeifer.no2go.synch.No2goSynchFactory;

public class SynchronizeHandler {

	@Execute
	public void execute(IEclipseContext context, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell)
			throws InvocationTargetException, InterruptedException {
		try {
			No2goSynchFactory.create().synch();
			MessageDialog.openInformation(shell, "Info", "Done");
		} catch (Exception e) {
			MessageDialog.openError(shell, "Error", e.getMessage());
		}
		
	}
}
