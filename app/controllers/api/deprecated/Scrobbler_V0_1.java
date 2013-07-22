package controllers.api.deprecated;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import views.api.deprecated.ScrobbleProxy_V0_1;
import views.api.util.deprecated.APIResponse_V0_1;
import controllers.api.util.SongwichAPIException;

public class Scrobbler_V0_1 extends Controller {

	public static Result scrobble(Long user_id, String track_title,
			String artist_name, String service, Long timestamp) {
		ScrobbleProxy_V0_1 scrobble;
		APIResponse_V0_1 response;

		try {
			scrobble = new ScrobbleProxy_V0_1(user_id, track_title,
					artist_name, service, timestamp);
		} catch (SongwichAPIException e) {
			response = new APIResponse_V0_1(e.getStatus(), e.getMessage());
			return Results.badRequest(response.toJson());
		}

		response = new APIResponse_V0_1(views.api.util.Status.SUCCESS,
				"Success");
		response.put("scrobble", scrobble.toJson());
		return ok(response.toJson());
	}
}
