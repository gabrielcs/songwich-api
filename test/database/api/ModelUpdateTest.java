package database.api;

import models.api.scrobbles.App;
import models.api.scrobbles.AppDeveloper;
import models.api.scrobbles.AuthToken;

import static org.junit.Assert.*;

import org.bson.types.ObjectId;
import org.junit.Test;

import database.api.scrobbles.AppDAO;
import database.api.scrobbles.AppDAOMongo;
import database.api.util.CleanDatabaseTest;

public class ModelUpdateTest extends CleanDatabaseTest {
	private final static String UPDATE_DEV_EMAIL = "update@songwich.com";

	@Test
	public void testModelUpdate() {
		App app = new App("Spotify");
		AppDAO<ObjectId> appDao = new AppDAOMongo();
		appDao.save(app, DEV_EMAIL);

		// updates a document using save()
		AppDeveloper appDeveloper = new AppDeveloper(UPDATE_DEV_EMAIL,
				"Updater", AuthToken.createDevAuthToken());
		app.addAppDeveloper(appDeveloper);
		appDao.save(app, UPDATE_DEV_EMAIL);

		app = appDao.findById(app.getId());
		assertEquals(DEV_EMAIL, app.getCreatedBy());
		assertEquals(UPDATE_DEV_EMAIL, app.getLastModifiedBy());
		assertTrue(app.getCreatedAt() < app.getLastModifiedAt());
	}
}
