package de.gpfeifer.no2go.e4.app.handlers;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MContribution;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

import de.gpfeifer.no2go.e4.app.preference.No2goPreferenceDialog;
//import org.eclipse.e4.ui.workbench.Persist;


public class PreferenceHandler {
	@CanExecute
	public boolean canExecute(@Named(IServiceConstants.ACTIVE_PART) MDirtyable dirtyable) {
//		if (dirtyable == null) {
//			return false;
//		}
//		return dirtyable.isDirty();
		return true;
	}

	@Execute
	public void execute(
			IEclipseContext context,
			@Named(IServiceConstants.ACTIVE_SHELL) Shell shell,
			@Named(IServiceConstants.ACTIVE_PART) final MContribution contribution)
			throws InvocationTargetException, InterruptedException {
		final IEclipseContext pmContext = context.createChild();
		new No2goPreferenceDialog().open(shell);
		pmContext.dispose();
	}
}
