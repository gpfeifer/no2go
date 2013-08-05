package de.gpfeifer.no2go.e4.app;

import org.eclipse.core.net.proxy.IProxyService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private static Activator instance;

	static BundleContext getContext() {
		return context;
	}

	private IProxyService proxyService;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		instance = this;
		// Force activation of IProxyService
		ServiceReference<IProxyService> serviceReference = bundleContext.getServiceReference(IProxyService.class);
		if (serviceReference != null) {
			proxyService = bundleContext.getService(serviceReference);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

	/**
	 * @return the proxyService
	 */
	public IProxyService getProxyService() {
		return proxyService;
	}

}
