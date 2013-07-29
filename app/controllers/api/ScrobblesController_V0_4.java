package controllers.api;

import java.util.List;

import org.bson.types.ObjectId;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Results;
import usecases.api.ScrobblesUseCases;
import usecases.api.util.SongwichAPIException;
import controllers.api.annotation.AppDeveloperAuthenticated;
import controllers.api.annotation.UserAuthenticated;
import controllers.api.util.APIController;
import dtos.api.ScrobblesDTO_V0_4;
import dtos.api.util.APIResponse_V0_4;
import dtos.api.util.APIStatus_V0_4;
import dtos.api.util.DataTransferObject;
import dtos.api.util.GetScrobblesResponse_V0_4;
import dtos.api.util.PostScrobblesResponse_V0_4;

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
	public static Result getScrobbles(String userId) {
		ObjectId objectId;
		try {
			objectId = new ObjectId(userId);
		} catch (IllegalArgumentException illegalArgumentEx) {
			SongwichAPIException apiEx = new SongwichAPIException(
					"Invalid userId: " + userId,
					APIStatus_V0_4.INVALID_PARAMETER);
			Logger.warn(String.format("%s [%s]: %s", apiEx.getStatus()
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
			scrobbleDTOs = scroblesUseCases.getScrobbles(objectId);
		} catch (SongwichAPIException exception) {
			// user unauthorized for getting scrobbles from another user
			Logger.warn(String.format("%s [%s]: %s", exception.getStatus()
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
}
