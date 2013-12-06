package database.api;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.api.scrobbles.App;
import models.api.scrobbles.AppUser;
import models.api.scrobbles.AuthToken;
import models.api.scrobbles.User;

import org.bson.types.ObjectId;
import org.junit.Test;

import util.api.WithRequestContext;
import database.api.scrobbles.UserDAOMongo;

public class UserDAOMongoTest extends WithRequestContext {
	@Test
	public void testSaveAndDelete() {
		User user1 = new User("gabriel@example.com", "Gabriel Example");
		User user2 = new User("daniel@example.com", "Daniel Example");

		UserDAOMongo userDao = new UserDAOMongo();
		userDao.cascadeSave(user1, getContext().getAppDeveloper().getEmailAddress());
		userDao.cascadeSave(user2, getContext().getAppDeveloper().getEmailAddress());

		assertTrue(userDao.count() == 2);

		userDao.delete(user1);
		userDao.delete(user2);

		assertTrue(userDao.count() == 0);
	}
	
	@Test
	public void testCountAndDeactivate() {
		User user1 = new User("gabriel@example.com", "Gabriel Example");
		User user2 = new User("daniel@example.com", "Daniel Example");

		UserDAOMongo userDao = new UserDAOMongo();
		userDao.cascadeSave(user1, getContext().getAppDeveloper().getEmailAddress());
		userDao.cascadeSave(user2, getContext().getAppDeveloper().getEmailAddress());

		assertEquals(2, userDao.count());
		assertEquals(2, userDao.find().asList().size());
		
		user1.setDeactivated(true);
		userDao.save(user1, getContext().getAppDeveloper().getEmailAddress());
		user2.setDeactivated(true);
		userDao.save(user2, getContext().getAppDeveloper().getEmailAddress());
		
		assertEquals(0, userDao.count());
		assertEquals(0, userDao.find().asList().size());
		
		assertNull(userDao.findById(user1.getId()));
		assertNull(userDao.findById(user2.getId()));
	}

	@Test
	public void testFindById() {
		User user1 = new User("gabriel@example.com", "Gabriel Example");
		User user2 = new User("daniel@example.com", "Daniel Example");

		UserDAOMongo userDao = new UserDAOMongo();
		userDao.cascadeSave(user1, getContext().getAppDeveloper().getEmailAddress());
		userDao.cascadeSave(user2, getContext().getAppDeveloper().getEmailAddress());

		assertTrue(userDao.findById(user1.getId()).equals(user1));
		assertTrue(userDao.findById(user2.getId()).equals(user2));
	}
	
	@Test
	public void testFindByUsersIds() {
		User user1 = new User("gabriel@example.com", "Gabriel Example");
		User user2 = new User("daniel@example.com", "Daniel Example");

		UserDAOMongo userDao = new UserDAOMongo();
		userDao.cascadeSave(user1, getContext().getAppDeveloper().getEmailAddress());
		userDao.cascadeSave(user2, getContext().getAppDeveloper().getEmailAddress());

		Set<ObjectId> usersIds = new HashSet<ObjectId>(2);
		usersIds.add(user1.getId());
		usersIds.add(user2.getId());
		
		List<User> users = userDao.findUsersByIds(usersIds);
		assertEquals(2, users.size());
		assertTrue(users.contains(user1));
		assertTrue(users.contains(user2));
	}

	@Test
	public void testFindByEmailAddress() {
		User user1 = new User("gabriel@example.com", "Gabriel Example");
		User user2 = new User("daniel@example.com", "Daniel Example");

		UserDAOMongo userDao = new UserDAOMongo();
		userDao.cascadeSave(user1, getContext().getAppDeveloper().getEmailAddress());
		userDao.cascadeSave(user2, getContext().getAppDeveloper().getEmailAddress());

		assertTrue(userDao.findByEmailAddress("gabriel@example.com").equals(
				user1));
		assertTrue(userDao.findByEmailAddress("daniel@example.com").equals(
				user2));
	}

	@Test
	public void testFindByEmailAddressWithMusicServiceUser() {
		App service1 = new App("Spotify");
		App service2 = new App("Rdio");

		User user1 = new User("gabriel@example.com", "Gabriel Example");
		AuthToken authToken = AuthToken.createUserAuthToken();
		AppUser service1User1 = new AppUser(service1, "gabriel@user.com",
				authToken);
		user1.addAppUser(service1User1);

		User user2 = new User("daniel@example.com", "Daniel Example");
		AuthToken authToken2 = AuthToken.createUserAuthToken();
		AppUser service2User2 = new AppUser(service2, "daniel@user.com",
				authToken2);
		user2.addAppUser(service2User2);

		UserDAOMongo userDao = new UserDAOMongo();
		userDao.cascadeSave(user1, getContext().getAppDeveloper().getEmailAddress());
		userDao.cascadeSave(user2, getContext().getAppDeveloper().getEmailAddress());

		assertTrue(userDao.findByEmailAddress("gabriel@user.com").equals(user1));
		assertTrue(userDao.findByEmailAddress("daniel@user.com").equals(user2));
	}

	@Test
	public void testFindByAuthToken() {
		User user = new User("gabriel@example.com", "Gabriel Example");
		App app = new App("Spotify");
		AuthToken authToken = AuthToken.createUserAuthToken();
		AppUser appUser = new AppUser(app, user.getEmailAddress(), authToken);
		user.addAppUser(appUser);

		UserDAOMongo userDao = new UserDAOMongo();
		userDao.cascadeSave(user, getContext().getAppDeveloper().getEmailAddress());
		User userDatabase = userDao.findByUserAuthToken(authToken.getToken());

		assertTrue(userDatabase.equals(user));
	}

	@Test
	public void testFindAppUserByAuthToken() {
		User user = new User("gabriel@example.com", "Gabriel Example");
		App app = new App("Spotify");
		AuthToken authToken = AuthToken.createUserAuthToken();
		AppUser appUser = new AppUser(app, user.getEmailAddress(), authToken);
		user.addAppUser(appUser);

		UserDAOMongo userDao = new UserDAOMongo();
		userDao.cascadeSave(user, getContext().getAppDeveloper().getEmailAddress());
		AppUser appUserDatabase = userDao.findAppUserByAuthToken(authToken
				.getToken());

		assertTrue(appUserDatabase.equals(appUser));
	}
}
