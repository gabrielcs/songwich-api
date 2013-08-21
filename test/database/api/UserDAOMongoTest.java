package database.api;

import static org.junit.Assert.assertTrue;
import models.api.App;
import models.api.AppUser;
import models.api.AuthToken;
import models.api.User;

import org.junit.Test;

import database.api.util.CleanDatabaseTest;

public class UserDAOMongoTest extends CleanDatabaseTest {
	@Test
	public void testSaveAndDelete() {
		User user1 = new User("gabriel@example.com", "Gabriel Example");
		User user2 = new User("daniel@example.com", "Daniel Example");

		UserDAOMongo userDao = new UserDAOMongo();
		userDao.cascadeSave(user1);
		userDao.cascadeSave(user2);

		assertTrue(userDao.count() == 2);

		userDao.delete(user1);
		userDao.delete(user2);

		assertTrue(userDao.count() == 0);
	}

	@Test
	public void testFindById() {
		User user1 = new User("gabriel@example.com", "Gabriel Example");
		User user2 = new User("daniel@example.com", "Daniel Example");

		UserDAOMongo userDao = new UserDAOMongo();
		userDao.cascadeSave(user1);
		userDao.cascadeSave(user2);

		assertTrue(userDao.findById(user1.getId()).equals(user1));
		assertTrue(userDao.findById(user2.getId()).equals(user2));
	}

	@Test
	public void testFindByEmailAddress() {
		User user1 = new User("gabriel@example.com", "Gabriel Example");
		User user2 = new User("daniel@example.com", "Daniel Example");

		UserDAOMongo userDao = new UserDAOMongo();
		userDao.cascadeSave(user1);
		userDao.cascadeSave(user2);

		assertTrue(userDao.findByEmailAddress("gabriel@example.com").equals(
				user1));
		assertTrue(userDao.findByEmailAddress("daniel@example.com").equals(
				user2));
	}

	@Test
	public void testFindByEmailAddressWithMusicServiceUser() {
		App service1 = new App("Spotify", DEV_EMAIL);
		App service2 = new App("Rdio", DEV_EMAIL);

		User user1 = new User("gabriel@example.com", "Gabriel Example");
		AuthToken authToken = AuthToken.createUserAuthToken();
		AppUser service1User1 = new AppUser(service1, "gabriel@user.com",
				authToken, DEV_EMAIL);
		user1.addAppUser(service1User1, DEV_EMAIL);

		User user2 = new User("daniel@example.com", "Daniel Example");
		AuthToken authToken2 = AuthToken.createUserAuthToken();
		AppUser service2User2 = new AppUser(service2, "daniel@user.com",
				authToken2, DEV_EMAIL);
		user2.addAppUser(service2User2, DEV_EMAIL);

		UserDAOMongo userDao = new UserDAOMongo();
		userDao.cascadeSave(user1);
		userDao.cascadeSave(user2);

		assertTrue(userDao.findByEmailAddress("gabriel@user.com").equals(user1));
		assertTrue(userDao.findByEmailAddress("daniel@user.com").equals(user2));
	}

	@Test
	public void testFindByAuthToken() {
		User user = new User("gabriel@example.com", "Gabriel Example");
		App app = new App("Spotify", "dev@example.com");
		AuthToken authToken = AuthToken.createUserAuthToken();
		AppUser appUser = new AppUser(app, user.getEmailAddress(), authToken,
				app.getCreatedBy());
		user.addAppUser(appUser, DEV_EMAIL);

		UserDAOMongo userDao = new UserDAOMongo();
		userDao.cascadeSave(user);
		User userDatabase = userDao.findByUserAuthToken(authToken.getToken());

		assertTrue(userDatabase.equals(user));
	}

	@Test
	public void testFindAppUserByAuthToken() {
		User user = new User("gabriel@example.com", "Gabriel Example");
		App app = new App("Spotify", "dev@example.com");
		AuthToken authToken = AuthToken.createUserAuthToken();
		AppUser appUser = new AppUser(app, user.getEmailAddress(), authToken,
				app.getCreatedBy());
		user.addAppUser(appUser, DEV_EMAIL);

		UserDAOMongo userDao = new UserDAOMongo();
		userDao.cascadeSave(user);
		AppUser appUserDatabase = userDao.findAppUserByAuthToken(authToken
				.getToken());

		assertTrue(appUserDatabase.equals(appUser));
	}
}
