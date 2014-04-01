package jobs;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import play.api.Application;
import play.api.DefaultApplication;
import play.api.Mode;
import play.api.Play;
import util.api.MyLogger;

/*
 * Pinger to be set on a Heroku Scheduler so that the Heroku dyno doesn't sleep.
 * Based on: https://github.com/jamesward/play2-scheduled-job-demo
 * See also 'scheduledping' on /Procfile.
 */
public class PingerJob {

	// more info on Heroku config vars at
	// https://devcenter.heroku.com/articles/config-vars
	private static String url = "http://" + System.getenv("HEROKU_APP_NAME")
			+ ".herokuapp.com";

	private static final int TIMEOUT = 25000; // 25 seconds

	// based on
	// http://stackoverflow.com/questions/3584210/preferred-java-way-to-ping-a-http-url-for-availability
	public static void ping() {
		url = url.replaceFirst("https", "http"); // Otherwise an exception may
													// be thrown on invalid SSL
													// certificates.

		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url)
					.openConnection();
			connection.setConnectTimeout(TIMEOUT);
			connection.setReadTimeout(TIMEOUT);
			connection.setRequestMethod("HEAD");
			int responseCode = connection.getResponseCode();
			if (!(200 <= responseCode && responseCode <= 399)) {
				MyLogger.warn(String.format("Ping response: [%d] %s",
						responseCode, connection.getResponseMessage()));
			} else {
				MyLogger.debug(String.format("Ping response: [%d] %s",
						responseCode, connection.getResponseMessage()));
			}
		} catch (IOException exception) {
			MyLogger.warn(String.format("PingerJob [%s]: %s", exception
					.getClass().getSimpleName(), exception.getMessage()));
		}
	}

	public static void main(String[] args) {
		Application application = new DefaultApplication(new File(args[0]),
				PingerJob.class.getClassLoader(), null, Mode.Prod());
		Play.start(application);
		ping();
	}
}
