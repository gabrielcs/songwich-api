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
import com.google.code.morphia.query.QueryResults;

import database.api.BasicDAOMongo;
import database.api.CascadeSaveDAO;

public class UserDAOMongo extends BasicDAOMongo<User> implements
		UserDAO<ObjectId>, CascadeSaveDAO<User, ObjectId> {

	public UserDAOMongo() {
	}

	/** Finds all User's that are not deactivated */
	@Override
	public QueryResults<User> find() {
		Query<User> query = ds.find(User.class);
		filterDeactivated(query);
		return query;
	}

	/** Counts User's that are not deactivated */
	@Override
	public long count() {
		Query<User> query = ds.find(User.class);
		filterDeactivated(query);
		return super.count(query);
	}

	@Override
	public User findById(ObjectId id) {
		Query<User> query = queryById(id);
		filterDeactivated(query);
		return query.get();
	}

	@Override
	public User findById(ObjectId id, boolean nonDeactivatedOnly) {
		if (nonDeactivatedOnly) {
			return findById(id);
		}
		// includes deactivated users
		Query<User> query = queryById(id);
		return query.get();
	}

	private Query<User> queryById(Object id) {
		return ds.find(User.class).filter("id", id);
	}

	@Override
	public List<User> findUsersByIds(Collection<ObjectId> ids) {
		return queryUsersByIds(ids, true).asList();
	}

	@Override
	public List<User> findUsersByIds(Collection<ObjectId> ids,
			boolean nonDeactivatedOnly) {
		return queryUsersByIds(ids, nonDeactivatedOnly).asList();
	}

	private Query<User> queryUsersByIds(Collection<ObjectId> ids,
			boolean nonDeactivatedOnly) {
		//Query<User> query = ds.find(User.class).filter("id in", ids);
		Query<User> query = ds.find(User.class).field("id").hasAnyOf(ids);
		if (nonDeactivatedOnly) {
			filterDeactivated(query);
		}
		return query;
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
		return queryByEmailAddress(emailAddress, true).get();
	}

	@Override
	public User findByEmailAddress(String emailAddress,
			boolean nonDeactivatedOnly) {
		return queryByEmailAddress(emailAddress, nonDeactivatedOnly).get();
	}

	private Query<User> queryByEmailAddress(String emailAddress,
			boolean nonDeactivatedOnly) {
		Query<User> query = ds.find(User.class).filter("emailAddress",
				emailAddress);
		filterDeactivated(query);
		User user = query.get();
		if (user != null) {
			return query;
		}

		// it might be an alternative email address
		query = ds.find(User.class).filter("appUsers.userEmailAddress",
				emailAddress);

		if (nonDeactivatedOnly) {
			filterDeactivated(query);
		}
		return query;
	}

	@Override
	public User findByUserAuthToken(String userAuthToken) {
		return queryByUserAuthToken(userAuthToken, true).get();
	}

	@Override
	public User findByUserAuthToken(String userAuthToken,
			boolean nonDeactivatedOnly) {
		return queryByUserAuthToken(userAuthToken, nonDeactivatedOnly).get();
	}

	private Query<User> queryByUserAuthToken(String userAuthToken,
			boolean nonDeactivatedOnly) {
		/*
		 * // search for it in the deprecated field return ds.find(User.class)
		 * .filter("appUsers.userAuthToken", userAuthToken).get();
		 */

		Query<User> query = ds.find(User.class).filter(
				"appUsers.userAuthToken.token", userAuthToken);
		if (nonDeactivatedOnly) {
			filterDeactivated(query);
		}
		return query;
	}

	/*
	 * This might be a bit inefficient since it finds in the database and later
	 * in memory.
	 */
	@Override
	public AppUser findAppUserByAuthToken(String userAuthToken) {
		User user = findByUserAuthToken(userAuthToken, true);
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
	
	@Override
	public AppUser findAppUserByAuthToken(String userAuthToken,
			boolean nonDeactivatedOnly) {
		User user = findByUserAuthToken(userAuthToken, nonDeactivatedOnly);
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
		//return query.filter("deactivated !=", deactivated);
		return query.field("deactivated").notEqual(deactivated);
	}
}
