package controllers.api.auth;

import models.api.scrobbles.App;
import models.api.scrobbles.AppDeveloper;
import models.api.scrobbles.AuthToken;

import org.bson.types.ObjectId;

import play.mvc.SimpleResult;
import play.libs.F;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Results;
import util.api.MyLogger;
import util.api.SongwichAPIException;
import views.api.APIResponse_V0_4;
import views.api.APIStatus_V0_4;
import controllers.api.annotation.AppDeveloperAuthenticated;
import database.api.CascadeSaveDAO;
import database.api.scrobbles.AppDAO;
import database.api.scrobbles.AppDAOMongo;

public class AppDeveloperAuthController extends Action<AppDeveloperAuthenticated> {
	
	public final static String DEV_AUTH_TOKEN_HEADER = "X-Songwich.devAuthToken";
	public final static String DEV_AUTH_TOKEN_HEADER_ALTERNATIVE = "X-Songwich.devauthtoken";
	public final static String DEV = "dev";
	public final static String APP = "app";

	@Override
	public F.Promise<SimpleResult> call(Http.Context ctx) throws Throwable {
	//public Result call(Http.Context ctx) throws Throwable {
		try {
			authenticateAppDeveloper(ctx);
		} catch (SongwichAPIException e) {
			MyLogger.warn(String.format("%s [%s]: %s",
					e.getStatus().toString(), e.getMessage(), ctx.request()));
			APIResponse_V0_4 response = new APIResponse_V0_4(e.getStatus(),
					e.getMessage());
			return F.Promise.<SimpleResult> pure(Results.unauthorized(Json.toJson(response)));
			//return Results.unauthorized(Json.toJson(response));
		}

		// app developer successfully authenticated
		return delegate.call(ctx);
	}

	private void authenticateAppDeveloper(Http.Context context)
			throws SongwichAPIException {

		String[] devAuthTokenHeaderValues = context.request().headers()
				.get(DEV_AUTH_TOKEN_HEADER);
		if (devAuthTokenHeaderValues == null) {
			// we need this on Heroku
			devAuthTokenHeaderValues = context.request().headers()
					.get(DEV_AUTH_TOKEN_HEADER_ALTERNATIVE);
		}

		if ((devAuthTokenHeaderValues != null)
				&& (devAuthTokenHeaderValues.length == 1)
				&& (devAuthTokenHeaderValues[0] != null)) {

			// there's 1 and only 1 auth token
			AppDeveloper dev;
			try {
				String devAuthToken = devAuthTokenHeaderValues[0];
				App app = findApp(devAuthToken);
				dev = findAppDeveloper(context, devAuthToken, app);
			} catch (IllegalArgumentException e) {
				// auth token cannot be converted into a UUID
				throw new SongwichAPIException("Invalid devAuthToken: "
						+ devAuthTokenHeaderValues[0],
						APIStatus_V0_4.INVALID_DEV_AUTH_TOKEN);
			}

			if (dev != null) {
				// authentication successful
				context.args.put(DEV, dev);
			} else {
				throw new SongwichAPIException("Invalid devAuthToken: "
						+ devAuthTokenHeaderValues[0],
						APIStatus_V0_4.INVALID_DEV_AUTH_TOKEN);
			}
		} else {
			// there's a number of userAuthTokens different than 1
			throw new SongwichAPIException(
					"There's a number of X-Songwich.devAuthToken headers different than 1: "
							+ devAuthTokenHeaderValues,
					APIStatus_V0_4.INVALID_DEV_AUTH_TOKEN);
		}
	}

	private App findApp(String devAuthToken) {
		AppDAO<ObjectId> appDAO = new AppDAOMongo();
		return appDAO.findByDevAuthToken(devAuthToken);
	}

	private AppDeveloper findAppDeveloper(Http.Context ctx,
			String devAuthToken, App app) {
		if (app != null) {
			ctx.args.put(APP, app);
			for (AppDeveloper appDeveloper : app.getAppDevelopers()) {
				if (appDeveloper.getDevAuthToken().getToken()
						.equals(devAuthToken)) {
					ctx.args.put(DEV, appDeveloper);
					return appDeveloper;
				}
			}
		}
		return null;
	}

	/*
	 * Creates an AppDeveloper associated to an App.
	 */
	public static String createTestAppWithDeveloper(String devAuthToken) {
		String homeDevEmail = "developers@songwich.com";

		AuthToken authToken = AuthToken.createDevAuthToken();
		authToken.setToken(devAuthToken);

		// creates a test AppDeveloper
		AppDeveloper appDeveloper = new AppDeveloper(homeDevEmail,
				"Songwich Developers", authToken);
		// creates a test App
		App songwich = new App("Songwich", appDeveloper);
		CascadeSaveDAO<App, ObjectId> appDao = new AppDAOMongo();
		appDao.cascadeSave(songwich, homeDevEmail);

		MyLogger.debug("Created 'developers@songwich.com' working at 'Songwich' with devAuthToken="
				+ authToken);

		return devAuthToken;
	}
}
