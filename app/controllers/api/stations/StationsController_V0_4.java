package controllers.api.stations;

import javax.inject.Inject;

import play.data.Form;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import util.api.MyLogger;
import util.api.SongwichAPIException;
import views.api.APIResponse;
import views.api.APIStatus_V0_4;
import views.api.DataTransferObject;
import views.api.stations.RadioStationDTO_V0_4;
import views.api.stations.RadioStationUpdateDTO_V0_4;
import views.api.stations.RadioStationsListDTO_V0_4;
import behavior.api.algorithms.StationStrategy;
import behavior.api.usecases.stations.StationsUseCases;
import controllers.api.APIController;
import controllers.api.annotation.AppDeveloperAuthenticated;
import controllers.api.annotation.UserAuthenticated;

public class StationsController_V0_4 extends APIController {

	private StationStrategy stationStrategy;

	@Inject
	public StationsController_V0_4(StationStrategy stationStrategy) {
		super();
		this.stationStrategy = stationStrategy;
	}

	// TODO: this should be accessible only from the Songwich Radio app
	@AppDeveloperAuthenticated
	@UserAuthenticated
	public Result postStations() {
		Form<RadioStationDTO_V0_4> radioStationForm = Form.form(
				RadioStationDTO_V0_4.class).bindFromRequest();
		if (radioStationForm.hasErrors()) {
			APIResponse apiResponse = new APIResponse(
					APIStatus_V0_4.INVALID_PARAMETER,
					DataTransferObject.errorsAsString(radioStationForm.errors()));
			return badRequest(Json.toJson(apiResponse));
		} else {
			RadioStationDTO_V0_4 radioStationDTO = radioStationForm.get();

			// process the request
			StationsUseCases stationsUseCases = new StationsUseCases(
					getContext());
			try {
				stationsUseCases.postStations(radioStationDTO, stationStrategy);
			} catch (SongwichAPIException exception) {
				MyLogger.warn(String.format("%s [%s]: %s", exception
						.getStatus().toString(), exception.getMessage(),
						Http.Context.current().request()));
				APIResponse response = new APIResponse(exception.getStatus(),
						exception.getMessage());
				if (exception.getStatus().equals(APIStatus_V0_4.UNAUTHORIZED)) {
					return Results.unauthorized(Json.toJson(response));
				} else {
					return Results.badRequest(Json.toJson(response));
				}
			}

			// return the response
			APIResponse response = new APIResponse(APIStatus_V0_4.SUCCESS,
					"Success", radioStationDTO);
			return ok(Json.toJson(response));
		}
	}

	@AppDeveloperAuthenticated
	public Result getStations() {
		// process the request
		StationsUseCases stationsUseCases = new StationsUseCases(getContext());
		RadioStationsListDTO_V0_4 radioStationsDTO = stationsUseCases
				.getStations();

		// return the response
		APIResponse response = new APIResponse(APIStatus_V0_4.SUCCESS,
				"Success", radioStationsDTO);
		return ok(Json.toJson(response));
	}

	@AppDeveloperAuthenticated
	public Result getStations(String stationId) {
		if (stationId == null) {
			// this is a call for all available stations
			return getStations();
		}

		// process the request
		StationsUseCases stationsUseCases = new StationsUseCases(getContext());
		RadioStationDTO_V0_4 radioStationDTO;
		try {
			radioStationDTO = stationsUseCases.getStations(stationId,
					stationStrategy);
		} catch (SongwichAPIException exception) {
			MyLogger.warn(String.format("%s [%s]: %s", exception.getStatus()
					.toString(), exception.getMessage(), Http.Context.current()
					.request()));
			APIResponse response = new APIResponse(exception.getStatus(),
					exception.getMessage());
			return Results.badRequest(Json.toJson(response));
		}

		// return the response
		APIResponse response = new APIResponse(APIStatus_V0_4.SUCCESS,
				"Success", radioStationDTO);
		return ok(Json.toJson(response));
	}

	// TODO: this should be accessible only from the Songwich Radio app
	@AppDeveloperAuthenticated
	@UserAuthenticated
	public Result putStations(String stationId) {

		Form<RadioStationUpdateDTO_V0_4> radioStationUpdateForm = Form.form(
				RadioStationUpdateDTO_V0_4.class).bindFromRequest();
		if (radioStationUpdateForm.hasErrors()) {
			APIResponse apiResponse = new APIResponse(
					APIStatus_V0_4.INVALID_PARAMETER,
					DataTransferObject.errorsAsString(radioStationUpdateForm
							.errors()));
			return badRequest(Json.toJson(apiResponse));
		} else {
			RadioStationUpdateDTO_V0_4 radioStationUpdateDTO = radioStationUpdateForm
					.get();

			// process the request
			StationsUseCases stationsUseCases = new StationsUseCases(
					getContext());
			try {
				stationsUseCases.putStations(stationId, radioStationUpdateDTO);
			} catch (SongwichAPIException exception) {
				MyLogger.warn(String.format("%s [%s]: %s", exception
						.getStatus().toString(), exception.getMessage(),
						Http.Context.current().request()));
				APIResponse response = new APIResponse(exception.getStatus(),
						exception.getMessage());
				if (exception.getStatus().equals(APIStatus_V0_4.UNAUTHORIZED)) {
					return Results.unauthorized(Json.toJson(response));
				} else {
					return Results.badRequest(Json.toJson(response));
				}
			}

			// return the response
			APIResponse response = new APIResponse(APIStatus_V0_4.SUCCESS,
					"Success", radioStationUpdateDTO);
			return ok(Json.toJson(response));
		}
	}

