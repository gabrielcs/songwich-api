package controllers.api.auth;

import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.Response;
import play.mvc.Result;
import play.mvc.Results;
import util.api.MyLogger;

public class CorsController extends Action.Simple {
	@Override
	// public F.Promise<SimpleResult> call(Http.Context context) throws
	// Throwable {
	public Result call(Http.Context context) throws Throwable {
		MyLogger.debug("Inside CorsController.call()");

		allowHeaders();
		return delegate.call(context);
	}

	public static Result allowHeaders(String path) throws Throwable {
		MyLogger.debug("Inside CorsController.allowHeaders()");
		allowHeaders();
		return Results.ok();
	}

	private static void allowHeaders() {
		Response response = Http.Context.current().response();
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods",
				"POST, GET, PUT, DELETE, OPTIONS");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader(
				"Access-Control-Allow-Headers",
				"X-Songwich.devAuthToken, X-Songwich.userAuthToken, Content-Type, Origin, Accept");
	}
}
