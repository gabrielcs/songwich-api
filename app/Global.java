import play.GlobalSettings;
import play.libs.Json;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;
import usecases.api.util.DatabaseContext;
import usecases.api.util.MyLogger;
import controllers.api.AppDeveloperAuthController;
import controllers.api.util.APIResponse_V0_4;
import controllers.api.util.APIStatus_V0_4;

public class Global extends GlobalSettings {
	@Override
	public void onStart(play.Application app) {
		if (app.isProd()) {
			// connects to a MongodDB-as-a-Service database
			// it may be a production or a staging database according to the
			// environment variable
			String dbName = System.getenv("MONGOHQ_DBNAME");
			String uri = System.getenv("MONGOHQ_URL");
			DatabaseContext.createDatastore(uri, dbName);
		}

		if (app.isDev()) {
			// starts with a clean local database if in development mode
			String dbName = app.configuration().getString("mongo.local.dbname");
			DatabaseContext.createDatastore(dbName);
			DatabaseContext.dropDatabase();

			// and creates a test developer
			AppDeveloperAuthController.createTestAppWithDeveloper(app
					.configuration().getString("dev.auth.token"));
		}
	}

	@Override
	public Result onBadRequest(RequestHeader request, String error) {
		MyLogger.warn(String.format("Bad request [%s]: %s\n", error, request));
		APIResponse_V0_4 response = new APIResponse_V0_4(
				APIStatus_V0_4.BAD_REQUEST, error);
		return Results.badRequest(Json.toJson(response));
	}

	@Override
	public Result onHandlerNotFound(RequestHeader request) {
		// ignore GET /favicon.ico
		if (request.toString().equals("GET /favicon.ico")) {
			return Results.badRequest();
		}

		MyLogger.warn("Handler not found: " + request);
		APIResponse_V0_4 response = new APIResponse_V0_4(
				APIStatus_V0_4.BAD_REQUEST, String.format(
						"API method not found: %s %s", request.method(),
						request.path()));
		return Results.badRequest(Json.toJson(response));
	}

	@Override
	public Result onError(RequestHeader request, Throwable t) {
		// get the message of the Exception wrapped inside Play's
		// ExecutionExeption
		String message = t.getCause().getClass().getSimpleName();
		if (t.getCause().getMessage() != null) {
			message = String.format("%s: %s", message, t.getCause()
					.getMessage());
		}
		// it's currently not showing POST requests parameters
		MyLogger.error(String.format("Error while processing: %s [%s]",
				request, message));

		APIResponse_V0_4 response = new APIResponse_V0_4(
				APIStatus_V0_4.UNKNOWN_ERROR, message);
		return Results.badRequest(Json.toJson(response));
	}
}