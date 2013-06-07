package controllers.api;

import play.mvc.Controller;
import play.mvc.Result;

public class Scrobbler extends Controller {
	
	public static Result scrobble(String userId, String trackName, String artistName, String service) {
		StringBuilder result = new StringBuilder("Scrobbling track...\n")
			.append("user_id = " + userId + "\n")
		    .append("track_name = " + trackName + "\n")
		    .append("artist_name = " + artistName + "\n")
		    .append("service = " + service + "\n");
		return ok(result.toString());
	}
}
