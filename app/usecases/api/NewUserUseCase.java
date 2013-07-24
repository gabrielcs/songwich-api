package usecases.api;

import java.util.UUID;

import models.AppUser;
import models.User;

import org.bson.types.ObjectId;

import usecases.api.util.DatabaseContext;
import usecases.api.util.RequestContext;
import usecases.api.util.UseCase;
import daos.api.UserDAO;
import daos.api.UserDAOMongo;
import dtos.api.UserDTO_V0_4;

public class NewUserUseCase extends UseCase {
	
	public NewUserUseCase(RequestContext context) {
		super(context);
	}

	public void newUser(UserDTO_V0_4 userDTO) {
		UserDAO<ObjectId> userDAO = new UserDAOMongo(
				DatabaseContext.getDatastore());
		User user = userDAO.findByEmailAddress(userDTO.getUserEmail());
		if (user != null) {
			// user was already in the database
			for (AppUser appUser : user.getAppUsers()) {
				if (appUser.getApp().equals(
						getContext().getApp())) {
					// AppUser was also already in the database
					// TODO: decide what to do
				} else {
					// registers a new AppUser for that User
					AppUser newAppUser = saveNewAppUser(user, userDTO.getUserEmail());
					userDTO.setUserAuthToken(newAppUser.getUserAuthToken()
							.toString());
				}
			}
		} else {
			// creates a brand new User with an associated AppUser
			String createdBy = getContext()
					.getAppDeveloper().getEmailAddress();
			user = new User(userDTO.getUserEmail(), createdBy);
			AppUser newAppUser = saveNewAppUser(user, userDTO.getUserEmail());
			userDTO.setUserAuthToken(newAppUser.getUserAuthToken().toString());
		}
	}

	/*
	 * If the User is also a new one it will be saved to the database as well.
	 */
	private AppUser saveNewAppUser(User user, String userAppEmail) {
		String createdBy = getContext()
				.getAppDeveloper().getEmailAddress();

		UUID userAuthToken = DatabaseContext.createUserAuthToken();
		AppUser newAppUser = new AppUser(getContext().getApp(), userAppEmail, userAuthToken,
				createdBy);
		user.addAppUser(newAppUser);

		UserDAO<ObjectId> userDAO = new UserDAOMongo(
				DatabaseContext.getDatastore());
		userDAO.save(user);
		return newAppUser;
	}
}
