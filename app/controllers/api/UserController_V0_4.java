package controllers.api;

import java.util.UUID;

import models.App;
import models.AppUser;
import models.User;

import org.bson.types.ObjectId;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.api.UserDTO_V0_4;
import views.api.util.APIStatus_V0_4;
import views.api.util.UsersResponse_V0_4;
import controllers.api.annotation.AppDeveloperAuthenticated;
import daos.api.UserDAO;
import daos.api.UserDAOMongo;

public class UserController_V0_4 extends Controller {

	@AppDeveloperAuthenticated
	public static Result newUser() {
		Form<UserDTO_V0_4> form = Form.form(UserDTO_V0_4.class)
				.bindFromRequest();
		if (form.hasErrors()) {
			return badRequest(form.errorsAsJson());
		} else {
			UserDTO_V0_4 userDTO = form.get();

			newUserUseCase(userDTO);

			UsersResponse_V0_4 response = new UsersResponse_V0_4(
					APIStatus_V0_4.SUCCESS, "Success", userDTO);
			return ok(Json.toJson(response));
		}
	}

	private static void newUserUseCase(UserDTO_V0_4 userDTO) {
		UserDAO<ObjectId> userDAO = new UserDAOMongo(
				DatabaseController.getDatastore());
		User user = userDAO.findByEmailAddress(userDTO.getUserEmail());
		if (user != null) {
			// user was already in the database
			Logger.debug("user != null");
			for (AppUser appUser : user.getAppUsers()) {
				if (appUser.getApp().equals(
						AppDeveloperAuthenticationController.getApp())) {
					// AppUser was also already in the database
					// TODO: decide what to do
				} else {
					// registers a new AppUser for that User
					AppUser newAppUser = saveNewAppUser(user,
							AppDeveloperAuthenticationController.getApp(),
							userDTO.getUserEmail());
					userDTO.setUserAuthToken(newAppUser.getUserAuthToken()
							.toString());
				}
			}
		} else {
			// creates a brand new User with an associated AppUser
			String createdBy = AppDeveloperAuthenticationController
					.getAppDeveloper().getEmailAddress();
			user = new User(userDTO.getUserEmail(), createdBy);
			AppUser newAppUser = saveNewAppUser(user,
					AppDeveloperAuthenticationController.getApp(),
					userDTO.getUserEmail());
			userDTO.setUserAuthToken(newAppUser.getUserAuthToken().toString());
		}
	}

	/*
	 * If the User is also a new one it will be saved to the database as well.
	 */
	private static AppUser saveNewAppUser(User user, App app,
			String userAppEmail) {
		String createdBy = AppDeveloperAuthenticationController
				.getAppDeveloper().getEmailAddress();

		UUID userAuthToken = UserAuthenticationController.createUserAuthToken();
		AppUser newAppUser = new AppUser(app, userAppEmail, userAuthToken,
				createdBy);
		user.addAppUser(newAppUser);

		UserDAO<ObjectId> userDAO = new UserDAOMongo(
				DatabaseController.getDatastore());
		userDAO.save(user);
		return newAppUser;
	}
}
