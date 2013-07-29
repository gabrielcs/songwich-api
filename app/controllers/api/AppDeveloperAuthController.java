package controllers.api;

import java.util.UUID;

import models.App;
import models.AppDeveloper;

import org.bson.types.ObjectId;

import play.Logger;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import controllers.api.annotation.AppDeveloperAuthenticated;
import controllers.api.util.SongwichAPIException;
import daos.api.AppDAO;
import daos.api.AppDAOMongo;
import daos.api.util.CascadeSaveDAO;
import dtos.api.util.APIResponse_V0_4;
import dtos.api.util.APIStatus_V0_4;

public class AppDeveloperAuthController extends
		Action<AppDeveloperAuthenticated> {

	public final static String DEV_AUTH_TOKEN_HEADER = "X-Songwich.devAuthToken";
	public final static String DEV_AUTH_TOKEN_HEADER_ALTERNATIVE = "X-Songwich.devauthtoken";
	public final static String DEV = "dev";
	public final static String APP = "app";

	@Override
	public Result call(Http.Context ctx) throws Throwable {
		try {
			authenticateAppDeveloper(ctx);
		} catch (SongwichAPIException e) {
			Logger.warn(String.format("%s [%s]: %s", e.getStatus().toString(),
					e.getMessage(), ctx.request()));
			APIResponse_V0_4 response = new APIResponse_V0_4(e.getStatus(),
					e.getMessage());
			return Results.unauthorized(Json.toJson(response));
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
				App app = setApp(devAuthToken);
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
				// authentication failed
				// TODO: Caon should check with Apigee whether our data is
				// up-do-date
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

	private App setApp(String devAuthToken) {
		AppDAO<ObjectId> appDAO = new AppDAOMongo();
		return appDAO.findByDevAuthToken(devAuthToken);
	}

	private AppDeveloper findAppDeveloper(Http.Context ctx,
			String devAuthToken, App app) {
		if (app != null) {
			ctx.args.put(APP, app);
			for (AppDeveloper appDeveloper : app.getAppDevelopers()) {
				if (appDeveloper.getDevAuthToken().equals(devAuthToken)) {
					ctx.args.put(DEV, appDeveloper);
					return appDeveloper;
				}
			}
		}
		return null;
	}

	/*
	 * Creates an AppDeveloper associated to an App. If devAuthToken is null a
	 * new one will be created.
	 */
	public static String createTestAppWithDeveloper(String devAuthToken) {
		Logger.debug("About to create a test AppDeveloper");

		if (devAuthToken == null) {
			devAuthToken = UUID.randomUUID().toString();
		}

		// creates a test AppDeveloper
		AppDeveloper appDeveloper = new AppDeveloper("developers@songwich.com",
				devAuthToken, "developers@songwich.com");
		// creates a test App
		App songwich = new App("Songwich", appDeveloper,
				"developers@songwich.com");
		CascadeSaveDAO<App, ObjectId> appDao = new AppDAOMongo();
		appDao.cascadeSave(songwich);

		Logger.info("Created 'developers@songwich.com' working at 'Songwich' with devAuthToken="
				+ devAuthToken);

		return devAuthToken;
	}
}
