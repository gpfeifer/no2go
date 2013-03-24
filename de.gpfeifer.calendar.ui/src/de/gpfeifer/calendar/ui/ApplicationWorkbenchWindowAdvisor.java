package de.gpfeifer.calendar.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.channels.FileChannel;

import org.eclipse.core.internal.net.ProxyManager;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;

import de.gpfeifer.calendar.ui.command.ExitHandler;
import de.gpfeifer.calendar.ui.preferences.PreferenceConstants;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	private IWorkbenchWindow window;
	private TrayItem trayItem;
	private Image trayImage;
	
	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(800, 300));
		configurer.setShowCoolBar(true);
		configurer.setShowCoolBar(true);

	        // XXX We set the status line and progress indicator so that update
	        // information can be shown there
		configurer.setShowStatusLine(true);
		configurer.setShowProgressIndicator(true);
		

	}
	// As of here is the new stuff
	@Override
	public void postWindowOpen() {
		super.postWindowOpen();
		window = getWindowConfigurer().getWindow();
		trayItem = initTaskItem(window);
		// Some OS might not support tray items
		if (trayItem != null) {
			minimizeBehavior();
			// Create exit and about action on the icon
			hookPopupMenu();
		}
		try {
			ProxyManager pm = (ProxyManager) ProxyManager.getProxyManager();
			pm.initialize();
		} catch (Exception e) {
		}
		updateNotesJar();
		
		
		MessageConsole console = new MessageConsole("System Output", null);
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);
		MessageConsoleStream stream = console.newMessageStream();

		System.setOut(new PrintStream(stream));
		System.setErr(new PrintStream(stream));

		
	}

	private void updateNotesJar() {
		IPreferenceStore store = Activator.getDefault().getSecurePreferenceStore();
		String path = store.getString(PreferenceConstants.P_NOTES_PATH);
		if (path != null ) {
			File file = new File(path,"jvm/lib/ext/notes.jar");
			if (file.exists()) {
				String notesBundle = getNotesLocation();
				if (notesBundle != null) {
					File to = new File(getNotesLocation(),"Notes.jar");
					try {
						copy(file,to);
					} catch (IOException e) {
					}
				}
			}
		}
	}
	
	public String getNotesLocation() {
		Bundle bundle = Platform.getBundle("lotus.notes");
		URL locationUrl =FileLocator.find(bundle,new Path("/"), null);
		try {
			return FileLocator.toFileURL(locationUrl).getFile();
		} catch (IOException e) {
		}
		return null;
		}

	private void copy(File source, File target) throws IOException {
		FileChannel in = (new FileInputStream(source)).getChannel();
		FileChannel out = (new FileOutputStream(target)).getChannel();
		in.transferTo(0, source.length(), out);
		in.close();
		out.close();
	}

	// Add a listener to the shell
	
	private void minimizeBehavior() {
		window.getShell().addShellListener(new ShellAdapter() {
			// If the window is minimized hide the window
			public void shellIconified(ShellEvent e) {
				window.getShell().setVisible(false);
			}
		});
		// If user double-clicks on the tray icons the application will be
		// visible again
		trayItem.addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(Event event) {
				Shell shell = window.getShell();
				if (!shell.isVisible()) {
					window.getShell().setMinimized(false);
					shell.setVisible(true);
				}
			}
		});
	}

	// We hook up on menu entry which allows to close the application
	private void hookPopupMenu() {
		trayItem.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				Menu menu = new Menu(window.getShell(), SWT.POP_UP);

				// Creates a new menu item that terminates the program
				// when selected
				MenuItem exit = new MenuItem(menu, SWT.NONE);
				exit.setText("Exit");
				exit.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event) {
						// Lets call our command
						IHandlerService handlerService = (IHandlerService) window
								.getService(IHandlerService.class);
						try {
							handlerService.executeCommand(ExitHandler.COMMAND_ID, null);
						} catch (Exception ex) {
							throw new RuntimeException(ExitHandler.COMMAND_ID);
						}
					}
				});
				// We need to make the menu visible
				menu.setVisible(true);
			}
		});
	}

	// This methods create the tray item and return a reference
	private TrayItem initTaskItem(IWorkbenchWindow window) {
		final Tray tray = window.getShell().getDisplay().getSystemTray();
		TrayItem trayItem = new TrayItem(tray, SWT.NONE);
		trayImage = AbstractUIPlugin.imageDescriptorFromPlugin(
				"de.gpfeifer.calendar.ui", "hsun2.jpg")
				.createImage();
		trayItem.setImage(trayImage);
		trayItem.setToolTipText("GSynch");
		return trayItem;

	}

	// We need to clean-up after ourself
	@Override
	public void dispose() {
		if (trayImage != null) {
			trayImage.dispose();
		}
		if (trayItem != null) {
			trayItem.dispose();
		}
	}

	@Override
	public boolean preWindowShellClose() {
		 getWindowConfigurer().getWindow().getShell().setVisible(false); 

		return false;
	}
	
}
