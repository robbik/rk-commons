package rk.commons.loader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class WebResourceLoader implements ResourceLoaderProvider, ServletContextListener {
	
	private static final String LOCATION_PREFIX = "web:";
	
	private ServletContext sc;
	
	public void contextInitialized(ServletContextEvent sce) {
		sc = sce.getServletContext();
		
		WebResourceLoaderFactory.instance = this;
	}
	
	public void contextDestroyed(ServletContextEvent sce) {
		WebResourceLoaderFactory.instance = null;
		
		sc = null;
	}
	
	public boolean canHandle(String location) {
		return location.startsWith(LOCATION_PREFIX);
	}

	public List<URL> getURLs(String location) {
		List<URL> resolved = new ArrayList<URL>();

		if (location.startsWith(LOCATION_PREFIX)) {
			location = location.substring(LOCATION_PREFIX.length());

			URL found;
			
			try {
				found = sc.getResource(location);
			} catch (Throwable t) {
				found = null;
			}
			
			if (found != null) {
				resolved.add(found);
			}
		}

		return resolved;
	}

	public URL getURL(String location) {
		URL resolved = null;

		if (location.startsWith(LOCATION_PREFIX)) {
			location = location.substring(LOCATION_PREFIX.length());
			
			try {
				resolved = sc.getResource(location);
			} catch (Throwable t) {
				// do nothing
			}
		}

		return resolved;
	}

	public Class<?> loadClass(String className) {
		return null;
	}
}
