package de.gpfeifer.no2go.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		File no2goDir = new File(System.getProperty("user.home"),".no2go");
		no2goDir.mkdirs();
		String name = "logback.xml";
		File logbackConfig = new File(no2goDir,name);
		if (!logbackConfig.exists()) {
			copyConfig(logbackConfig,bundleContext);
		}

		System.setProperty("logback.configurationFile", logbackConfig.getAbsolutePath());
//		 JoranConfigurator configurator = new JoranConfigurator();
//		 StatusManager statusManager = configurator.getStatusManager();

//		Logger logger = LoggerFactory.getLogger("Test");
//		logger.info("Test");
		
	}

	private void copyConfig(File logbackConfig, BundleContext bundleContext)  {
		URL entry = bundleContext.getBundle().getEntry("/logback.xml");
		if (entry == null) {
			return;
		}
		InputStream source;
		try {
			source = entry.openStream();
		} catch (IOException e1) {
			System.err.println(e1);
			return;
		}

		if (source != null) {
			FileOutputStream out;
			try {
				out = new FileOutputStream(logbackConfig);
			} catch (FileNotFoundException e1) {
				System.err.println(e1);
				return;
			}
			try {
				copyFile(source,out);
			} catch (IOException e) {
				System.err.println(e);
			}
		}
		
	}

	public static void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) != -1) {
		    out.write(buffer, 0, len);
		}
	}
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
