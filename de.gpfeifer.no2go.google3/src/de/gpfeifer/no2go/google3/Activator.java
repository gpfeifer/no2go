package de.gpfeifer.no2go.google3;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

public class Activator implements BundleActivator {

	private static Logger logger = LoggerFactory.getLogger(Activator.class);
	private static Activator instance;
	private IProxyService proxyService;

	@Override
	public void start(BundleContext context) throws Exception {
		// Force activation of IProxyService
		instance = this;
		ServiceReference<IProxyService> serviceReference = context.getServiceReference(IProxyService.class);
		if (serviceReference != null) {
			proxyService = context.getService(serviceReference);
			logger.debug("IProxyService found");
		} else {
			logger.warn("IProxyService NOT found");
		}

	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the instance
	 */
	public static Activator getInstance() {
		return instance;
	}

	/**
	 * @return the proxyService
	 */
	public IProxyService getProxyService() {
		return proxyService;
	}

	public HttpTransport getHttpTransport() {
		HttpTransport httpTransport = null;

		IProxyData[] proxydata = getProxyData();
		String msg = "";
		if (proxydata != null && proxydata.length > 0) {
			IProxyData p = proxydata[0];
			String host = p.getHost();
			int port = p.getPort();
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
			httpTransport = new NetHttpTransport.Builder().setProxy(proxy).build();
			msg = "Get HttpTransport using proxy" + host + ":" + port;
		} else {
			httpTransport = new NetHttpTransport.Builder().build();
			msg = "Get HttpTransport (no proxy)";
		}
		logger.debug(msg);
		return httpTransport;
	}

	private IProxyData[] getProxyData() {
		if (proxyService != null) {
			try {
				IProxyData[] select = proxyService.select(new URI("https://www.googleapis.com/calendar/v3"));
				return select;
			} catch (URISyntaxException e) {
			}
		}
		return null;
	}


}
