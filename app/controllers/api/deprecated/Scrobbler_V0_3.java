package controllers.api.deprecated;

import java.util.Map;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Results;
import views.api.deprecated.ScrobbleProxy_V0_3;
import views.api.util.deprecated.APIResponse_V0_1;
import controllers.api.util.PostRequestBodyParser;
import controllers.api.util.SongwichAPIException;

public class Scrobbler_V0_3 extends Controller {

	public static Result scrobble() {
		ScrobbleProxy_V0_3 scrobble;
		APIResponse_V0_1 response;
		try {
			// get POST data
			Map<String, String> data = PostRequestBodyParser.parse(
					ScrobbleProxy_V0_3.class, false);
			// try to create a scrobble
			scrobble = new ScrobbleProxy_V0_3(data.get("user_id"),
					data.get("track_title"), data.get("artist_name"),
					data.get("service"), data.get("timestamp"));
		} catch (SongwichAPIException e) {
			Logger.warn(String.format("%s [%s]: %s", e.getStatus().toString(),
					e.getMessage(), Context.current().request()));
			response = new APIResponse_V0_1(e.getStatus(), e.getMessage());
			return Results.badRequest(response.toJson());
		}

		// scrobble successful
		response = new APIResponse_V0_1(views.api.util.Status.SUCCESS,
				"Success");
		response.put("scrobble", scrobble.toJson());
		return ok(response.toJson());
	}
}