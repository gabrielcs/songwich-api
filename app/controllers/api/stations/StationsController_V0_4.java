package controllers.api.stations;

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
import views.api.stations.GetStationsResponse_V0_4;
import views.api.stations.NewRadioStationDTO_V0_4;
import views.api.stations.PostNextSongResponse_V0_4;
import views.api.stations.PostStationsResponse_V0_4;
import views.api.stations.RadioStationDTO_V0_4;
import views.api.stations.StationSongListDTO_V0_4;
import behavior.api.usecases.stations.StationsUseCases;
import controllers.api.APIController;
import controllers.api.annotation.AppDeveloperAuthenticated;

public class StationsController_V0_4 extends APIController {

	// TODO: this should be accessible only from the Songwich Radio app
	@AppDeveloperAuthenticated
	public static Result postNextSong() {
		Form<StationSongListDTO_V0_4> stationEntryForm = Form.form(
				StationSongListDTO_V0_4.class).bindFromRequest();
		if (stationEntryForm.hasErrors()) {
			APIResponse_V0_4 apiResponse = new APIResponse_V0_4(
					APIStatus_V0_4.INVALID_PARAMETER,
					DataTransferObject.errorsAsString(stationEntryForm.errors()));
			return badRequest(Json.toJson(apiResponse));
		} else {
			StationSongListDTO_V0_4 stationSongListDTO = stationEntryForm.get();

			// process the request
			StationsUseCases stationsUseCases = new StationsUseCases(
					getContext());
			try {
				stationsUseCases.postNextSong(stationSongListDTO);
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
					APIStatus_V0_4.SUCCESS, "Success", stationSongListDTO);
			return ok(Json.toJson(response));
		}
	}

	// TODO: this should be accessible only from the Songwich Radio app
	// TODO: this should be created by a user that represents one of the userIds
	@AppDeveloperAuthenticated
	public static Result postStations() {
		Form<NewRadioStationDTO_V0_4> newRadioStationForm = Form.form(
				NewRadioStationDTO_V0_4.class).bindFromRequest();
		if (newRadioStationForm.hasErrors()) {
			APIResponse_V0_4 apiResponse = new APIResponse_V0_4(
					APIStatus_V0_4.INVALID_PARAMETER,
					DataTransferObject.errorsAsString(newRadioStationForm
							.errors()));
			return badRequest(Json.toJson(apiResponse));
		} else {
			NewRadioStationDTO_V0_4 newRadioStationDTO = newRadioStationForm
					.get();

			// process the request
			StationsUseCases stationsUseCases = new StationsUseCases(
					getContext());
			try {
				stationsUseCases.postStations(newRadioStationDTO);
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
					APIStatus_V0_4.SUCCESS, "Success", newRadioStationDTO);
			return ok(Json.toJson(response));
		}
	}

	@AppDeveloperAuthenticated
	public static Result getStations() {
		// process the request
		StationsUseCases stationsUseCases = new StationsUseCases(getContext());
		List<RadioStationDTO_V0_4> radioStationsDTO = stationsUseCases
				.getStations();

		// return the response
		GetStationsResponse_V0_4 response = new GetStationsResponse_V0_4(
				APIStatus_V0_4.SUCCESS, "Success", radioStationsDTO);
		return ok(Json.toJson(response));
	}
	/*
	@AppDeveloperAuthenticated
	public static Result getStations(String stationId) {
		// process the request
		StationsUseCases stationsUseCases = new StationsUseCases(getContext());
		RadioStationDTO_V0_4 radioStationDTO = stationsUseCases
				.getStations(stationId);

		// return the response
		GetStationsResponse_V0_4 response = new GetStationsResponse_V0_4(
				APIStatus_V0_4.SUCCESS, "Success", radioStationsDTO);
		return ok(Json.toJson(response));
	}
*/
}
