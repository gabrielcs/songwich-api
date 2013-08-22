package behavior.api.usecases;

import models.api.scrobbles.AppUser;
import models.api.scrobbles.AuthToken;
import models.api.scrobbles.User;

import org.bson.types.ObjectId;

import behavior.api.util.MyLogger;

import views.api.UsersDTO_V0_4;
import database.api.UserDAO;
import database.api.UserDAOMongo;

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
									.getAppDeveloper().getDevAuthToken().getToken()));
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
		String appDevEmail = getContext().getAppDeveloper().getEmailAddress();

		AuthToken userAuthToken = AuthToken.createUserAuthToken();
		AppUser newAppUser = new AppUser(getContext().getApp(), userEmail,
				userAuthToken, appDevEmail);
		user.addAppUser(newAppUser, appDevEmail);

		UserDAO<ObjectId> userDAO = new UserDAOMongo();
		userDAO.save(user);

		return newAppUser;
	}

	private void updateDTO(User user, AppUser newAppUser, UsersDTO_V0_4 userDTO) {
		userDTO.setUserAuthToken(newAppUser.getUserAuthToken().getToken());
		userDTO.setUserId(user.getId().toString());
	}
}
