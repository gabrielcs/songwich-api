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
import views.api.util.APIResponse_V0_4;
import views.api.util.APIStatus_V0_4;
import controllers.api.annotation.AppDeveloperAuthenticated;
import controllers.api.util.SongwichAPIException;
import daos.api.AppDAO;
import daos.api.AppDAOMongo;

public class AppDeveloperAuthenticationController extends Action<AppDeveloperAuthenticated> {

	public final static String DEV_AUTH_TOKEN_HEADER = "X-Songwich.devAuthToken";
	public final static String DEV = "dev";
	public final static String APP = "app";

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

		// app developer and user successfully authenticated
		return delegate.call(ctx);
	}

	private void authenticateAppDeveloper(Http.Context ctx)
			throws SongwichAPIException {

		String[] devAuthTokenHeaderValues = ctx.request().headers()
				.get(DEV_AUTH_TOKEN_HEADER);
		if ((devAuthTokenHeaderValues != null)
				&& (devAuthTokenHeaderValues.length == 1)
				&& (devAuthTokenHeaderValues[0] != null)) {
			try {
				AppDeveloper dev = setAppAndFindAppDeveloper(ctx,
						UUID.fromString(devAuthTokenHeaderValues[0]));
				if (dev != null) {
					ctx.args.put(DEV, dev);
				} else {
					Logger.warn(String.format("%s: %s",
							APIStatus_V0_4.INVALID_DEV_AUTH_TOKEN.toString(),
							devAuthTokenHeaderValues[0]));
					throw new SongwichAPIException(
							APIStatus_V0_4.INVALID_DEV_AUTH_TOKEN.toString(),
							APIStatus_V0_4.INVALID_DEV_AUTH_TOKEN);
				}
			} catch (IllegalArgumentException e) {
				// devAuthToken cannot be converted into a UUID
				throw new SongwichAPIException(
						APIStatus_V0_4.INVALID_DEV_AUTH_TOKEN.toString(),
						APIStatus_V0_4.INVALID_DEV_AUTH_TOKEN);
			}
		} else {
			Logger.warn(String.format("%s: %s",
					APIStatus_V0_4.INVALID_DEV_AUTH_TOKEN.toString(),
					devAuthTokenHeaderValues));
			throw new SongwichAPIException(
					APIStatus_V0_4.INVALID_DEV_AUTH_TOKEN.toString(),
					APIStatus_V0_4.INVALID_DEV_AUTH_TOKEN);
		}
	}

	private AppDeveloper setAppAndFindAppDeveloper(Http.Context ctx,
			UUID devAuthToken) {
		AppDAO<ObjectId> appDAO = new AppDAOMongo(
				DatabaseController.getDatastore());
		App app = appDAO.findByDevAuthToken(devAuthToken);
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

	public static AppDeveloper getAppDeveloper() {
		return (AppDeveloper) Http.Context.current().args.get(DEV);
	}

	public static App getApp() {
		return (App) Http.Context.current().args.get(APP);
	}
}
