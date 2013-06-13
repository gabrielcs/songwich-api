package controllers.api;

import static controllers.api.util.Status.SUCCESS;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Results;
import controllers.api.proxies.ScrobbleProxyV0_1;
import controllers.api.proxies.ScrobbleProxyV0_2;
import controllers.api.util.APIResponse;
import controllers.api.util.SongwichAPIException;

public class Scrobbler extends Controller {

	public static Result scrobbleV0_2(String user_id, String track_title,
			String artist_name, String service, String timestamp) {
		ScrobbleProxyV0_2 scrobble;
		APIResponse response;

		try {
			scrobble = new ScrobbleProxyV0_2(user_id, track_title, artist_name,
					service, timestamp);
		} catch (SongwichAPIException e) {
			Logger.warn(String.format("%s [%s]: %s", e.getStatus().toString(),
					e.getMessage(), Context.current().request()));
			response = new APIResponse(e.getStatus(), e.getMessage());
			return Results.badRequest(response.toJson());
		}

		response = new APIResponse(SUCCESS, "Success");
		response.put("scrobble", scrobble.toJson());
		return ok(response.toJson());
	}

	public static Result scrobbleV0_1(Long user_id, String track_title,
			String artist_name, String service, Long timestamp) {
		ScrobbleProxyV0_1 scrobble;
		APIResponse response;

		try {
			scrobble = new ScrobbleProxyV0_1(user_id, track_title, artist_name,
					service, timestamp);
		} catch (SongwichAPIException e) {
			response = new APIResponse(e.getStatus(), e.getMessage());
			return Results.badRequest(response.toJson());
		}

		response = new APIResponse(SUCCESS, "Success");
		response.put("scrobble", scrobble.toJson());
		return ok(response.toJson());
	}

	public static Result scrobbleError() {
		throw new RuntimeException("Testing the error page");
	}

}
