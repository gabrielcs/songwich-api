package controllers.api;

import java.util.UUID;

import models.User;

import org.bson.types.ObjectId;

import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import daos.api.UserDAO;
import daos.api.UserDAOMongo;

public class AuthenticationController extends Action.Simple {

	public final static String USER_AUTH_TOKEN_HEADER = "X-Songwich.userAuthToken";

	public Result call(Http.Context ctx) throws Throwable {
		User user = null;
		String[] userAuthTokenHeaderValues = ctx.request().headers()
				.get(USER_AUTH_TOKEN_HEADER);
		if ((userAuthTokenHeaderValues != null)
				&& (userAuthTokenHeaderValues.length == 1)
				&& (userAuthTokenHeaderValues[0] != null)) {
			// user = models.User.findByAuthToken(userAuthTokenHeaderValues[0]);
			UserDAO<ObjectId> userDAO = new UserDAOMongo(
					DatabaseController.getDatastore());
			user = userDAO.findByUserAuthToken(UUID
					.fromString(userAuthTokenHeaderValues[0]));
			if (user != null) {
				ctx.args.put("user", user);
				return delegate.call(ctx);
			}
		}

		return unauthorized("unauthorized");
	}
	
	public static User getUser() {
        return (User) Http.Context.current().args.get("user");
    }
}
