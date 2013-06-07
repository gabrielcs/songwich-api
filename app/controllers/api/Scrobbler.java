package controllers.api;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import api.ScrobbleProxy;

public class Scrobbler extends Controller {

	public static Result scrobble(String user_id,
			String track_title, String artist_name,
			String service) {

		ScrobbleProxy scrobble = new ScrobbleProxy(user_id, track_title,
				artist_name, service);

		ObjectNode result = Json.newObject();
		if (scrobble.user_id == null) {
			result.put("status", "1");
			result.put("message", "Missing parameter [user_id]");
			return badRequest(result);
		} else if (scrobble.track_title == null) {
			result.put("status", "1");
			result.put("message", "Missing parameter [track_title]");
			return badRequest(result);
		}  else if (scrobble.artist_name == null) {
			result.put("status", "1");
			result.put("message", "Missing parameter [artist_name]");
			return badRequest(result);
		} 
		
		result.put("status", "0");
		result.put("message", "Success");
		//result.putPOJO("scrobble", scrobble);
		return ok(result);
	}
}
