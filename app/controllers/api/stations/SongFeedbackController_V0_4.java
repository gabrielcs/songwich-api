package controllers.api.stations;

import play.data.Form;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import util.api.MyLogger;
import util.api.SongwichAPIException;
import views.api.APIResponse_V0_4;
import views.api.APIStatus_V0_4;
import views.api.DTOValidator;
import views.api.stations.GetIsSongStarredResponse_V0_4;
import views.api.stations.GetStarredSongsResponse_V0_4;
import views.api.stations.IsSongStarredDTO_V0_4;
import views.api.stations.PostSongFeedback_V0_4;
import views.api.stations.SongFeedbackDTO_V0_4;
import views.api.stations.StarredSongSetDTO_V0_4;
import behavior.api.usecases.stations.SongFeedbackUseCases;
import controllers.api.APIController;
import controllers.api.annotation.AppDeveloperAuthenticated;
import controllers.api.annotation.UserAuthenticated;

public class SongFeedbackController_V0_4 extends APIController {

	@AppDeveloperAuthenticated
	@UserAuthenticated
	public static Result postSongFeedback() {
		Form<SongFeedbackDTO_V0_4> songFeedbackForm = Form.form(
				SongFeedbackDTO_V0_4.class).bindFromRequest();
		if (songFeedbackForm.hasErrors()) {
			APIResponse_V0_4 apiResponse = new APIResponse_V0_4(
					APIStatus_V0_4.INVALID_PARAMETER,
					DTOValidator.errorsAsString(songFeedbackForm.errors()));
			return badRequest(Json.toJson(apiResponse));
		} else {
			SongFeedbackDTO_V0_4 songFeedbackDTO = songFeedbackForm.get();

			// process the request
			SongFeedbackUseCases songFeedbackUseCases = new SongFeedbackUseCases(
					getContext());
			try {
				songFeedbackUseCases.postSongFeedback(songFeedbackDTO);
			} catch (SongwichAPIException exception) {
				MyLogger.warn(String.format("%s [%s]: %s", exception
						.getStatus().toString(), exception.getMessage(),
						Http.Context.current().request()));
				APIResponse_V0_4 response = new APIResponse_V0_4(
						exception.getStatus(), exception.getMessage());
				if (exception.getStatus().equals(APIStatus_V0_4.UNAUTHORIZED)) {
					return Results.unauthorized(Json.toJson(response));
				} else {
					return Results.badRequest(Json.toJson(response));
				}
			}

			// return the response
			PostSongFeedback_V0_4 response = new PostSongFeedback_V0_4(
					APIStatus_V0_4.SUCCESS, "Success", songFeedbackDTO);
			return ok(Json.toJson(response));
		}
	}

	@AppDeveloperAuthenticated
	// TODO: decide if it should be @UserAuthenticated
	public static Result getStarredSongs(String userId) {
		SongFeedbackUseCases songFeedbackUseCases = new SongFeedbackUseCases(
				getContext());
		StarredSongSetDTO_V0_4 starredSongSetDTO;
		try {
			starredSongSetDTO = songFeedbackUseCases.getStarredSongs(userId);
		} catch (SongwichAPIException exception) {
			MyLogger.warn(String.format("%s [%s]: %s", exception.getStatus()
					.toString(), exception.getMessage(), Http.Context.current()
					.request()));
			APIResponse_V0_4 response = new APIResponse_V0_4(
					exception.getStatus(), exception.getMessage());
			return Results.badRequest(Json.toJson(response));
		}

		// return the response
		GetStarredSongsResponse_V0_4 response = new GetStarredSongsResponse_V0_4(
				APIStatus_V0_4.SUCCESS, "Success", starredSongSetDTO);
		return ok(Json.toJson(response));
	}

	@AppDeveloperAuthenticated
	// TODO: decide if it should be @UserAuthenticated
	public static Result getIsSongStarred(String userId, String songTitle,
			String artistsNames, String albumTitle) {

		SongFeedbackUseCases songFeedbackUseCases = new SongFeedbackUseCases(
				getContext());
		IsSongStarredDTO_V0_4 isSongStarredDTO;
		try {
			isSongStarredDTO = songFeedbackUseCases.getIsSongStarred(userId,
					songTitle, artistsNames, albumTitle);
		} catch (SongwichAPIException exception) {
			MyLogger.warn(String.format("%s [%s]: %s", exception.getStatus()
					.toString(), exception.getMessage(), Http.Context.current()
					.request()));
			APIResponse_V0_4 response = new APIResponse_V0_4(
					exception.getStatus(), exception.getMessage());
			return Results.badRequest(Json.toJson(response));
		}

		// return the response
		GetIsSongStarredResponse_V0_4 response = new GetIsSongStarredResponse_V0_4(
				APIStatus_V0_4.SUCCESS, "Success", isSongStarredDTO);
		return ok(Json.toJson(response));
	}

	@AppDeveloperAuthenticated
	@UserAuthenticated
	public static Result deleteSongFeedback(String idForFeedback,
			String feedbackType) {
		SongFeedbackUseCases songFeedbackUseCases = new SongFeedbackUseCases(
				getContext());
		try {
			songFeedbackUseCases
					.deleteSongFeedback(idForFeedback, feedbackType);
		} catch (SongwichAPIException exception) {
			MyLogger.warn(String.format("%s [%s]: %s", exception.getStatus()
					.toString(), exception.getMessage(), Http.Context.current()
					.request()));
			APIResponse_V0_4 response = new APIResponse_V0_4(
					exception.getStatus(), exception.getMessage());
			if (exception.getStatus().equals(APIStatus_V0_4.UNAUTHORIZED)) {
				return Results.unauthorized(Json.toJson(response));
			} else {
				return Results.badRequest(Json.toJson(response));
			}

		}

		// return the response
		APIResponse_V0_4 response = new APIResponse_V0_4(
				APIStatus_V0_4.SUCCESS, "Success");
		return ok(Json.toJson(response));
	}

}
