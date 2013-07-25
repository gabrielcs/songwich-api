package daos.api;

import java.util.Set;
import java.util.UUID;

import models.App;
import models.AppUser;
import models.User;

import org.bson.types.ObjectId;

import com.google.code.morphia.Datastore;

import daos.api.util.BasicDAOMongo;
import daos.api.util.CascadeSaveDAO;

public class UserDAOMongo extends BasicDAOMongo<User> implements
		UserDAO<ObjectId>, CascadeSaveDAO<User, ObjectId> {

	public UserDAOMongo() {
	}

	@Override
	public User findById(ObjectId id) {
		return ds.find(User.class).filter("id", id).get();
	}

	@Override
	public void cascadeSave(User t) {
		cascadeSaveMusicServices(t);
		save(t);
	}

	private void cascadeSaveMusicServices(User t) {
		if (t.getAppUsers().isEmpty()) {
			// there's nothing to save
			return;
		}

		// check if there are App's to save
		CascadeSaveDAO<App, ObjectId> musicServiceDAO = new AppDAOMongo();
		App musicService;

		Set<AppUser> musicServiceUsers = t.getAppUsers();
		for (AppUser musicServiceUser : musicServiceUsers) {
			musicService = musicServiceUser.getApp();
			if (musicService.getId() == null) {
				musicServiceDAO.save(musicService);
			}
		}
	}

	// TODO: test
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

	// TODO: test
	@Override
	public User findByUserAuthToken(UUID userAuthToken) {
		return ds.find(User.class)
				.filter("appUsers.userAuthToken", userAuthToken).get();
	}

	/*
	 * This might be a bit inefficient since it finds in the database and later 
	 * in memory.
	 * 
	 * TODO: test
	 */
	@Override
	public AppUser findAppUserByAuthToken(UUID userAuthToken) {
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
