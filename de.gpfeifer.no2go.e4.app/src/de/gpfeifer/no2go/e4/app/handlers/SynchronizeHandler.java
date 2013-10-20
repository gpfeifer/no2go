package de.gpfeifer.no2go.e4.app.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

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
			
			String name = "error-" + new Date().getTime() +".txt";
			MessageDialog.openError(shell, "Error " + name, e.toString());
			PrintStream out;
			try {
				out = new PrintStream(new File( name));
				e.printStackTrace(out);
				out.close();
			} catch (FileNotFoundException e1) {
			}

			
		}
		
	}
}
