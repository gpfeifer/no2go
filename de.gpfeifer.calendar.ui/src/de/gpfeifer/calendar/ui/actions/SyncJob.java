package de.gpfeifer.calendar.ui.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;

import de.gpfeifer.calendar.ui.Activator;
import de.gpfeifer.calendar.ui.ProgressMonitorSO;
import de.gpfeifer.calendar.ui.preferences.PreferenceConstants;
import de.gpfeifer.calendar.ui.preferences.SecurePreferenceStore;

public class SyncJob extends Job {

	private Shell shell;

	public SyncJob(Shell shell) {
		super("Synchronize");
		this.shell = shell;
	}

	public void start() {
		setPriority(SHORT);
		schedule();
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
//		final Shell shell = Display.getCurrent().getActiveShell();
		ProgressMonitorSO m = new ProgressMonitorSO(shell,monitor);
		m.beginTask("Synchronize", 15);
		m.worked(1);
		//System.out.println("Job started." + new Date());
		IStatus result = Status.OK_STATUS;
		try {
			new SyncRunnable().sync();
		} catch (Exception e) {
			result = new Status(Status.ERROR,Activator.PLUGIN_ID,"Synchronize failed.", e );
		} finally {
			SecurePreferenceStore store = Activator.getDefault().getSecurePreferenceStore();
			boolean isAutoSync = store.getBoolean(PreferenceConstants.P_GENERAL_IS_AUTOSYNC);
			if (isAutoSync) {
				int min = store.getInt(PreferenceConstants.P_GENERAL_AUTOSYNC_MIN);
				schedule((long)min * 60l * 1000l);
			}
		}
		m.done();
		return result;
	}

}
