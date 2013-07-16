package daos.api;

import java.util.UUID;

import models.User;

import daos.api.util.DAOMongo;

public class UserDAOMongo 
	//extends DAOMongo<User> 
	// implements UserDAO 
{
/*
	public static void save(User user) {
		user.insert();
	}
	
	public static void update(User user)  {
		user.update();
	}
	
	public static User findById(DatabaseId id) {
		return find.where().eq("id", id).findUnique();
	}
	
	public static User findByUserAuthToken(UUID userAuthToken) {
		return find.where().eq("musicServiceUsers.userAuthToken", userAuthToken).findUnique();
	}
	
	public static User findByEmailAddress(String emailAddress) {
		User user = find.where().eq("emailAddress", emailAddress).findUnique();
		if (user != null) {
			return user;
		}
		// it might be an alternative email address
		return find.where().eq("musicServiceUsers.emailAddress", emailAddress).findUnique();
	}
*/
}
