package daos.api;

import java.util.UUID;

import models.User;
import daos.api.util.DatabaseId;
import daos.api.util.DomainAccessObject;

public interface UserDAO extends DomainAccessObject<User> {

	public void save(User user);
	
	public void update(User user);
	
	public User findById(DatabaseId id);
	
	public User findByUserAuthToken(UUID userAuthToken);
	
	public User findByEmailAddress(String emailAddress);
}
