package controllers.api;

import java.util.Map;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Results;
import views.api.deprecated.ScrobbleProxyV0_1;
import views.api.deprecated.ScrobbleProxyV0_2;
import views.api.deprecated.ScrobbleProxyV0_3;
import views.api.util.APIResponse;
import views.api.util.Status;
import controllers.api.util.PostRequestBodyParser;
import controllers.api.util.SongwichAPIException;

public class Scrobbler extends Controller {
	
	public static Result scrobbleV0_4() {
		return TODO;
	}

	public static Result scrobbleV0_3() {
		ScrobbleProxyV0_3 scrobble;
		APIResponse response;
		try {
			// get POST data
			Map<String, String> data = PostRequestBodyParser.parse(
					ScrobbleProxyV0_3.class, false);
			// try to create a scrobble
			scrobble = new ScrobbleProxyV0_3(data.get("user_id"),
					data.get("track_title"), data.get("artist_name"),
					data.get("service"), data.get("timestamp"));
		} catch (SongwichAPIException e) {
			Logger.warn(String.format("%s [%s]: %s", e.getStatus().toString(),
					e.getMessage(), Context.current().request()));
			response = new APIResponse(e.getStatus(), e.getMessage());
			return Results.badRequest(response.toJson());
		}

		// scrobble successful
		response = new APIResponse(views.api.util.Status.SUCCESS,
				"Success");
		response.put("scrobble", scrobble.toJson());
		return ok(response.toJson());
	}

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

		response = new APIResponse(views.api.util.Status.SUCCESS,
				"Success");
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

		response = new APIResponse(views.api.util.Status.SUCCESS,
				"Success");
		response.put("scrobble", scrobble.toJson());
		return ok(response.toJson());
	}

	public static Result scrobbleErrorGet() {
		throw new RuntimeException("Testing the error page");
	}

	public static Result scrobbleErrorPost() {
		throw new RuntimeException("Testing the error page");
	}
}
