import java.net.UnknownHostException;

import play.GlobalSettings;
import play.Logger;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;
import views.api.util.APIResponse;
import views.api.util.Status;

import com.google.code.morphia.Morphia;
import com.mongodb.MongoClient;

public class Global extends GlobalSettings {

	@Override
	public void beforeStart(play.Application app) {
		String dbName;
		if (app.isDev()) {
			dbName = app.configuration().getString("morphia.db.dev.name");
		} else if (app.isTest()) {
			dbName = app.configuration().getString("morphia.db.test.name");
		} else if (app.isProd()) {
			dbName = app.configuration().getString("morphia.db.prod.name");
		} else {
			RuntimeException e = new RuntimeException(
					"App is not set to Dev, Test or Prod");
			Logger.error(e.getMessage());
			throw e;
		}

		try {
			controllers.api.Application.setDatastore(new Morphia()
					.createDatastore(new MongoClient(), dbName));
			Logger.info("Connected to database " + dbName);
		} catch (UnknownHostException e) {
			Logger.error("Couldn't connect to the database: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	@Override
	public Result onBadRequest(RequestHeader request, String error) {
		// it's currently not showing POST requests parameters
		Logger.warn(String.format("Bad request [%s]: %s\n", error, request));
		APIResponse response = new APIResponse(Status.BAD_REQUEST, error);
		return Results.badRequest(response.toJson());
	}

	@Override
	public Result onHandlerNotFound(RequestHeader request) {
		// ignore GET /favicon.ico
		if (request.toString().equals("GET /favicon.ico")) {
			return Results.badRequest();
		}

		Logger.warn("Handler not found: " + request);
		APIResponse response = new APIResponse(Status.METHOD_NOT_FOUND,
				String.format("API method not found: %s %s", request.method(),
						request.path()));
		return Results.badRequest(response.toJson());
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
		Logger.error(String.format("Error while processing: %s [%s]", request,
				message));

		APIResponse response = new APIResponse(Status.UNKNOWN_ERROR, message);
		return Results.badRequest(response.toJson());
	}
}