import play.GlobalSettings;
import play.Logger;
import play.mvc.Http.Context;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;
import controllers.api.util.APIResponse;
import controllers.api.util.Status;

public class Global extends GlobalSettings {

	@Override
	public Result onBadRequest(RequestHeader request, String error) {
		// TODO: re-write logger for POST requests
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
		// TODO: re-write logger for showing POST data
		Logger.error(String.format("Error while processing: %s [%s]", request,
				message));

		APIResponse response = new APIResponse(Status.UNKNOWN_ERROR, message);
		return Results.badRequest(response.toJson());
	}
}