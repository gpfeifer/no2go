package de.gpfeifer.no2go.e4.app.part;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

public class ProgressDialog {
	class Cancelable implements IRunnableWithProgress {

		
		public Cancelable(Runnable runnable) {
			super();
			this.runnable = runnable;
		}

		private Runnable runnable; 

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			Thread thread = new Thread(runnable);
			thread.start();
			while (thread.isAlive()) {
				if (monitor.isCanceled()) {
					thread.interrupt();
					throw new InterruptedException();
				}
				Thread.sleep(50);
			}
			
		}
		
	}

	private boolean isCanceled;
	
	public boolean isCanceled() {
		return isCanceled;
	}
	
	public void open(Shell shell, Runnable runnable) {
		
		
		isCanceled = false;
		try {
			
			Cancelable cancelable = new Cancelable(runnable);
	          new ProgressMonitorDialog(shell).run(true, true, cancelable);
	        } catch (InvocationTargetException e) {
	        	MessageDialog.openError(shell, "Error", e.getMessage());
	        } catch (InterruptedException e) {
	        	isCanceled = true;
	        }
	}

}
