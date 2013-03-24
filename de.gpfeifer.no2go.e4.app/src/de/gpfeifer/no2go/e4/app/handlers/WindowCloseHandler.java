package de.gpfeifer.no2go.e4.app.handlers;

import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.IWindowCloseHandler;
import org.eclipse.swt.widgets.Shell;

public class WindowCloseHandler implements IWindowCloseHandler {

	@Override
	public boolean close(MWindow window) {
		Object widget = window.getWidget();
		if (widget instanceof Shell) {
			Shell shell = (Shell) widget;
			shell.setMinimized(true);
		}
		return false;
	}

}
