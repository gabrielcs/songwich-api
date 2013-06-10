package controllers.api;

import static controllers.api.util.Status.SUCCESS;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import controllers.api.proxies.ScrobbleProxy;
import controllers.api.util.Response;
import controllers.api.util.SongwichAPIException;

public class Scrobbler extends Controller {

	public static Result scrobble(Long user_id, String track_title,
			String artist_name, String service, Long timestamp) {

		ScrobbleProxy scrobble;
		Response response;

		try {
			scrobble = new ScrobbleProxy(user_id, track_title, artist_name, service,
					timestamp);
		} catch (SongwichAPIException e) {
			response = new Response(e.getStatus(), e.getMessage());
			return Results.badRequest(response.toJson());
		}

		response = new Response(SUCCESS, "Success");
		response.put("scrobble", scrobble.toJson());
		return ok(response.toJson());
	}

	public static Result scrobbleError(Long user_id, String track_title,
			String artist_name, String service, Long timestamp) {

		throw new RuntimeException("Testing the error page");
	}

}
