package database.api.scrobbles;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import models.api.scrobbles.App;
import models.api.scrobbles.AppUser;
import models.api.scrobbles.User;

import org.bson.types.ObjectId;

import com.google.code.morphia.Key;

import database.api.BasicDAOMongo;
import database.api.CascadeSaveDAO;

public class UserDAOMongo extends BasicDAOMongo<User> implements
		UserDAO<ObjectId>, CascadeSaveDAO<User, ObjectId> {

	public UserDAOMongo() {
	}

	@Override
	public User findById(ObjectId id) {
		return ds.find(User.class).filter("id", id).get();
	}
	
	@Override
	public List<User> findUsersByIds(Collection<ObjectId> ids) {
		return ds.find(User.class).filter("id in", ids).asList();
	}

	@Override
	public Key<User> cascadeSave(User t, String devEmail) {
		cascadeSaveAppUser(t, devEmail);
		return save(t, devEmail);
	}

	private void cascadeSaveAppUser(User t, String devEmail) {
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
			if (!app.isModelPersisted() || app.isModelUpdated()) {
				appDAO.save(app, devEmail);
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
		User user = ds.find(User.class)
				.filter("appUsers.statefulUserAuthToken.token", userAuthToken)
				.get();
		if (user != null) {
			return user;
		}

		// search for it in the deprecated field
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
		if (user == null) {
			// the userAuthToken is not in the database
			return null;
		}

		for (AppUser appUser : user.getAppUsers()) {
			if (appUser.getUserAuthToken().getToken().equals(userAuthToken)) {
				return appUser;
			}
		}
		// shouldn't reach this point
		return null;
	}
}
