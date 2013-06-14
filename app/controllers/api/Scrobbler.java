package controllers.api;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.Http.MultipartFormData;
import play.mvc.Result;
import play.mvc.Results;
import controllers.api.proxies.ScrobbleProxyV0_1;
import controllers.api.proxies.ScrobbleProxyV0_2;
import controllers.api.proxies.ScrobbleProxyV0_3;
import controllers.api.util.APIResponse;
import controllers.api.util.SongwichAPIException;

public class Scrobbler extends Controller {

	public static Result scrobbleV0_3() {
		ScrobbleProxyV0_3 scrobble;
		APIResponse response;
		try {
			// get POST data
			Map<String, String> data = PostDataReader.readData(false);
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
		response = new APIResponse(controllers.api.util.Status.SUCCESS,
				"Success");
		response.put("scrobble", scrobble.toJson());
		return ok(response.toJson());
	}

	private static class PostDataReader {

		private static Map<String, String> readData(boolean ignoreUnexpectedData)
				throws SongwichAPIException {

			Map<String, String> data = new HashMap<String, String>();

			// check if there's data to extract
			MultipartFormData multipartFormData = Context.current().request()
					.body().asMultipartFormData();
			if (multipartFormData == null) {
				return data; // returns an empty map
			}

			// discover what data has to be extracted
			List<Field> fields = Arrays.asList(ScrobbleProxyV0_3.class
					.getDeclaredFields());
			List<String> fieldNames = new ArrayList<String>(fields.size());
			for (Field field : fields) {
				fieldNames.add(field.getName());
			}

			// reference the data to be extracted
			Map<String, String[]> formUrlData = multipartFormData
					.asFormUrlEncoded();

			// check if there's unexpected data
			if (!ignoreUnexpectedData) {
				Set<String> formUrlDataKeySet = formUrlData.keySet();
				for (String key : formUrlDataKeySet) {
					if (!fieldNames.contains(key)) {
						throw new SongwichAPIException("Unexpected data: "
								+ key, controllers.api.util.Status.BAD_REQUEST);

					}
				}
			}

			// extract data
			String[] dataArray;
			for (String fieldName : fieldNames) {
				dataArray = formUrlData.get(fieldName);
				if (dataArray == null) {
					data.put(fieldName, null);
				} else if (dataArray.length != 1) {
					throw new SongwichAPIException("Unexpected multiple data: "
							+ fieldName,
							controllers.api.util.Status.BAD_REQUEST);
				} else {
					data.put(fieldName, dataArray[0]);
				}
			}
			return data;
		}
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

		response = new APIResponse(controllers.api.util.Status.SUCCESS,
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

		response = new APIResponse(controllers.api.util.Status.SUCCESS,
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
