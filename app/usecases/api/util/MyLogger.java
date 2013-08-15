package usecases.api.util;

import play.Logger;

public class MyLogger {
	public static void trace(String message) {
		Logger.trace(message);
	}

	public static void debug(String message) {
		Logger.debug(message);
	}

	public static void info(String message) {
		Logger.info(message);
	}

	public static void warn(String message) {
		Logger.warn(message);
	}

	public static void error(String message) {
		Logger.error(message);
	}
}
