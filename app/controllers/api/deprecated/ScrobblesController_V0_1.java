package controllers.api.deprecated;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import usecases.api.util.SongwichAPIException;
import views.api.deprecated.ScrobbleProxy_V0_1;
import views.api.util.deprecated.APIResponse_V0_1;
import views.api.util.deprecated.APIStatus_V0_1;

@Deprecated
public class ScrobblesController_V0_1 extends Controller {

	public static Result getScrobble(Long user_id, String track_title,
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

		response = new APIResponse_V0_1(APIStatus_V0_1.SUCCESS,
				"Success");
		response.put("scrobble", scrobble.toJson());
		return ok(response.toJson());
	}
}
