package controllers.api.scrobbles;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import play.data.Form;
import play.libs.F.Option;
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
import views.api.scrobbles.ScrobblesPagingDTO_V0_4;
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
	public static Result getScrobbles(String userId, Option<Integer> results,
			Option<String> since, Option<String> sinceInclusive,
			Option<String> until, Option<String> untilInclusive,
			boolean chosenByUserOnly) {

		MyLogger.debug("timestamp=" + getContext().getTimestamp());

		// TODO: get this from a configuration file
		final int DEFAULT_RESULTS = 30;

		ScrobblesUseCases scrobblesUseCases = new ScrobblesUseCases(
				getContext());
		Pair<List<ScrobblesDTO_V0_4>, ScrobblesPagingDTO_V0_4> scrobblesRestultDTOPair;
		try {
			checkUrlParams(since, sinceInclusive, until, untilInclusive);

			if (since.isDefined()) {
				scrobblesRestultDTOPair = scrobblesUseCases.getScrobblesSince(
						Http.Context.current().request().host(), userId,
						since.get(), false, results.getOrElse(DEFAULT_RESULTS),
						chosenByUserOnly);
			} else if (until.isDefined()) {
				scrobblesRestultDTOPair = scrobblesUseCases.getScrobblesUntil(
						Http.Context.current().request().host(), userId,
						until.get(), false, results.getOrElse(DEFAULT_RESULTS),
						chosenByUserOnly);
			} else if (sinceInclusive.isDefined()) {
				scrobblesRestultDTOPair = scrobblesUseCases.getScrobblesSince(
						Http.Context.current().request().host(), userId,
						sinceInclusive.get(), true,
						results.getOrElse(DEFAULT_RESULTS), chosenByUserOnly);
			} else if (until.isDefined()) {
				scrobblesRestultDTOPair = scrobblesUseCases.getScrobblesUntil(
						Http.Context.current().request().host(), userId,
						untilInclusive.get(), true,
						results.getOrElse(DEFAULT_RESULTS), chosenByUserOnly);
			} else {
				scrobblesRestultDTOPair = scrobblesUseCases.getScrobbles(
						Http.Context.current().request().host(), userId,
						results.getOrElse(DEFAULT_RESULTS), chosenByUserOnly);
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
				APIStatus_V0_4.SUCCESS, "Success",
				scrobblesRestultDTOPair.getLeft(),
				scrobblesRestultDTOPair.getRight());
		return ok(Json.toJson(response));
	}

	private static void checkUrlParams(Option<String> since,
			Option<String> sinceInclusive, Option<String> until,
			Option<String> untilInclusive) throws SongwichAPIException {

		int definedParams = 0;

		if (since.isDefined()) {
			definedParams++;
		}
		if (sinceInclusive.isDefined()) {
			definedParams++;
		}
		if (until.isDefined()) {
			definedParams++;
		}
		if (untilInclusive.isDefined()) {
			definedParams++;
		}

		if (definedParams > 1) {
			throw new SongwichAPIException(
					"Cannot define more than 1 of 'since', 'until', 'sinceInclusive' and 'untilInclusive'",
					APIStatus_V0_4.INVALID_PARAMETER);
		}
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
