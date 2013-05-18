package de.gpfeifer.no2go.e4.app;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;

/**
 * The LifeCycleManager is registered via an extension point (plugin.xml)
 * 
 * @author gpfeifer
 *
 */
public class LifeCycleManager {

	@PostContextCreate
	public void postContextCreate(IEclipseContext context, IEventBroker eventBroker) {
		new SystemTrayManager(context,eventBroker);
	}
	
	

}
