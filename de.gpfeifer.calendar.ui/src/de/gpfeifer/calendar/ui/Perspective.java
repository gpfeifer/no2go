package de.gpfeifer.calendar.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		String editorArea = layout.getEditorArea();
		IFolderLayout consoleFolder = layout.createFolder("viewFolder", IPageLayout.TOP, 1.0f, editorArea);
		consoleFolder.addView(IConsoleConstants.ID_CONSOLE_VIEW);
//		consoleFolder.addView(SynchView.ID);

		layout.setFixed(true);

	}

}
