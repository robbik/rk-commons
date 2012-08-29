package rk.commons.loader;

public abstract class WebResourceLoaderFactory {
	
	static ResourceLoaderProvider instance = null;

	public static boolean webDetected() {
		return instance != null;
	}

	public static ResourceLoaderProvider create() {
		return instance;
	}
}
