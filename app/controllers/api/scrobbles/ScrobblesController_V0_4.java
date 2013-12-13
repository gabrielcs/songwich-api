package controllers.api.scrobbles;

import java.util.List;

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
import views.api.scrobbles.GetScrobblesResponse_V0_4;
import views.api.scrobbles.PostScrobblesResponse_V0_4;
import views.api.scrobbles.PutScrobblesResponse_V0_4;
import views.api.scrobbles.ScrobblesDTO_V0_4;
import views.api.scrobbles.ScrobblesUpdateDTO_V0_4;
import behavior.api.usecases.scrobbles.ScrobblesUseCases;
import controllers.api.APIController;
import controllers.api.annotation.AppDeveloperAuthenticated;
import controllers.api.annotation.Logged;
import controllers.api.annotation.UserAuthenticated;

public class ScrobblesController_V0_4 extends APIController {

	@AppDeveloperAuthenticated
	@UserAuthenticated
	@Logged
	public static Result postScrobbles() {
		Form<ScrobblesDTO_V0_4> scrobblesForm = Form.form(
				ScrobblesDTO_V0_4.class).bindFromRequest();
		if (scrobblesForm.hasErrors()) {
			APIResponse_V0_4 apiResponse = new APIResponse_V0_4(
					APIStatus_V0_4.INVALID_PARAMETER,
					DTOValidator.errorsAsString(scrobblesForm.errors()));
			return badRequest(Json.toJson(apiResponse));
		} else {
			ScrobblesDTO_V0_4 scrobbleDTO = scrobblesForm.get();

			// process the request
			ScrobblesUseCases scrobblesUseCases = new ScrobblesUseCases(
					getContext());
			try {
				scrobblesUseCases.postScrobbles(scrobbleDTO);
			} catch (SongwichAPIException exception) {
				// Missing X-Songwich.userAuthToken
				MyLogger.warn(String.format("%s [%s]: %s", exception
						.getStatus().toString(), exception.getMessage(),
						Http.Context.current().request()));
				APIResponse_V0_4 response = new APIResponse_V0_4(
						exception.getStatus(), exception.getMessage());
				return Results.unauthorized(Json.toJson(response));
			}

			// return the response
			PostScrobblesResponse_V0_4 response = new PostScrobblesResponse_V0_4(
					APIStatus_V0_4.SUCCESS, "Success", scrobbleDTO);
			return ok(Json.toJson(response));
		}
	}

	@AppDeveloperAuthenticated
	@UserAuthenticated
	@Logged
	public static Result putScrobbles(String scrobbleId) {
		Form<ScrobblesUpdateDTO_V0_4> scrobblesUpdateForm = Form.form(
				ScrobblesUpdateDTO_V0_4.class).bindFromRequest();
		if (scrobblesUpdateForm.hasErrors()) {
			APIResponse_V0_4 apiResponse = new APIResponse_V0_4(
					APIStatus_V0_4.INVALID_PARAMETER,
					DTOValidator.errorsAsString(scrobblesUpdateForm.errors()));
			return badRequest(Json.toJson(apiResponse));
		} else {
			ScrobblesUpdateDTO_V0_4 scrobblesUpdateDTO = scrobblesUpdateForm
					.get();

			// process the request
			ScrobblesUseCases scrobblesUseCases = new ScrobblesUseCases(
					getContext());
			try {
				scrobblesUseCases.putScrobbles(scrobbleId, scrobblesUpdateDTO);
			} catch (SongwichAPIException exception) {
				// Missing X-Songwich.userAuthToken
				MyLogger.warn(String.format("%s [%s]: %s", exception
						.getStatus().toString(), exception.getMessage(),
						Http.Context.current().request()));
				APIResponse_V0_4 response = new APIResponse_V0_4(
						exception.getStatus(), exception.getMessage());
				return Results.unauthorized(Json.toJson(response));
			}

			// return the response
			PutScrobblesResponse_V0_4 response = new PutScrobblesResponse_V0_4(
					APIStatus_V0_4.SUCCESS, "Success", scrobblesUpdateDTO);
			return ok(Json.toJson(response));
		}
	}

	@AppDeveloperAuthenticated
	@UserAuthenticated
	@Logged
	public static Result deleteScrobbles(String scrobbleId) {
		ScrobblesUseCases scrobblesUseCases = new ScrobblesUseCases(
				getContext());
		try {
			scrobblesUseCases.deleteScrobbles(scrobbleId);
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

	@AppDeveloperAuthenticated
	@UserAuthenticated
	@Logged
	public static Result getScrobbles(String userId, int daysOffset,
			int results, boolean chosenByUserOnly) {
		
		ScrobblesUseCases scrobblesUseCases = new ScrobblesUseCases(
				getContext());
		List<ScrobblesDTO_V0_4> scrobbleDTOs;
		try {
			if (daysOffset < 0) {
				if (results < 0) {
					scrobbleDTOs = scrobblesUseCases.getScrobbles(userId,
							chosenByUserOnly);
				} else {
					scrobbleDTOs = scrobblesUseCases.getScrobbles(userId,
							results, chosenByUserOnly);
				}
			} else {
				if (results < 0) {
					scrobbleDTOs = scrobblesUseCases.getScrobblesDaysOffset(
							userId, daysOffset, chosenByUserOnly);
				} else {
					scrobbleDTOs = scrobblesUseCases.getScrobblesDaysOffset(
							userId, daysOffset, results, chosenByUserOnly);
				}
			}
		} catch (SongwichAPIException exception) {
			// user unauthorized for getting scrobbles from another user
			MyLogger.warn(String.format("%s [%s]: %s", exception.getStatus()
					.toString(), exception.getMessage(), Http.Context.current()
					.request()));
			APIResponse_V0_4 response = new APIResponse_V0_4(
					exception.getStatus(), exception.getMessage());
			return Results.unauthorized(Json.toJson(response));
		}

		// return the response
		GetScrobblesResponse_V0_4 response = new GetScrobblesResponse_V0_4(
				APIStatus_V0_4.SUCCESS, "Success", scrobbleDTOs);
		return ok(Json.toJson(response));
	}

	/*
	 * public static Result postFixScrobbles() { String devEmail =
	 * "gabrielcs@gmail.com";
	 * 
	 * ObjectId oldId = new ObjectId("525db27d92e69e28dca31ca6"); ObjectId newId
	 * = new ObjectId("5272bf5ce4b065f24467b51d");
	 * 
	 * ScrobbleDAO<ObjectId> scrobbleDAO = new ScrobbleDAOMongo();
	 * List<Scrobble> scrobbles = scrobbleDAO.find().asList(); for (Scrobble
	 * scrobble : scrobbles) { if (scrobble.getUserId().equals(oldId)) {
	 * scrobble.setUserId(newId); scrobbleDAO.save(scrobble, devEmail); } }
	 * 
	 * return Results.ok(); }
	 */
}
