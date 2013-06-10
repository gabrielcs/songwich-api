import play.GlobalSettings;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;
import controllers.api.util.Response;
import controllers.api.util.Status;

public class Global extends GlobalSettings {

	@Override
	public Result onBadRequest(RequestHeader request, String error) {
		Response response = new Response(Status.BAD_REQUEST, error);
		return Results.badRequest(response.toJson());
	}

	@Override
	public Result onHandlerNotFound(RequestHeader request) {
		Response response = new Response(Status.METHOD_NOT_FOUND,
				"API method not found: " + request.path());
		return Results.badRequest(response.toJson());
	}

	@Override
	public Result onError(RequestHeader request, Throwable t) {
		Response response = new Response(Status.UNKNOWN_ERROR,
		// get the message of the Exception wrapped inside Play's ExecutionExeption
				t.getCause().getMessage());
		return Results.badRequest(response.toJson());
	}

}