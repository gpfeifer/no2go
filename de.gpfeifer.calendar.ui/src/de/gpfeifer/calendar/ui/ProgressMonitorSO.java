package de.gpfeifer.calendar.ui;

import java.io.OutputStream;
import java.io.PrintStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;

public class ProgressMonitorSO implements IProgressMonitor {
	private PrintStream out;
	
	class PrintStreamSO extends  PrintStream {
		private ProgressMonitorSO monitor;
		private Shell shell;

		public PrintStreamSO(Shell shell, ProgressMonitorSO monitor, OutputStream out) {
			super(out);
			this.monitor = monitor;
			this.shell = shell;
		}

		@Override
		public void println(final String x) {

			
			
				shell.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						monitor.setTaskName(x);
						monitor.worked(1);
					}
				});
			
			super.println(x);
		}
	}

	public ProgressMonitorSO(Shell shell, IProgressMonitor monitor) {
		super();
		this.monitor = monitor;
		this.out = System.out;
		System.setOut(new PrintStreamSO(shell, this, this.out));
	}

	IProgressMonitor monitor;

	public void beginTask(String name, int totalWork) {
		monitor.beginTask(name, totalWork);
	}

	public void done() {
		monitor.done();
		System.setOut(out);
	}

	public void internalWorked(double work) {
		monitor.internalWorked(work);
	}

	public boolean isCanceled() {
		return monitor.isCanceled();
	}

	public void setCanceled(boolean value) {
		monitor.setCanceled(value);
	}

	public void setTaskName(String name) {
		monitor.setTaskName(name);
	}

	public void subTask(String name) {
		monitor.subTask(name);
	}

	public void worked(int work) {
		monitor.worked(work);
	}

}
