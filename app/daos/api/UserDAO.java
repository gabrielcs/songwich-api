package daos.api;

import java.util.UUID;

import models.User;

import com.google.code.morphia.dao.DAO;

public interface UserDAO<I> extends DAO<User, I> {
	
	public User findById(I id);

	public User findByUserAuthToken(UUID userAuthToken);

	public User findByEmailAddress(String emailAddress);
}
