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
import views.api.DataTransferObject;
import views.api.scrobbles.GetScrobblesResponse_V0_4;
import views.api.scrobbles.PostScrobblesResponse_V0_4;
import views.api.scrobbles.PutScrobblesResponse_V0_4;
import views.api.scrobbles.ScrobblesDTO_V0_4;
import views.api.scrobbles.ScrobblesUpdateDTO_V0_4;
import behavior.api.usecases.scrobbles.ScrobblesUseCases;
import controllers.api.APIController;
import controllers.api.annotation.AppDeveloperAuthenticated;
import controllers.api.annotation.UserAuthenticated;

public class ScrobblesController_V0_4 extends APIController {

	@AppDeveloperAuthenticated
	@UserAuthenticated
	public static Result postScrobbles() {
		Form<ScrobblesDTO_V0_4> scrobblesForm = Form.form(
				ScrobblesDTO_V0_4.class).bindFromRequest();
		if (scrobblesForm.hasErrors()) {
			APIResponse_V0_4 apiResponse = new APIResponse_V0_4(
					APIStatus_V0_4.INVALID_PARAMETER,
					DataTransferObject.errorsAsString(scrobblesForm.errors()));
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
	public static Result putScrobbles(String scrobbleId) {
		Form<ScrobblesUpdateDTO_V0_4> scrobblesUpdateForm = Form.form(
				ScrobblesUpdateDTO_V0_4.class).bindFromRequest();
		if (scrobblesUpdateForm.hasErrors()) {
			APIResponse_V0_4 apiResponse = new APIResponse_V0_4(
					APIStatus_V0_4.INVALID_PARAMETER,
					DataTransferObject.errorsAsString(scrobblesUpdateForm
							.errors()));
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
	public static Result deleteScrobbles(String scrobbleId) {
		ScrobblesUseCases scrobblesUseCases = new ScrobblesUseCases(
				getContext());
		try {
			scrobblesUseCases.deleteScrobbles(scrobbleId);
		} catch (SongwichAPIException exception) {
			// user unauthorized for getting scrobbles from another user
			MyLogger.warn(String.format("%s [%s]: %s", exception.getStatus()
					.toString(), exception.getMessage(), Http.Context.current()
					.request()));
			APIResponse_V0_4 response = new APIResponse_V0_4(
					exception.getStatus(), exception.getMessage());
			return Results.badRequest(Json.toJson(response));
		}

		// return the response
		APIResponse_V0_4 response = new APIResponse_V0_4(
				APIStatus_V0_4.SUCCESS, "Success");
		return ok(Json.toJson(response));
	}

	@AppDeveloperAuthenticated
	@UserAuthenticated
	public static Result getScrobbles(String userId, int daysOffset, int results) {
		ScrobblesUseCases scrobblesUseCases = new ScrobblesUseCases(
				getContext());
		List<ScrobblesDTO_V0_4> scrobbleDTOs;
		try {
			if (daysOffset < 0) {
				if (results < 0) {
					scrobbleDTOs = scrobblesUseCases.getScrobbles(userId);
				} else {
					scrobbleDTOs = scrobblesUseCases.getScrobbles(userId,
							results);
				}
			} else {
				if (results < 0) {
					scrobbleDTOs = scrobblesUseCases.getScrobblesDaysOffset(
							userId, daysOffset);
				} else {
					scrobbleDTOs = scrobblesUseCases.getScrobblesDaysOffset(
							userId, daysOffset, results);
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
		MyLogger.info(String.format(
				"GET scrobbles processed for user=%s by devAuthToken=%s",
				getContext().getUser().getId(), getContext().getAppDeveloper()
						.getDevAuthToken().getToken()));
		return ok(Json.toJson(response));
	}
}
