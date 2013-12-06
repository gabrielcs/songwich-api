package database.api.scrobbles;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import models.api.scrobbles.App;
import models.api.scrobbles.AppUser;
import models.api.scrobbles.User;

import org.bson.types.ObjectId;

import com.google.code.morphia.Key;
import com.google.code.morphia.query.Query;

import database.api.BasicDAOMongo;
import database.api.CascadeSaveDAO;

public class UserDAOMongo extends BasicDAOMongo<User> implements
		UserDAO<ObjectId>, CascadeSaveDAO<User, ObjectId> {

	public UserDAOMongo() {
	}

	@Override
	public User findById(ObjectId id) {
		Query<User> query = ds.find(User.class).filter("id", id);
		filterDeactivated(query);
		return query.get();
	}

	@Override
	public List<User> findUsersByIds(Collection<ObjectId> ids) {
		Query<User> query = ds.find(User.class).filter("id in", ids);
		filterDeactivated(query);
		return query.asList();
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
		Query<User> query = ds.find(User.class).filter("emailAddress",
				emailAddress);
		filterDeactivated(query);
		User user = query.get();
		if (user != null) {
			return user;
		}

		// it might be an alternative email address
		query = ds.find(User.class).filter("appUsers.userEmailAddress",
				emailAddress);
		filterDeactivated(query);
		return query.get();
	}

	@Override
	public User findByUserAuthToken(String userAuthToken) {
		Query<User> query = ds.find(User.class).filter(
				"appUsers.userAuthToken.token", userAuthToken);
		filterDeactivated(query);
		return query.get();

		/*
		 * // search for it in the deprecated field return ds.find(User.class)
		 * .filter("appUsers.userAuthToken", userAuthToken).get();
		 */
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

	private Query<User> filterDeactivated(Query<User> query) {
		Boolean deactivated = true;
		return query.filter("deactivated !=", deactivated);
	}
}
