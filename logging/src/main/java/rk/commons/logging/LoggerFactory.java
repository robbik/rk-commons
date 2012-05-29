package rk.commons.logging;

public abstract class LoggerFactory {

	private static final String[] loggers = {
			"rk.commons.logging.Slf4jLogger",
			"rk.commons.logging.CommonsLogger",
			"rk.commons.logging.JdkLogger" };
	
	private static boolean slf4jBindingsAvailable() {
		try {
			Class.forName("org.slf4j.impl.StaticLoggerBinder");
		} catch (Throwable t) {
			return false;
		}
		
		return true;
	}

	public static Logger getLogger(Class<?> clazz) {
		int i = slf4jBindingsAvailable() ? 0 : 1;
		
		for (int length = loggers.length; i < length; ++i) {
			try {
				return (Logger) Class.forName(loggers[i])
						.getConstructor(Class.class).newInstance(clazz);
			} catch (Throwable t) {
				// do nothing
			}
		}

		return NoopLogger.INSTANCE;
	}

	public static Logger getLogger(String name) {
		int i = slf4jBindingsAvailable() ? 0 : 1;
		
		for (int length = loggers.length; i < length; ++i) {
			try {
				return (Logger) Class.forName(loggers[i])
						.getConstructor(String.class).newInstance(name);
			} catch (Throwable t) {
				// do nothing
			}
		}

		return NoopLogger.INSTANCE;
	}
}