	// TODO: this should be accessible only from the Songwich Radio app
	@AppDeveloperAuthenticated
	@UserAuthenticated
	public Result putStationsAddScrobblers(String stationId) {
		Form<RadioStationUpdateDTO_V0_4> radioStationUpdateForm = Form.form(
				RadioStationUpdateDTO_V0_4.class).bindFromRequest();
		if (radioStationUpdateForm.hasErrors()) {
			APIResponse apiResponse = new APIResponse(
					APIStatus_V0_4.INVALID_PARAMETER,
					DataTransferObject.errorsAsString(radioStationUpdateForm
							.errors()));
			return badRequest(Json.toJson(apiResponse));
		} else {
			RadioStationUpdateDTO_V0_4 radioStationUpdateDTO = radioStationUpdateForm
					.get();

			// process the request
			StationsUseCases stationsUseCases = new StationsUseCases(
					getContext());
			try {
				stationsUseCases.putStationsAddScrobblers(stationId,
						radioStationUpdateDTO, stationStrategy);
			} catch (SongwichAPIException exception) {
				MyLogger.warn(String.format("%s [%s]: %s", exception
						.getStatus().toString(), exception.getMessage(),
						Http.Context.current().request()));
				APIResponse response = new APIResponse(exception.getStatus(),
						exception.getMessage());
				if (exception.getStatus().equals(APIStatus_V0_4.UNAUTHORIZED)) {
					return Results.unauthorized(Json.toJson(response));
				} else {
					return Results.badRequest(Json.toJson(response));
				}
			}

			// return the response
			APIResponse response = new APIResponse(APIStatus_V0_4.SUCCESS,
					"Success", radioStationUpdateDTO);
			return ok(Json.toJson(response));
		}
	}

	// TODO: this should be accessible only from the Songwich Radio app
	@AppDeveloperAuthenticated
	@UserAuthenticated
	public Result putStationsRemoveScrobblers(String stationId) {
		Form<RadioStationUpdateDTO_V0_4> radioStationUpdateForm = Form.form(
				RadioStationUpdateDTO_V0_4.class).bindFromRequest();
		if (radioStationUpdateForm.hasErrors()) {
			APIResponse apiResponse = new APIResponse(
					APIStatus_V0_4.INVALID_PARAMETER,
					DataTransferObject.errorsAsString(radioStationUpdateForm
							.errors()));
			return badRequest(Json.toJson(apiResponse));
		} else {
			RadioStationUpdateDTO_V0_4 radioStationUpdateDTO = radioStationUpdateForm
					.get();

			// process the request
			StationsUseCases stationsUseCases = new StationsUseCases(
					getContext());
			try {
				stationsUseCases.putStationsRemoveScrobblers(stationId,
						radioStationUpdateDTO, stationStrategy);
			} catch (SongwichAPIException exception) {
				MyLogger.warn(String.format("%s [%s]: %s", exception
						.getStatus().toString(), exception.getMessage(),
						Http.Context.current().request()));
				APIResponse response = new APIResponse(exception.getStatus(),
						exception.getMessage());
				if (exception.getStatus().equals(APIStatus_V0_4.UNAUTHORIZED)) {
					return Results.unauthorized(Json.toJson(response));
				} else {
					return Results.badRequest(Json.toJson(response));
				}
			}

			// return the response
			APIResponse response = new APIResponse(APIStatus_V0_4.SUCCESS,
					"Success", radioStationUpdateDTO);
			return ok(Json.toJson(response));
		}
	}

	// TODO: this should be accessible only from the Songwich Radio app
	@AppDeveloperAuthenticated
	public Result postNextSong() {
		Form<RadioStationUpdateDTO_V0_4> stationEntryForm = Form.form(
				RadioStationUpdateDTO_V0_4.class).bindFromRequest();
		if (stationEntryForm.hasErrors()) {
			APIResponse apiResponse = new APIResponse(
					APIStatus_V0_4.INVALID_PARAMETER,
					DataTransferObject.errorsAsString(stationEntryForm.errors()));
			return badRequest(Json.toJson(apiResponse));
		} else {
			RadioStationUpdateDTO_V0_4 radioStationDTO = stationEntryForm.get();

			// process the request
			StationsUseCases stationsUseCases = new StationsUseCases(
					getContext());
			try {
				stationsUseCases.postNextSong(radioStationDTO, stationStrategy);
			} catch (SongwichAPIException exception) {
				MyLogger.warn(String.format("%s [%s]: %s", exception
						.getStatus().toString(), exception.getMessage(),
						Http.Context.current().request()));
				APIResponse response = new APIResponse(exception.getStatus(),
						exception.getMessage());
				if (exception.getStatus().equals(APIStatus_V0_4.UNAUTHORIZED)) {
					return Results.unauthorized(Json.toJson(response));
				} else {
					return Results.badRequest(Json.toJson(response));
				}
			}

			// return the response
			APIResponse response = new APIResponse(APIStatus_V0_4.SUCCESS,
					"Success", radioStationDTO);
			return ok(Json.toJson(response));
		}
	}
}
