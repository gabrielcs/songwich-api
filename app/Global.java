import java.util.UUID;

import play.GlobalSettings;
import play.Logger;
import play.libs.Json;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;
import usecases.api.util.DatabaseContext;

import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;

import controllers.api.AppDeveloperAuthController;
import dtos.api.util.APIResponse_V0_4;
import dtos.api.util.APIStatus_V0_4;

public class Global extends GlobalSettings {

	/*
	 * This is necessary, otherwise we get an IllegalArgumentException:
	 * "can't parse argument number interface com.google.code.morphia.annotations.Id"
	 * 
	 * http://chepurnoy.org/blog/2013/03/play2-plus-morphia-how-to-avoid-cant-parse
	 * -argument-number-interface-error/
	 */
	static {
		MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);
	}

	@Override
	public void beforeStart(play.Application app) {
		if (app.isProd()) {
			// connects to our Mongo-as-a-Service database
			String dbName = app.configuration().getString("mongo.name");
			String uri = app.configuration().getString("mongo.uri");
			DatabaseContext.createDatastore(uri, dbName);
		}

		if (app.isDev()) {
			// starts with a clean local database if in development mode
			// String dbName = app.configuration().getString("mongo.dev.name");
			// DatabaseContext.createDatastore(dbName);
			// DatabaseContext.dropDatabase();
			
			String dbName = app.configuration().getString("mongo.name");
			String uri = app.configuration().getString("mongo.uri");
			DatabaseContext.createDatastore(uri, dbName);
		}
	}

	@Override
	public Result onBadRequest(RequestHeader request, String error) {
		// it's currently not showing POST requests parameters
		Logger.warn(String.format("Bad request [%s]: %s\n", error, request));
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

		Logger.warn("Handler not found: " + request);
		APIResponse_V0_4 response = new APIResponse_V0_4(
				APIStatus_V0_4.METHOD_NOT_FOUND, String.format(
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
		Logger.error(String.format("Error while processing: %s [%s]", request,
				message));

		APIResponse_V0_4 response = new APIResponse_V0_4(
				APIStatus_V0_4.UNKNOWN_ERROR, message);
		return Results.badRequest(Json.toJson(response));
	}
}