import play.GlobalSettings;
import play.Logger;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;
import controllers.api.util.APIResponse;
import controllers.api.util.Status;

public class Global extends GlobalSettings {

	@Override
	public Result onBadRequest(RequestHeader request, String error) {
		Logger.warn(String.format("Bad request [%s]: %s", error, request));
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
				"API method not found: " + request.method() + " "
						+ request.path());
		return Results.badRequest(response.toJson());
	}

	@Override
	public Result onError(RequestHeader request, Throwable t) {
		Logger.error("Error while processing: " + request.toString());
		APIResponse response = new APIResponse(Status.UNKNOWN_ERROR,
		// get the message of the Exception wrapped inside Play's
		// ExecutionExeption
				t.getCause().getMessage());
		return Results.badRequest(response.toJson());
	}
}