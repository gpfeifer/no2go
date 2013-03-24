package de.gpfeifer.no2go.e4.app;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.contexts.RunAndTrack;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.IWindowCloseHandler;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import de.gpfeifer.no2go.e4.app.handlers.WindowCloseHandler;

public class SystemTrayManager  {
	private static Image image;
	  static {
	    URL url = null;
	    try {
	    	url = new URL("platform:/plugin/de.gpfeifer.no2go.e4.app/icons/no2go.jpg");
		    image = ImageDescriptor.createFromURL(url).createImage();
	    } catch (MalformedURLException e) {
	    }

	  }
	
//	private static Image 


	private boolean isInitializedithShell = false;

	public SystemTrayManager(IEventBroker eventBroker) {
		init(eventBroker);
	}


	private void init(IEventBroker eventBroker) {
		eventBroker.subscribe(UIEvents.Context.TOPIC_CONTEXT, new EventHandler() {

					@Override
					public void handleEvent(Event event) {
						if(!UIEvents.isSET(event) ) {
							return;
						}
						Object element = event.getProperty(UIEvents.EventTags.ELEMENT);
						if (element instanceof MTrimmedWindow) {
							final MTrimmedWindow tb = (MTrimmedWindow) element;
							IEclipseContext context = tb.getContext();
							if (context != null) {
								
								context.runAndTrack(new RunAndTrack() {

									private final IWindowCloseHandler quitHandler = new WindowCloseHandler();

									@Override
									public boolean changed(IEclipseContext context) {
										// access the context value to be eevaluated
										// on every future change of the value
										Object value = context.get(IWindowCloseHandler.class); 
										if (value != null) {
											Object shell = context.get(Shell.class);
											if (shell != null) {
												init((Shell)shell);
											}
											
											// prevents endless loop
											if (!quitHandler.equals(value)) {
												context.set( IWindowCloseHandler.class,	quitHandler);
											}
										}

										return true; // true keeps tracking 
									}
								});
							}
						}
					}

				});


		
	}



	private TrayItem createTrayItem(Shell shell) {
		final Tray tray = shell.getDisplay().getSystemTray();
		TrayItem trayItem = new TrayItem(tray, SWT.NONE);
//		trayImage = AbstractUIPlugin.imageDescriptorFromPlugin(
//				"de.gpfeifer.calendar.ui", "hsun2.jpg")
//				.createImage();
		if (image != null) {
			trayItem.setImage(image);
		}
		trayItem.setToolTipText("Notes -> Google");
		return trayItem;

	}


	synchronized private void init(final Shell shell) {
		if (shell == null) {
			return;
		}
		if (!isInitializedithShell) {
			isInitializedithShell = true;
			TrayItem trayItem = createTrayItem(shell);
			shell.addShellListener(new ShellAdapter() {
				// If the window is minimized hide the window
				public void shellIconified(ShellEvent e) {
					shell.setVisible(false);
				}
			});
			// If user double-clicks on the tray icons the application will be
			// visible again
			trayItem.addListener(SWT.DefaultSelection, new Listener() {
				public void handleEvent(org.eclipse.swt.widgets.Event event) {
					if (!shell.isVisible()) {
						shell.setMinimized(false);
						shell.setVisible(true);
					}
				}
			});
		}
		
	}

}
