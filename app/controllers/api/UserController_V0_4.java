package controllers.api;

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
import views.api.util.UserResponse_V0_4;
import controllers.api.annotation.AppDeveloperAuthenticated;
import daos.api.UserDAO;
import daos.api.UserDAOMongo;

public class UserController_V0_4 extends Controller {

	//@AppDeveloperAuthenticated
	public static Result newUser() {
		Form<UserDTO_V0_4> form = Form.form(UserDTO_V0_4.class)
				.bindFromRequest();
		if (form.hasErrors()) {
			return badRequest(form.errorsAsJson());
		} else {
			UserDTO_V0_4 userDTO = form.get();
			
			newUserUseCase(userDTO);

			UserResponse_V0_4 response = new UserResponse_V0_4(
					APIStatus_V0_4.SUCCESS, "Success", userDTO);
			return ok(Json.toJson(response));
		}
	}

	private static void newUserUseCase(UserDTO_V0_4 userDTO) {
		Logger.debug("About to get the datastore");
		UserDAO<ObjectId> userDAO = new UserDAOMongo(
				DatabaseController.getDatastore());
		Logger.debug("About to check for the user on the database");
		User user = userDAO.findByEmailAddress(userDTO.getUserEmail());
		Logger.debug("Have just checked for the user on the database");
		if (user != null) {
			for (AppUser appUser : user.getAppUsers()) {
				if (appUser.getApp().equals(
						AppDeveloperAuthenticationController.getApp())) {
					// TODO: decide what to do
				} else {
					// creates a new AppUser
					user.addAppUser(appUser);
				}
			}
		} else {
			String createdBy = AppDeveloperAuthenticationController
					.getAppDeveloper().getEmailAddress();
			// creates a brand new user
			user = new User(userDTO.getUserEmail(), createdBy);
			AppUser appUser = new AppUser(
					AppDeveloperAuthenticationController.getApp(),
					userDTO.getUserEmail(), createdBy);
			user.addAppUser(appUser);
			userDAO.save(user);
		}
	}
}
