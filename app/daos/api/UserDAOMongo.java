package daos.api;

import java.util.UUID;

import models.User;

import org.bson.types.ObjectId;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.dao.BasicDAO;

public class UserDAOMongo extends BasicDAO<User, ObjectId> implements UserDAO<ObjectId> {

	public UserDAOMongo(Datastore ds) {
		super(ds);
	}

	// TODO: test
	@Override
	public User findByUserAuthToken(UUID userAuthToken) {
		return ds.find(User.class).filter("userAuthToken", userAuthToken).get();
	}

	@Override
	public User findByEmailAddress(String emailAddress) {
		User user = ds.find(User.class).filter("emailAddress", emailAddress).get();
		if (user != null) {
			return user;
		}
		
		// TODO: fix
		// it might be an alternative email address
		return ds.find(User.class).filter("musicServiceUsers.emailAddress", emailAddress).get();
	}

	@Override
	public User findById(ObjectId id) {
		return ds.find(User.class).filter("id", id).get();
	}
}
