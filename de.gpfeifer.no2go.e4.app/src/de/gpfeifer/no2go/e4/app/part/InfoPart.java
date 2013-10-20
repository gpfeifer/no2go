package de.gpfeifer.no2go.e4.app.part;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.google.api.services.calendar.model.Event;

import de.gpfeifer.no2go.core.No2goCalendarEvent;
import de.gpfeifer.no2go.core.No2goUtil;
import de.gpfeifer.no2go.securestore.SecurePreferenceStore;
import de.gpfeifer.no2go.securestore.SecurePreferenceStoreConstants;
import de.gpfeifer.no2go.synch.No2goSynch;
import de.gpfeifer.no2go.synch.No2goSynchFactory;
import de.gpfeifer.no2go.synch.No2goSynchListener;

public class InfoPart implements No2goSynchListener {
	
	class SynchJob  extends Job {

//		private Shell shell;
		private No2goSynch synch;

		public SynchJob( No2goSynch synch) {
			super("Synchronize");
			this.synch = synch;
			setPriority(SHORT);
		}

		public void start() {


			schedule();
		}

		private void startDelayed() {
			SecurePreferenceStore store = SecurePreferenceStore.get();
			boolean isAutoSync = store.getBoolean(SecurePreferenceStoreConstants.P_GENERAL_IS_AUTOSYNC);
			if (isAutoSync) {
				int min = store.getInt(SecurePreferenceStoreConstants.P_GENERAL_AUTOSYNC_MIN);
				long nextStart = (long)min * 60l * 1000l;
				updateNextJobStart(nextStart);
				schedule(nextStart);
			} else {
				updateNextJobStart(-1);
			}
		}

		@Override
		protected IStatus run(IProgressMonitor arg0) {
			try {
				synch.synch();

			} catch (Exception e) {
				return new Status(IStatus.ERROR, "de.gpfeifer.no2go.e4.app", e.toString());

			} finally {
				startDelayed();
		}

			return Status.OK_STATUS;
		}
	}

	private Label autoSynchLabel;
	private Label infoLabel;
	private No2goSynch synch;
	private SynchJob synchJob;
//	private TableViewer tableViewer;
	private InfoTable infoTable;

	@Inject
	public InfoPart(Composite parent) {
		super();
		createPart(parent);
		init();
	}

	public void createPart(final Composite parent) {
		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);
		infoTable = new InfoTable();
		infoTable.create(parent);

//		createTableViewer(parent);
	
		autoSynchLabel = new Label(parent, SWT.NONE);
		GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 2;
		autoSynchLabel.setLayoutData(data);
	
		final Button button = new Button(parent, SWT.PUSH);
		button.setText("Sychronize");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ProgressDialog dialog = new ProgressDialog();
				dialog.open(parent.getShell(), new Runnable() {

					@Override
					public void run() {
						try {
							synch.synch();
						} catch (final Exception e) {
							// TODO Auto-generated catch block
							Display.getDefault().asyncExec(
								new Runnable() {
									@Override
									public void run() {
										String name = "error-" + new Date().getTime() +".txt";
										MessageDialog.openError(parent.getShell(), "Error " + name, e.toString());
										PrintStream out;
										try {
											out = new PrintStream(new File( name));
											e.printStackTrace(out);
											out.close();
										} catch (FileNotFoundException e1) {
										}

										
									}
								});	

						}
						
					}});
			}
		});
		
		infoLabel = new Label(parent, SWT.NONE);
		infoLabel.setText("");
		infoLabel.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		
		final Button delButton = new Button(parent, SWT.PUSH);
		delButton.setText("Delete Google Events");
		delButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ProgressDialog dialog = new ProgressDialog();
				dialog.open(parent.getShell(), new Runnable() {

					@Override
					public void run() {
						try {
							int number = synch.delete();
//							fireInfo("");
						} catch (final Exception e) {
							// TODO Auto-generated catch block
							Display.getDefault().asyncExec(
								new Runnable() {
									@Override
									public void run() {
										MessageDialog.openError(parent.getShell(), "Error", e.getMessage());										
									}
								});	

						}
						
					}});
			}
		});

		parent.layout();
	}

	public void googleInsert(final List<No2goCalendarEvent> insertList) {
		if (!insertList.isEmpty()) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
//					tableViewer.setInput(insertList);
				}
			});
			
		}
		
	}

	@Override
	public void info(final String info) {
		Display display = Display.getDefault();
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				infoLabel.setText(info);
			}
		});

		
	}

	public void updateNextJobStart(long nextStart) {
		if (nextStart < 0) {
			updateInfoLabel("Auto synchronize disabled");
		} else {
			Date  date = new Date(new Date().getTime() + nextStart);
			updateInfoLabel("Next auto synchronize: " + No2goUtil.printTime(date));
		}
	}

	
	private void init() {
	
		SecurePreferenceStore.get().addPropertyChangeListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				String property = event.getProperty();
				if (property.equals(SecurePreferenceStoreConstants.P_GENERAL_IS_AUTOSYNC)
						|| property.equals(SecurePreferenceStoreConstants.P_GENERAL_AUTOSYNC_MIN)
						) {
					updateSynchJob();
				}
				
			}
		});
		synch = No2goSynchFactory.create();
		synch.addListener(this);
		synch.addListener(infoTable);
		synchJob = new SynchJob(synch);
		updateSynchJob();
	}

	private void updateInfoLabel(final String string) {
		Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
				autoSynchLabel.setText(string);
				
			}
		});
		
	}

	private void updateSynchJob() {
		SecurePreferenceStore store = SecurePreferenceStore.get();
		boolean autosynch = store.getBoolean(SecurePreferenceStoreConstants.P_GENERAL_IS_AUTOSYNC);
		synchJob.cancel();
		if (autosynch) {
			synchJob.startDelayed();
		} else {
			updateNextJobStart(-1);
		}

	}

	@Override
	public void insert(Event event) {
	}

	@Override
	public void update(Event event) {
	}

	@Override
	public void delete(Event event) {
	}

	@Override
	public void unchanged(Event event) {
	}

	@Override
	public void synchBegin() {
	}

	@Override
	public void synchEnd() {
	}

}
