package database.api;

import java.util.Set;

import models.api.App;
import models.api.AppUser;
import models.api.User;

import org.bson.types.ObjectId;

import com.google.code.morphia.Key;

import database.api.util.BasicDAOMongo;
import database.api.util.CascadeSaveDAO;

public class UserDAOMongo extends BasicDAOMongo<User> implements
		UserDAO<ObjectId>, CascadeSaveDAO<User, ObjectId> {

	public UserDAOMongo() {
	}

	@Override
	public User findById(ObjectId id) {
		return ds.find(User.class).filter("id", id).get();
	}

	@Override
	public Key<User> cascadeSave(User t) {
		cascadeSaveAppUser(t);
		return save(t);
	}

	private void cascadeSaveAppUser(User t) {
		if (t.getAppUsers().isEmpty()) {
			// there's nothing to save
			return;
		}

		// check if there are App's to save
		CascadeSaveDAO<App, ObjectId> appDAO = new AppDAOMongo();
		App app;

		Set<AppUser> appUsers = t.getAppUsers();
		for (AppUser appUser : appUsers) {
			app = appUser.getApp();
			if (app.getId() == null) {
				appDAO.save(app);
			}
		}
	}

	@Override
	public User findByEmailAddress(String emailAddress) {
		User user = ds.find(User.class).filter("emailAddress", emailAddress)
				.get();
		if (user != null) {
			return user;
		}

		// it might be an alternative email address
		return ds.find(User.class)
				.filter("appUsers.userEmailAddress", emailAddress).get();
	}

	@Override
	public User findByUserAuthToken(String userAuthToken) {
		return ds.find(User.class)
				.filter("appUsers.userAuthToken", userAuthToken).get();
	}

	/*
	 * This might be a bit inefficient since it finds in the database and later 
	 * in memory.
	 */
	@Override
	public AppUser findAppUserByAuthToken(String userAuthToken) {
		User user = findByUserAuthToken(userAuthToken);

		for (AppUser appUser : user.getAppUsers()) {
			if (appUser.getUserAuthToken().equals(userAuthToken)) {
				return appUser;
			}
		}
		// shouldn't reach this point
		return null;
	}
}