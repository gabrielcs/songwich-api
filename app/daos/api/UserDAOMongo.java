package daos.api;

import java.util.Set;
import java.util.UUID;

import models.MusicService;
import models.MusicServiceUser;
import models.User;

import org.bson.types.ObjectId;

import com.google.code.morphia.Datastore;

import daos.api.util.BasicDAOMongo;
import daos.api.util.CascadeSaveDAO;

public class UserDAOMongo extends BasicDAOMongo<User> implements
		UserDAO<ObjectId>, CascadeSaveDAO<User, ObjectId> {

	public UserDAOMongo(Datastore ds) {
		super(ds);
	}

	@Override
	public void cascadeSave(User t) {
		cascadeSaveMusicServices(t);
		save(t);
	}

	private void cascadeSaveMusicServices(User t) {
		if (t.getMusicServiceUsers().isEmpty()) {
			// there's nothing to save
			return;
		}

		// check if there are MusicService's to save
		CascadeSaveDAO<MusicService, ObjectId> musicServiceDAO = new MusicServiceDAOMongo(ds);
		MusicService musicService;

		Set<MusicServiceUser> musicServiceUsers = t.getMusicServiceUsers();
		for (MusicServiceUser musicServiceUser : musicServiceUsers) {
			musicService = musicServiceUser.getStreamingService();
			if (musicService.getId() == null) {
				musicServiceDAO.save(musicService);
			}
		}
	}

	// TODO: test
	@Override
	public User findByUserAuthToken(UUID userAuthToken) {
		return ds.find(User.class).filter("userAuthToken", userAuthToken).get();
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
				.filter("musicServiceUsers.emailAddress", emailAddress).get();
	}

	@Override
	public User findById(ObjectId id) {
		return ds.find(User.class).filter("id", id).get();
	}
}
