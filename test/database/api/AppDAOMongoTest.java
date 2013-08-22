package database.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import models.api.scrobbles.App;
import models.api.scrobbles.AppDeveloper;
import models.api.scrobbles.AuthToken;

import org.bson.types.ObjectId;
import org.junit.Test;


import com.google.code.morphia.Key;
import com.mongodb.WriteResult;

import database.api.util.CleanDatabaseTest;


public class AppDAOMongoTest extends CleanDatabaseTest {
	@Test
	public void testSaveAndDelete() {
		App app1 = new App("Spotify", DEV_EMAIL);
		App app2 = new App("Deezer", DEV_EMAIL);

		AppDAO<ObjectId> appDao = new AppDAOMongo();
		Key<App> keySave = appDao.save(app1);
		appDao.save(app2);

		// updates a document using save()
		app1.setName("Rdio", DEV_EMAIL);
		Key<App> keySaveAgain = appDao.save(app1);
		// checks that it doesn't save twice
		assertEquals(keySave.getId(), keySaveAgain.getId());

		assertTrue(appDao.count() == 2);

		// assert that it updates the object
		App spotifyFromDatabase = appDao.findByName("Spotify");
		App rdioFromDatabase = appDao.findByName("Rdio");
		assertNull(spotifyFromDatabase);
		assertNotNull(rdioFromDatabase);

		// assert deletion is working properly
		appDao.delete(app1);
		appDao.delete(app2);
		// try to delete a document twice
		WriteResult writeResultDeleteTwice = appDao.delete(app2);
		assertNull(writeResultDeleteTwice.getError());

		assertTrue(appDao.count() == 0);
	}

	@Test
	public void testFindById() {
		App app1 = new App("Spotify", DEV_EMAIL);
		App app2 = new App("Deezer", DEV_EMAIL);

		AppDAO<ObjectId> appDao = new AppDAOMongo();
		appDao.save(app1);
		appDao.save(app2);

		assertTrue(appDao.findById(app1.getId()).equals(app1));
		assertTrue(appDao.findById(app2.getId()).equals(app2));
	}

	@Test
	public void testFindByName() {
		App app1 = new App("Spotify", DEV_EMAIL);
		App app2 = new App("Deezer", DEV_EMAIL);

		AppDAO<ObjectId> appDao = new AppDAOMongo();
		appDao.save(app1);
		appDao.save(app2);

		assertTrue(appDao.findByName(app1.getName()).equals(app1));
		assertTrue(appDao.findByName(app2.getName()).equals(app2));
	}

	@Test
	public void testFindByDevAuthToken() {
		App app = new App("Spotify", DEV_EMAIL);
		AuthToken authToken = AuthToken.createDevAuthToken();
		AppDeveloper appDev = new AppDeveloper("gabriel@example.com",
				"Gabriel", authToken, DEV_EMAIL);
		app.addAppDeveloper(appDev, DEV_EMAIL);

		AppDAOMongo appDao = new AppDAOMongo();
		appDao.cascadeSave(app);

		assertTrue(appDao.findByDevAuthToken(authToken.getToken()).equals(app));
	}

	@Test
	public void testFindAppDevByAuthToken() {
		App app = new App("Spotify", DEV_EMAIL);
		AuthToken authToken = AuthToken.createDevAuthToken();
		AppDeveloper appDev = new AppDeveloper("gabriel@example.com",
				"Gabriel", authToken, DEV_EMAIL);
		app.addAppDeveloper(appDev, DEV_EMAIL);

		AppDAOMongo appDao = new AppDAOMongo();
		appDao.cascadeSave(app);

		assertTrue(appDao.findAppDevByAuthToken(authToken.getToken()).equals(
				appDev));
	}

	@Test
	public void testFindByDevEmail() {
		App app = new App("Spotify", DEV_EMAIL);
		AuthToken authToken = AuthToken.createDevAuthToken();
		AppDeveloper appDev = new AppDeveloper("gabriel@example.com",
				"Gabriel", authToken, DEV_EMAIL);
		app.addAppDeveloper(appDev, DEV_EMAIL);

		AppDAOMongo appDao = new AppDAOMongo();
		appDao.cascadeSave(app);

		List<App> appsDatabase = appDao.findByDevEmail("gabriel@example.com");
		assertEquals(appsDatabase.size(), 1);
		assertTrue(appsDatabase.get(0).equals(app));
	}

	@Test
	public void testFindAppDevByEmail() {
		App app = new App("Spotify", DEV_EMAIL);
		AuthToken authToken = AuthToken.createDevAuthToken();
		AppDeveloper appDev = new AppDeveloper("gabriel@example.com",
				"Gabriel", authToken, DEV_EMAIL);
		app.addAppDeveloper(appDev, DEV_EMAIL);

		AppDAOMongo appDao = new AppDAOMongo();
		appDao.cascadeSave(app);

		List<AppDeveloper> appDevsDatabase = appDao
				.findAppDevByEmail("gabriel@example.com");
		assertEquals(appDevsDatabase.size(), 1);
		assertTrue(appDevsDatabase.get(0).equals(appDev));
	}

}
