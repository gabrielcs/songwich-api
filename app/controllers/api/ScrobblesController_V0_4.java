package controllers.api;

import java.util.List;

import org.bson.types.ObjectId;

import play.data.Form;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Results;
import usecases.api.ScrobblesUseCases;
import usecases.api.util.MyLogger;
import usecases.api.util.SongwichAPIException;
import views.api.ScrobblesDTO_V0_4;
import views.api.util.DataTransferObject;
import controllers.api.annotation.AppDeveloperAuthenticated;
import controllers.api.annotation.UserAuthenticated;
import controllers.api.util.APIController;
import controllers.api.util.APIResponse_V0_4;
import controllers.api.util.APIStatus_V0_4;
import controllers.api.util.GetScrobblesResponse_V0_4;
import controllers.api.util.PostScrobblesResponse_V0_4;

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
			scrobblesUseCases.postScrobbles(scrobbleDTO);

			// return the response
			PostScrobblesResponse_V0_4 response = new PostScrobblesResponse_V0_4(
					APIStatus_V0_4.SUCCESS, "Success", scrobbleDTO);
			return ok(Json.toJson(response));
		}
	}

	@AppDeveloperAuthenticated
	@UserAuthenticated
	public static Result getScrobbles(String userId, int daysOffset, int results) {
		ObjectId objectId;
		try {
			objectId = new ObjectId(userId);
		} catch (IllegalArgumentException illegalArgumentEx) {
			SongwichAPIException apiEx = new SongwichAPIException(
					"Invalid userId: " + userId,
					APIStatus_V0_4.INVALID_PARAMETER);
			MyLogger.warn(String.format("%s [%s]: %s", apiEx.getStatus()
					.toString(), apiEx.getMessage(), Context.current()
					.request()));
			APIResponse_V0_4 response = new APIResponse_V0_4(apiEx.getStatus(),
					apiEx.getMessage());
			return Results.badRequest(Json.toJson(response));
		}

		// process the request
		ScrobblesUseCases scroblesUseCases = new ScrobblesUseCases(getContext());
		List<ScrobblesDTO_V0_4> scrobbleDTOs;
		try {
			if (daysOffset < 0) {
				if (results < 0) {
					scrobbleDTOs = scroblesUseCases.getScrobbles(objectId);
				} else {
					scrobbleDTOs = scroblesUseCases.getScrobbles(objectId,
							results);
				}
			} else {
				if (results < 0) {
					scrobbleDTOs = scroblesUseCases.getScrobblesDaysOffset(
							objectId, daysOffset);
				} else {
					scrobbleDTOs = scroblesUseCases.getScrobblesDaysOffset(
							objectId, daysOffset, results);
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
