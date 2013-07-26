package daos.api;

import models.AppUser;
import models.User;

import com.google.code.morphia.dao.DAO;

public interface UserDAO<I> extends DAO<User, I> {
	
	public User findById(I id);

	public User findByUserAuthToken(String userAuthToken);

	public User findByEmailAddress(String emailAddress);
	
	public AppUser findAppUserByAuthToken(String userAuthToken);
}
