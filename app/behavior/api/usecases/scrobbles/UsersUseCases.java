package behavior.api.usecases.scrobbles;

import models.api.scrobbles.AppUser;
import models.api.scrobbles.AuthToken;
import models.api.scrobbles.User;

import org.bson.types.ObjectId;

import behavior.api.usecases.RequestContext;
import behavior.api.usecases.UseCase;

import util.api.MyLogger;
import views.api.scrobbles.UsersDTO_V0_4;
import database.api.scrobbles.UserDAO;
import database.api.scrobbles.UserDAOMongo;

public class UsersUseCases extends UseCase {

	public UsersUseCases(RequestContext context) {
		super(context);
	}

	public void postUsers(UsersDTO_V0_4 userDTO) {
		UserDAO<ObjectId> userDAO = new UserDAOMongo();
		User user = userDAO.findByEmailAddress(userDTO.getUserEmail());
		if (user != null) {

			// user was already in the database
			for (AppUser appUser : user.getAppUsers()) {
				if (appUser.getApp().equals(getContext().getApp())) {
					// AppUser was also already in the database
					updateDTO(user, appUser, userDTO);
					MyLogger.info(String
							.format("Tried to create user \"%s\" but it was already in database with id=%s",
									userDTO.getUserEmail(), userDTO.getUserId())
							+ String.format(". devAuthToken=%s", getContext()
									.getAppDeveloper().getDevAuthToken()
									.getToken()));
					return;
				} else {
					// registers a new AppUser for that User
					AppUser newAppUser = saveNewAppUser(user,
							userDTO.getUserEmail());
					updateDTO(user, newAppUser, userDTO);
				}
			}
		} else {
			// creates a brand new User with an associated AppUser
			String createdBy = getContext().getAppDeveloper().getEmailAddress();
			user = new User(userDTO.getUserEmail(), createdBy);
			AppUser newAppUser = saveNewAppUser(user, userDTO.getUserEmail());
			updateDTO(user, newAppUser, userDTO);
		}
		MyLogger.info(String.format(
				"Created user \"%s\" with id=%s and authToken=%s",
				userDTO.getUserEmail(), userDTO.getUserId(),
				userDTO.getUserAuthToken()));
	}

	/*
	 * If the User is also a new one it will be saved to the database as well.
	 */
	private AppUser saveNewAppUser(User user, String userEmail) {
		AuthToken userAuthToken = AuthToken.createUserAuthToken();
		AppUser newAppUser = new AppUser(getContext().getApp(), userEmail,
				userAuthToken);
		user.addAppUser(newAppUser);

		UserDAO<ObjectId> userDAO = new UserDAOMongo();
		userDAO.save(user, getContext().getAppDeveloper().getEmailAddress());

		return newAppUser;
	}

	private void updateDTO(User user, AppUser newAppUser, UsersDTO_V0_4 userDTO) {
		userDTO.setUserAuthToken(newAppUser.getUserAuthToken().getToken());
		userDTO.setUserId(user.getId().toString());
	}
}
