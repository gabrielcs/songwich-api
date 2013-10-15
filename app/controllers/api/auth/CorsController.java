package controllers.api.auth;

import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.Response;
import play.mvc.Result;
import play.mvc.Results;

// http://daniel.reuterwall.com/blog/2013/04/15/play-with-cors
public class CorsController extends Action.Simple {
	@Override
	// public F.Promise<SimpleResult> call(Http.Context context) throws
	// Throwable {
	public Result call(Http.Context context) throws Throwable {
		allowCustomHeaders();
		return delegate.call(context);
	}

	public static Result allowCustomHeaders(String path) throws Throwable {
		allowCustomHeaders();
		return Results.ok();
	}

	private static void allowCustomHeaders() {
		Response response = Http.Context.current().response();
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods",
				"POST, GET, PUT, DELETE, OPTIONS, HEAD");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader(
				"Access-Control-Allow-Headers",
				"X-Songwich.devAuthToken, X-Songwich.userAuthToken, Content-Type, Origin, Accept");
	}
}
