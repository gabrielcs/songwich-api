package database.api.scrobbles;

import java.util.Collection;
import java.util.List;

import models.api.scrobbles.AppUser;
import models.api.scrobbles.User;
import database.api.SongwichDAO;

public interface UserDAO<I> extends SongwichDAO<User, I> {
	
	public User findById(I id);
	
	public List<User> findUsersByIds(Collection<I> ids);

	public User findByUserAuthToken(String userAuthToken);

	public User findByEmailAddress(String emailAddress);
	
	public AppUser findAppUserByAuthToken(String userAuthToken);
}
