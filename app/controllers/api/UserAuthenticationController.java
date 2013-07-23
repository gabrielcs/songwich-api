package controllers.api;

import java.util.UUID;

import models.User;

import org.bson.types.ObjectId;

import play.Logger;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import views.api.util.APIResponse_V0_4;
import views.api.util.APIStatus_V0_4;
import controllers.api.util.SongwichAPIException;
import daos.api.UserDAO;
import daos.api.UserDAOMongo;

public class UserAuthenticationController extends Action.Simple {

	public final static String USER_AUTH_TOKEN_HEADER = "X-Songwich.userAuthToken";
	public final static String USER = "user";

	public Result call(Http.Context ctx) throws Throwable {
		try {
			authenticateUser(ctx);
		} catch (SongwichAPIException e) {
			Logger.warn(String.format("%s [%s]: %s", e.getStatus().toString(),
					e.getMessage(), ctx.request()));
			APIResponse_V0_4 response = new APIResponse_V0_4(e.getStatus(), e.getMessage());
			return Results.unauthorized(Json.toJson(response));
		}

		// user successfully authenticated 
		return delegate.call(ctx);
	}

	private static void authenticateUser(Http.Context ctx)
			throws SongwichAPIException {

		String[] userAuthTokenHeaderValues = ctx.request().headers()
				.get(USER_AUTH_TOKEN_HEADER);
		if ((userAuthTokenHeaderValues != null)
				&& (userAuthTokenHeaderValues.length == 1)
				&& (userAuthTokenHeaderValues[0] != null)) {
			UserDAO<ObjectId> userDAO = new UserDAOMongo(
					DatabaseController.getDatastore());
			User user = userDAO.findByUserAuthToken(UUID
					.fromString(userAuthTokenHeaderValues[0]));
			if (user != null) {
				ctx.args.put(USER, user);
			} else {
				Logger.warn(String.format("%s: %s",
						APIStatus_V0_4.INVALID_USER_AUTH_TOKEN.toString(),
						userAuthTokenHeaderValues[0]));
				throw new SongwichAPIException(
						APIStatus_V0_4.INVALID_USER_AUTH_TOKEN.toString(),
						APIStatus_V0_4.INVALID_USER_AUTH_TOKEN);
			}
		} else {
			Logger.warn(String.format("%s: %s",
					APIStatus_V0_4.INVALID_USER_AUTH_TOKEN.toString(),
					userAuthTokenHeaderValues));
			throw new SongwichAPIException(
					APIStatus_V0_4.INVALID_USER_AUTH_TOKEN.toString(),
					APIStatus_V0_4.INVALID_USER_AUTH_TOKEN);
		}
	}

	public static User getUser() {
		return (User) Http.Context.current().args.get(USER);
	}

	public static UUID createUserAuthToken() {
		UUID userAuthToken = UUID.randomUUID();
		// assert that the random UUID is unique (might be expensive)
		UserDAO<ObjectId> userDAO = new UserDAOMongo(
				DatabaseController.getDatastore());
		User user = userDAO.findByUserAuthToken(userAuthToken);
		if (user == null) {
			return userAuthToken;
		} else {
			return createUserAuthToken();
		}
	}
}
