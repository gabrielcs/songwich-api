package controllers.api.auth;

import models.api.scrobbles.User;

import org.bson.types.ObjectId;

import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import util.api.MyLogger;
import util.api.SongwichAPIException;
import views.api.APIResponse_V0_4;
import views.api.APIStatus_V0_4;
import controllers.api.annotation.UserAuthenticated;
import database.api.scrobbles.UserDAO;
import database.api.scrobbles.UserDAOMongo;

public class UserAuthController extends Action<UserAuthenticated> {

	public final static String USER_AUTH_TOKEN_HEADER = "X-Songwich.userAuthToken";
	public final static String USER_AUTH_TOKEN_HEADER_ALTERNATE = "X-Songwich.userauthtoken";
	public final static String USER = "user";

	@Override
	// public F.Promise<SimpleResult> call(Http.Context context) throws
	// Throwable {
	public Result call(Http.Context context) throws Throwable {
		try {
			authenticateUser(context);
		} catch (SongwichAPIException e) {
			MyLogger.warn(String.format("%s [%s]: %s",
					e.getStatus().toString(), e.getMessage(), context.request()));
			APIResponse_V0_4 response = new APIResponse_V0_4(e.getStatus(),
					e.getMessage());
			// return F.Promise.<SimpleResult>
			// pure(Results.unauthorized(Json.toJson(response)));
			return Results.unauthorized(Json.toJson(response));
		}

		// user successfully authenticated
		return delegate.call(context);
	}

	private void authenticateUser(Http.Context context)
			throws SongwichAPIException {
		String[] userAuthTokenHeaderValues = context.request().headers()
				.get(USER_AUTH_TOKEN_HEADER);
		if (userAuthTokenHeaderValues == null) {
			// we need this on Heroku
			userAuthTokenHeaderValues = context.request().headers()
					.get(USER_AUTH_TOKEN_HEADER_ALTERNATE);
		}

		if ((userAuthTokenHeaderValues != null)
				&& (userAuthTokenHeaderValues[0] != null)) {

			UserDAO<ObjectId> userDAO = new UserDAOMongo();
			User user;
			try {
				// it currently only considers the first userAuthToken
				user = userDAO
						.findByUserAuthToken(userAuthTokenHeaderValues[0]);
			} catch (IllegalArgumentException e) {
				// auth token cannot be converted into a UUID
				throw new SongwichAPIException(
						APIStatus_V0_4.INVALID_USER_AUTH_TOKEN.toString(),
						APIStatus_V0_4.INVALID_USER_AUTH_TOKEN);
			}

			if (user != null) {
				// authentication successful
				context.args.put(USER, user);
			} else {
				// authentication failed
				MyLogger.warn(String.format("%s: %s",
						APIStatus_V0_4.INVALID_USER_AUTH_TOKEN.toString(),
						userAuthTokenHeaderValues[0]));
				throw new SongwichAPIException(
						APIStatus_V0_4.INVALID_USER_AUTH_TOKEN.toString(),
						APIStatus_V0_4.INVALID_USER_AUTH_TOKEN);
			}

		} else {
			throw new SongwichAPIException("X-Songwich.userAuthToken missing",
					APIStatus_V0_4.INVALID_USER_AUTH_TOKEN);
		}
	}
}
