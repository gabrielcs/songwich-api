package controllers.api.auth;

import models.api.scrobbles.App;
import models.api.scrobbles.AppDeveloper;
import models.api.scrobbles.User;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import util.api.MyLogger;
import util.api.SongwichAPIException;
import controllers.api.annotation.Logged;

public class APILoggingController extends Action<Logged> {
	@Override
	// public F.Promise<SimpleResult> call(Http.Context context) throws
	// Throwable {
	public Result call(Http.Context context) throws Throwable {
		logAPICall(context);
		// process request
		return delegate.call(context);
	}

	private void logAPICall(Http.Context context) throws SongwichAPIException {
		// retrieves app, dev and user (the Logged annotation shall come after
		// the other ones)
		App app = (App) context.args.get(AppDeveloperAuthController.APP);
		AppDeveloper dev = (AppDeveloper) context.args
				.get(AppDeveloperAuthController.DEV);
		// user might be null
		User user = (User) context.args.get(UserAuthController.USER);

		// builds the string
		StringBuilder logString = new StringBuilder();
		if (dev != null) {
			logString.append(String.format("\n Developer: %s\n App:       %s",
					dev.getEmailAddress(), app.getName()));
		}

		if (user != null) {
			logString.append(String.format("\n User:      %s (id: %s)",
					user.getEmailAddress(), user.getId()));
		}

		logString.append("\n Request:   " + context.request());

		if (context.request().body().asJson() != null) {
			logString.append("\n Body:      "
					+ context.request().body().asJson());
		}

		// process the log
		MyLogger.info(logString.toString());
	}
}
