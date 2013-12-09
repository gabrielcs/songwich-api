package database.api.scrobbles;

import java.util.Collection;
import java.util.List;

import models.api.scrobbles.AppUser;
import models.api.scrobbles.User;
import database.api.SongwichDAO;

public interface UserDAO<I> extends SongwichDAO<User, I> {

	public User findById(I id);

	public User findById(I id, boolean nonDeactivatedOnly);

	public List<User> findUsersByIds(Collection<I> ids);

	public List<User> findUsersByIds(Collection<I> ids,
			boolean nonDeactivatedOnly);

	public User findByUserAuthToken(String userAuthToken);

	public User findByUserAuthToken(String userAuthToken,
			boolean nonDeactivatedOnly);

	public User findByEmailAddress(String emailAddress);

	public User findByEmailAddress(String emailAddress,
			boolean nonDeactivatedOnly);

	public AppUser findAppUserByAuthToken(String userAuthToken);

	public AppUser findAppUserByAuthToken(String userAuthToken,
			boolean nonDeactivatedOnly);
}
