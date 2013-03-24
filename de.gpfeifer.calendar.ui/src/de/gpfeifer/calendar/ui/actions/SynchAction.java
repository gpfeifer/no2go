package de.gpfeifer.calendar.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;

public class SynchAction extends Action {

	@Override
	public String getText() {
		return "Synchronize";
	}


	
	@Override
	public void run() {
		new SyncJob(Display.getCurrent().getActiveShell()).start();
//		final Shell shell = Display.getCurrent().getActiveShell();
//		ProgressMonitorDialog pd = new ProgressMonitorDialog(shell);
//		try {
//			pd.run(true, false, new IRunnableWithProgress() {
//
//					@Override
//					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
//						ProgressMonitorSO m = new ProgressMonitorSO(shell, monitor);
//						m.beginTask("Synchronize", 15);
//						m.worked(1);
//						try {
//							new SyncRunnable().sync();
//						} catch (Exception e) {
//							System.out.println("ERROR: " + e.getMessage());
//						} finally {
//							m.done();
//						}
//
//					}
//				});
//		} catch (InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		pd.close();
	}

}
