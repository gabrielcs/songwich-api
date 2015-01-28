package controllers.api.stations;

import java.util.List;

import javax.inject.Inject;

import models.api.stations.RadioStation;

import org.bson.types.ObjectId;

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
import views.api.stations.GetStationsResponse_V0_4;
import views.api.stations.GetStationsUniqueResponse_V0_4;
import views.api.stations.PostNextSongResponse_V0_4;
import views.api.stations.PostStationsResponse_V0_4;
import views.api.stations.PutStationsResponse_V0_4;
import views.api.stations.RadioStationInputDTO_V0_4;
import views.api.stations.RadioStationOutputDTO_V0_4;
import views.api.stations.RadioStationUpdateInputDTO_V0_4;
import behavior.api.algorithms.StationStrategy;
import behavior.api.usecases.stations.StationsUseCases;
import controllers.api.APIController;
import controllers.api.annotation.AppDeveloperAuthenticated;
import controllers.api.annotation.Logged;
import controllers.api.annotation.UserAuthenticated;
import database.api.stations.RadioStationDAO;
import database.api.stations.RadioStationDAOMongo;

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
	@Logged
	public Result postStations(boolean subscribeScrobblers) {
		Form<RadioStationInputDTO_V0_4> radioStationForm = Form.form(
				RadioStationInputDTO_V0_4.class).bindFromRequest();
		if (radioStationForm.hasErrors()) {
			APIResponse_V0_4 apiResponse = new APIResponse_V0_4(
					APIStatus_V0_4.INVALID_PARAMETER,
					DTOValidator.errorsAsString(radioStationForm.errors()));
			return badRequest(Json.toJson(apiResponse));
		} else {
			RadioStationInputDTO_V0_4 radioStationInputDTO = radioStationForm
					.get();

			// process the request
			StationsUseCases stationsUseCases = new StationsUseCases(
					getContext());
			RadioStationOutputDTO_V0_4 radioStationOutputDTO;
			try {
				radioStationOutputDTO = stationsUseCases.postStations(
						radioStationInputDTO, stationStrategy,
						subscribeScrobblers);
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
			PostStationsResponse_V0_4 response = new PostStationsResponse_V0_4(
					APIStatus_V0_4.SUCCESS, "Success", radioStationOutputDTO);
			return ok(Json.toJson(response));
		}
	}

	@AppDeveloperAuthenticated
	@Logged
	private Result getStations(boolean onlyActiveStations) {
		// process the request
		StationsUseCases stationsUseCases = new StationsUseCases(getContext());
		List<RadioStationOutputDTO_V0_4> radioStationsDTO = stationsUseCases
				.getStations(onlyActiveStations);

		// return the response
		GetStationsResponse_V0_4 response = new GetStationsResponse_V0_4(
				APIStatus_V0_4.SUCCESS, "Success", radioStationsDTO);
		return ok(Json.toJson(response));
	}

	@AppDeveloperAuthenticated
	@Logged
	public Result getStations(String stationId, boolean onlyActiveStations,
			boolean includeScrobblersData) {

		if (stationId == null) {
			// this is a call for all available stations
			return getStations(onlyActiveStations);
		}

		// process the request
		StationsUseCases stationsUseCases = new StationsUseCases(getContext());
		RadioStationOutputDTO_V0_4 radioStationDTO;
		try {
			radioStationDTO = stationsUseCases.getStations(stationId,
					stationStrategy, includeScrobblersData);
		} catch (SongwichAPIException exception) {
			MyLogger.warn(String.format("%s [%s]: %s", exception.getStatus()
					.toString(), exception.getMessage(), Http.Context.current()
					.request()));
			APIResponse_V0_4 response = new APIResponse_V0_4(
					exception.getStatus(), exception.getMessage());
			return Results.badRequest(Json.toJson(response));
		}

		// return the response
		GetStationsUniqueResponse_V0_4 response = new GetStationsUniqueResponse_V0_4(
				APIStatus_V0_4.SUCCESS, "Success", radioStationDTO);
		return ok(Json.toJson(response));
	}

	// TODO: this should be accessible only from the Songwich Radio app
	@AppDeveloperAuthenticated
	@UserAuthenticated
	@Logged
	public Result putStations(String stationId) {

		Form<RadioStationUpdateInputDTO_V0_4> radioStationUpdateForm = Form
				.form(RadioStationUpdateInputDTO_V0_4.class).bindFromRequest();
		if (radioStationUpdateForm.hasErrors()) {
			APIResponse_V0_4 apiResponse = new APIResponse_V0_4(
					APIStatus_V0_4.INVALID_PARAMETER,
					DTOValidator.errorsAsString(radioStationUpdateForm.errors()));
			return badRequest(Json.toJson(apiResponse));
		} else {
			RadioStationUpdateInputDTO_V0_4 radioStationUpdateDTO = radioStationUpdateForm
					.get();

			// process the request
			StationsUseCases stationsUseCases = new StationsUseCases(
					getContext());
			RadioStationOutputDTO_V0_4 radioStationOutputDTO;
			try {
				radioStationOutputDTO = stationsUseCases.putStations(stationId,
						radioStationUpdateDTO);
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
			PutStationsResponse_V0_4 response = new PutStationsResponse_V0_4(
					APIStatus_V0_4.SUCCESS, "Success", radioStationOutputDTO);
			return ok(Json.toJson(response));
		}
	}

	// TODO: this should be accessible only from the Songwich Radio app
	@AppDeveloperAuthenticated
	@UserAuthenticated
	@Logged
	public Result putStationsAddScrobblers(String stationId,
			boolean subscribeScrobblers) {

		Form<RadioStationUpdateInputDTO_V0_4> radioStationUpdateForm = Form
				.form(RadioStationUpdateInputDTO_V0_4.class).bindFromRequest();
		if (radioStationUpdateForm.hasErrors()) {
			APIResponse_V0_4 apiResponse = new APIResponse_V0_4(
					APIStatus_V0_4.INVALID_PARAMETER,
					DTOValidator.errorsAsString(radioStationUpdateForm.errors()));
			return badRequest(Json.toJson(apiResponse));
		} else {
			RadioStationUpdateInputDTO_V0_4 radioStationUpdateDTO = radioStationUpdateForm
					.get();

			// process the request
			StationsUseCases stationsUseCases = new StationsUseCases(
					getContext());
			RadioStationOutputDTO_V0_4 radioStationOutputDTO;
			try {
				radioStationOutputDTO = stationsUseCases
						.putStationsAddScrobblers(stationId,
								radioStationUpdateDTO, stationStrategy,
								subscribeScrobblers);
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
			PutStationsResponse_V0_4 response = new PutStationsResponse_V0_4(
					APIStatus_V0_4.SUCCESS, "Success", radioStationOutputDTO);
			return ok(Json.toJson(response));
		}
	}

	// TODO: this should be accessible only from the Songwich Radio app
	@AppDeveloperAuthenticated
	@UserAuthenticated
	@Logged
	public Result putStationsRemoveScrobblers(String stationId) {
		Form<RadioStationUpdateInputDTO_V0_4> radioStationUpdateForm = Form
				.form(RadioStationUpdateInputDTO_V0_4.class).bindFromRequest();
		if (radioStationUpdateForm.hasErrors()) {
			APIResponse_V0_4 apiResponse = new APIResponse_V0_4(
					APIStatus_V0_4.INVALID_PARAMETER,
					DTOValidator.errorsAsString(radioStationUpdateForm.errors()));
			return badRequest(Json.toJson(apiResponse));
		} else {
			RadioStationUpdateInputDTO_V0_4 radioStationUpdateDTO = radioStationUpdateForm
					.get();

			// process the request
			StationsUseCases stationsUseCases = new StationsUseCases(
					getContext());
			RadioStationOutputDTO_V0_4 radioStationOutputDTO;
			try {
				radioStationOutputDTO = stationsUseCases
						.putStationsRemoveScrobblers(stationId,
								radioStationUpdateDTO, stationStrategy);
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
			PutStationsResponse_V0_4 response = new PutStationsResponse_V0_4(
					APIStatus_V0_4.SUCCESS, "Success", radioStationOutputDTO);
			return ok(Json.toJson(response));
		}
	}

	// TODO: this should be accessible only from the Songwich Radio app
	@AppDeveloperAuthenticated
	@UserAuthenticated
	@Logged
	public static Result putStationsMarkAsVerified(String stationId) {
		StationsUseCases stationsUseCases = new StationsUseCases(getContext());
		RadioStationOutputDTO_V0_4 radioStationOutputDTO;
		try {
			radioStationOutputDTO = stationsUseCases
					.putStationsMarkAsVerified(stationId);
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
		PutStationsResponse_V0_4 response = new PutStationsResponse_V0_4(
				APIStatus_V0_4.SUCCESS, "Success", radioStationOutputDTO);
		return ok(Json.toJson(response));
	}

	// TODO: this should be accessible only from the Songwich Radio app
	@AppDeveloperAuthenticated
	@Logged
	public Result postNextSong() {
		Form<RadioStationUpdateInputDTO_V0_4> stationEntryForm = Form.form(
				RadioStationUpdateInputDTO_V0_4.class).bindFromRequest();
		if (stationEntryForm.hasErrors()) {
			APIResponse_V0_4 apiResponse = new APIResponse_V0_4(
					APIStatus_V0_4.INVALID_PARAMETER,
					DTOValidator.errorsAsString(stationEntryForm.errors()));
			return badRequest(Json.toJson(apiResponse));
		} else {
			RadioStationUpdateInputDTO_V0_4 radioStationDTO = stationEntryForm
					.get();

			// process the request
			StationsUseCases stationsUseCases = new StationsUseCases(
					getContext());
			RadioStationOutputDTO_V0_4 stationOutputDTO;
			try {
				stationOutputDTO = stationsUseCases.postNextSong(
						radioStationDTO, stationStrategy);
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
			PostNextSongResponse_V0_4 response = new PostNextSongResponse_V0_4(
					APIStatus_V0_4.SUCCESS, "Success", stationOutputDTO);
			return ok(Json.toJson(response));
		}
	}

	// TODO: this should be accessible only from the Songwich Radio app
	@AppDeveloperAuthenticated
	@UserAuthenticated
	@Logged
	public Result putStationsDeactivate(String stationId) {
		RadioStationOutputDTO_V0_4 radioStationUpdateDTO;

		// process the request
		StationsUseCases stationsUseCases = new StationsUseCases(getContext());
		try {
			radioStationUpdateDTO = stationsUseCases
					.putStationsDeactivate(stationId);
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
		PutStationsResponse_V0_4 response = new PutStationsResponse_V0_4(
				APIStatus_V0_4.SUCCESS, "Success", radioStationUpdateDTO);
		return ok(Json.toJson(response));
	}

	public Result postFixRemoveFaultyStationsStaging() {
		ObjectId danielCaonFM = new ObjectId("526ee129e4b03f1a33f3dd42");
		ObjectId gabrielFM = new ObjectId("526ee177e4b03f1a33f3dd45");
		
		removeStation(danielCaonFM);
		removeStation(gabrielFM);
		
		// return the response
		APIResponse_V0_4 response = new APIResponse_V0_4(
				APIStatus_V0_4.SUCCESS, "Success");
		return ok(Json.toJson(response));
	}

	private void removeStation(ObjectId stationId) {
		RadioStationDAO<ObjectId> stationDAO = new RadioStationDAOMongo();
		RadioStation station = stationDAO.findById(stationId);
		stationDAO.delete(station);
	}

	/*
	 * public Result postFixStationsIds() { ObjectId oldStationId = new
	 * ObjectId("52a20974e4b04a9b4816440d"); ObjectId newStationId = new
	 * ObjectId("526ee177e4b03f1a33f3dd45"); fixStationId(oldStationId,
	 * newStationId);
	 * 
	 * oldStationId = new ObjectId("52a20760e4b04a9b4816440c"); newStationId =
	 * new ObjectId("526ee129e4b03f1a33f3dd42"); fixStationId(oldStationId,
	 * newStationId);
	 * 
	 * oldStationId = new ObjectId("52a21390e4b0f949eea56540"); newStationId =
	 * new ObjectId("526ee1bfe4b03f1a33f3dd48"); fixStationId(oldStationId,
	 * newStationId);
	 * 
	 * return Results.ok(); }
	 * 
	 * private void fixStationId(ObjectId oldStationId, ObjectId newStationId) {
	 * String devEmail = "gabrielcs@gmail.com";
	 * 
	 * RadioStationDAO<ObjectId> stationDAO = new RadioStationDAOMongo();
	 * RadioStation station = stationDAO.findById(oldStationId);
	 * stationDAO.delete(station); station.setId(newStationId);
	 * stationDAO.save(station, devEmail); }
	 */

}
