package database.api;

import models.api.AppUser;
import models.api.User;

import com.google.code.morphia.dao.DAO;

public interface UserDAO<I> extends DAO<User, I> {
	
	public User findById(I id);

	public User findByUserAuthToken(String userAuthToken);

	public User findByEmailAddress(String emailAddress);
	
	public AppUser findAppUserByAuthToken(String userAuthToken);
}
