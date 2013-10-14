package controllers.api;

import models.api.scrobbles.App;
import models.api.scrobbles.AppDeveloper;
import models.api.scrobbles.User;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.Response;
import play.mvc.Result;
import play.mvc.Results;
import util.api.MyLogger;
import behavior.api.usecases.RequestContext;
import controllers.api.auth.AppDeveloperAuthController;
import controllers.api.auth.UserAuthController;

public class APIController extends Controller {

	protected static RequestContext getContext() {
		return new RequestContext(
				(App) Http.Context.current().args
						.get(AppDeveloperAuthController.APP),
				(AppDeveloper) Http.Context.current().args
						.get(AppDeveloperAuthController.DEV),
				(User) Http.Context.current().args.get(UserAuthController.USER));
	}

	public static Result cors(String path) {
		MyLogger.debug("inside Cors");
		
		Response response = Http.Context.current().response();
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods",
				"POST, GET, PUT, DELETE, OPTIONS");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader(
				"Access-Control-Allow-Headers",
				"X-Songwich.devAuthToken, X-Songwich.userAuthToken, Content-Type, Origin, Accept");

		return Results.ok();
	}
}
