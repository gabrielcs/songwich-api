package controllers.api.scrobbles;

import java.util.List;

import javax.inject.Inject;

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
import views.api.scrobbles.GetUsersResponse_V0_4;
import views.api.scrobbles.GetUsersUniqueResponse_V0_4;
import views.api.scrobbles.PostUsersResponse_V0_4;
import views.api.scrobbles.PutUsersResponse_V0_4;
import views.api.scrobbles.UserInputDTO_V0_4;
import views.api.scrobbles.UserOutputDTO_V0_4;
import views.api.scrobbles.UserUpdateInputDTO_V0_4;
import behavior.api.algorithms.StationStrategy;
import behavior.api.usecases.scrobbles.UsersUseCases;
import controllers.api.APIController;
import controllers.api.annotation.AppDeveloperAuthenticated;
import controllers.api.annotation.Logged;
import controllers.api.annotation.UserAuthenticated;

public class UsersController_V0_4 extends APIController {

	private StationStrategy stationStrategy;

	@Inject
	public UsersController_V0_4(StationStrategy stationStrategy) {
		super();
		this.stationStrategy = stationStrategy;
	}

	// TODO: this should be accessible only from Songwich developers
	@AppDeveloperAuthenticated
	@UserAuthenticated
	@Logged
	public static Result putUsersMarkAsVerified(String userId) {
		// process the request
		UserOutputDTO_V0_4 userOutputDTO;
		UsersUseCases usersUseCases = new UsersUseCases(getContext());
		try {
			userOutputDTO = usersUseCases.putUsersMarkAsVerified(userId);
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
		PutUsersResponse_V0_4 response = new PutUsersResponse_V0_4(
				APIStatus_V0_4.SUCCESS, "Success", userOutputDTO);
		return ok(Json.toJson(response));
	}

	@AppDeveloperAuthenticated
	@Logged
	public static Result postUsers() {
		Form<UserInputDTO_V0_4> form = Form.form(UserInputDTO_V0_4.class)
				.bindFromRequest();
		if (form.hasErrors()) {
			APIResponse_V0_4 apiResponse = new APIResponse_V0_4(
					APIStatus_V0_4.INVALID_PARAMETER,
					DTOValidator.errorsAsString(form.errors()));
			return badRequest(Json.toJson(apiResponse));
		} else {
			UserInputDTO_V0_4 userInputDTO = form.get();

			// process the request
			UsersUseCases usersUseCases = new UsersUseCases(getContext());
			UserOutputDTO_V0_4 userOutputDTO = usersUseCases
					.postUsers(userInputDTO);

			// return the response
			PostUsersResponse_V0_4 response = new PostUsersResponse_V0_4(
					APIStatus_V0_4.SUCCESS, "Success", userOutputDTO);
			return ok(Json.toJson(response));
		}
	}

	@AppDeveloperAuthenticated
	@UserAuthenticated
	@Logged
	public static Result putUsers(String userId) {
		Form<UserUpdateInputDTO_V0_4> form = Form.form(
				UserUpdateInputDTO_V0_4.class).bindFromRequest();
		if (form.hasErrors()) {
			APIResponse_V0_4 apiResponse = new APIResponse_V0_4(
					APIStatus_V0_4.INVALID_PARAMETER,
					DTOValidator.errorsAsString(form.errors()));
			return badRequest(Json.toJson(apiResponse));
		} else {
			UserUpdateInputDTO_V0_4 userUpdateDTO = form.get();
			// process the request
			UsersUseCases usersUseCases = new UsersUseCases(getContext());
			UserOutputDTO_V0_4 userOutputDTO;
			try {
				userOutputDTO = usersUseCases.putUsers(userId, userUpdateDTO);
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
			PutUsersResponse_V0_4 response = new PutUsersResponse_V0_4(
					APIStatus_V0_4.SUCCESS, "Success", userOutputDTO);
			return ok(Json.toJson(response));
		}
	}

	@AppDeveloperAuthenticated
	@Logged
	public static Result getUsers() {
		// process the request
		UsersUseCases usersUseCases = new UsersUseCases(getContext());
		List<UserOutputDTO_V0_4> usersDTO = usersUseCases.getUsers();

		// return the response
		GetUsersResponse_V0_4 response = new GetUsersResponse_V0_4(
				APIStatus_V0_4.SUCCESS, "Success", usersDTO);
		return ok(Json.toJson(response));
	}

	@AppDeveloperAuthenticated
	@Logged
	public static Result getUsers(String userId, String userEmail) {
		if (userId == null && userEmail == null) {
			// this is a call for all registered users
			return getUsers();
		}

		// process the request
		UsersUseCases usersUseCases = new UsersUseCases(getContext());
		UserOutputDTO_V0_4 userDTO;
		try {
			if (userId != null) {
				userDTO = usersUseCases.getUsersById(userId);
			} else {
				// userEmail != null
				userDTO = usersUseCases.getUsersByEmail(userEmail);
			}
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
		GetUsersUniqueResponse_V0_4 response = new GetUsersUniqueResponse_V0_4(
				APIStatus_V0_4.SUCCESS, "Success", userDTO);
		return ok(Json.toJson(response));
	}

	@AppDeveloperAuthenticated
	@UserAuthenticated
	@Logged
	public Result putUsersDeactivate(String userId) {
		// process the request
		UserOutputDTO_V0_4 userOutputDTO;
		UsersUseCases usersUseCases = new UsersUseCases(getContext());
		try {
			userOutputDTO = usersUseCases.putUsersDeactivate(userId,
					stationStrategy);
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
		PutUsersResponse_V0_4 response = new PutUsersResponse_V0_4(
				APIStatus_V0_4.SUCCESS, "Success", userOutputDTO);
		return ok(Json.toJson(response));
	}

	/*
	 * public static Result postFixReactivateUser() { String devEmail =
	 * "gabrielcs@gmail.com";
	 * 
	 * ObjectId objectId = new ObjectId("5267d52792e6bf54e1b5047d");
	 * 
	 * UserDAO<ObjectId> userDAO = new UserDAOMongo(); User user =
	 * userDAO.findById(objectId, false); user.setDeactivated(false);
	 * userDAO.save(user, devEmail);
	 * 
	 * return Results.ok(); }
	 * 
	 * public static Result postFixReactivateStation() { String devEmail =
	 * "gabrielcs@gmail.com";
	 * 
	 * ObjectId objectId = new ObjectId("528e59f2e4b0fed29a59c813");
	 * 
	 * RadioStationDAO<ObjectId> stationDAO = new RadioStationDAOMongo();
	 * RadioStation station = stationDAO.findById(objectId, false);
	 * station.setDeactivated(false); stationDAO.save(station, devEmail);
	 * 
	 * return Results.ok(); }
	 * 
	 * 
	 * public static Result postFixUserNames() { String devEmail =
	 * "gabrielcs@gmail.com";
	 * 
	 * UserDAO<ObjectId> userDAO = new UserDAOMongo(); User userGabriel =
	 * userDAO.findByEmailAddress("gabrielcs@gmail.com");
	 * userGabriel.setName("Gabriel Cypriano"); userDAO.save(userGabriel,
	 * devEmail);
	 * 
	 * User userDaniel = userDAO.findByEmailAddress("drscaon@gmail.com");
	 * userDaniel.setName("Daniel Caon"); userDAO.save(userDaniel, devEmail);
	 * 
	 * return Results.ok(); }
	 * 
	 * public static Result postFixTestUser() { String gabrielEmail =
	 * "gabrielcs@gmail.com";
	 * 
	 * ObjectId oldId = new ObjectId("526ee2cee4b03f1a33f3dd4d"); ObjectId newId
	 * = new ObjectId("5267d52792e6bf54e1b5047d");
	 * 
	 * UserDAO<ObjectId> userDAO = new UserDAOMongo(); List<User> users =
	 * userDAO.find().asList(); for (User user : users) { if
	 * (user.getId().equals(oldId)) { userDAO.delete(user); user.setId(newId);
	 * userDAO.save(user, gabrielEmail); } }
	 * 
	 * return Results.ok(); }
	 * 
	 * 
	 * public static Result postFixDevAuthTokens() { String oldAuthTokenString =
	 * "52ea5b3f-0700-4e4f-9074-d0cba7d77237"; AuthToken newAuthToken = new
	 * AuthToken("5158f947-291e-4594-8c14-72671a92e94e");
	 * postFixDevAuthToken(oldAuthTokenString, newAuthToken);
	 * 
	 * oldAuthTokenString = "75df2d1e-27f6-4c23-befb-ba0577814953"; newAuthToken
	 * = new AuthToken("aa9da1b3-d3aa-408a-8a1d-9d852c3bc421");
	 * postFixDevAuthToken(oldAuthTokenString, newAuthToken);
	 * 
	 * return Results.ok(); }
	 * 
	 * public static void postFixDevAuthToken(String oldAuthTokenString,
	 * AuthToken newAuthToken) { String devEmail = "gabrielcs@gmail.com";
	 * 
	 * AppDAO<ObjectId> appDAO = new AppDAOMongo(); App app =
	 * appDAO.findByDevAuthToken(oldAuthTokenString); for (AppDeveloper
	 * appDeveloper : app.getAppDevelopers()) { if
	 * (appDeveloper.getDevAuthToken().getToken().equals(oldAuthTokenString)) {
	 * appDeveloper.setDevAuthToken(newAuthToken); break; } }
	 * 
	 * appDAO.save(app, devEmail); }
	 */
}
