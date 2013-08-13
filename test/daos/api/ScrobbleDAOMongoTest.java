package daos.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import models.api.Scrobble;
import models.api.User;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import usecases.api.util.DatabaseContext;
import database.api.ScrobbleDAO;
import database.api.ScrobbleDAOMongo;
import database.api.UserDAOMongo;
import database.api.util.CascadeSaveDAO;

public class ScrobbleDAOMongoTest {

	private static final String CREATED_BY = "developers@songwich.com";
	private String dbName = "songwich-api-test";

	@Before
	public void setUp() throws Exception {
		DatabaseContext.createDatastore(dbName);
	}

	@After
	public void tearDown() throws Exception {
		DatabaseContext.dropDatabase();
	}

	@Test
	public void testSaveAndDelete() {
		User user1 = new User("gabriel@example.com", "Gabriel Example");
		User user2 = new User("daniel@example.com", "Daniel Example");
		CascadeSaveDAO<User, ObjectId> userDao = new UserDAOMongo();
		userDao.cascadeSave(user1);
		userDao.cascadeSave(user2);

		List<String> artists1 = new ArrayList<String>();
		artists1.add("Passion Pit");

		List<String> artists2 = new ArrayList<String>();
		artists2.add("Daft Punk");
		artists2.add("Pharrell Williams");

		Scrobble scrobble1 = new Scrobble(user1.getId(), "Take a Walk",
				artists1, System.currentTimeMillis(), false, "Spotify",
				CREATED_BY);
		Scrobble scrobble2 = new Scrobble(user2.getId(), "Get Lucky", artists2,
				System.currentTimeMillis(), true, "Deezer", CREATED_BY);

		// ScrobbleDAOMongo is not a CascadeSaveDAO
		// it requires saving its references beforehand
		ScrobbleDAO<ObjectId> scrobbleDao = new ScrobbleDAOMongo();
		scrobbleDao.save(scrobble1);
		scrobbleDao.save(scrobble2);

		assertTrue(scrobbleDao.count() == 2);

		scrobbleDao.delete(scrobble1);
		scrobbleDao.delete(scrobble2);

		assertTrue(scrobbleDao.count() == 0);
	}

	@Test
	public void testFindById() {
		User user1 = new User("gabriel@example.com", "Gabriel Example");
		User user2 = new User("daniel@example.com", "Daniel Example");
		CascadeSaveDAO<User, ObjectId> userDao = new UserDAOMongo();
		userDao.cascadeSave(user1);
		userDao.cascadeSave(user2);

		List<String> artists1 = new ArrayList<String>();
		artists1.add("Passion Pit");

		List<String> artists2 = new ArrayList<String>();
		artists2.add("Daft Punk");
		artists2.add("Pharrell Williams");

		Scrobble scrobble1 = new Scrobble(user1.getId(), "Take a Walk",
				artists1, System.currentTimeMillis(), false, "Spotify",
				CREATED_BY);
		Scrobble scrobble2 = new Scrobble(user2.getId(), "Get Lucky", artists2,
				System.currentTimeMillis(), true, "Deezer", CREATED_BY);

		// ScrobbleDAOMongo is not a CascadeSaveDAO
		// it requires saving its references beforehand
		ScrobbleDAO<ObjectId> scrobbleDao = new ScrobbleDAOMongo();
		scrobbleDao.save(scrobble1);
		scrobbleDao.save(scrobble2);

		assertTrue(scrobbleDao.findById(scrobble1.getId()).equals(scrobble1));
		assertTrue(scrobbleDao.findById(scrobble2.getId()).equals(scrobble2));
	}

	@Test
	public void testFindByUserId() {
		User user1 = new User("gabriel@example.com", "Gabriel Example");
		User user2 = new User("daniel@example.com", "Daniel Example");
		CascadeSaveDAO<User, ObjectId> userDao = new UserDAOMongo();
		userDao.cascadeSave(user1);
		userDao.cascadeSave(user2);

		List<String> artists2 = new ArrayList<String>();
		artists2.add("Daft Punk");
		artists2.add("Pharrell Williams");

		Scrobble scrobble1 = new Scrobble(user1.getId(), "Take a Walk",
				"Passion Pit", System.currentTimeMillis(), false, "Spotify",
				CREATED_BY);
		Scrobble scrobble2 = new Scrobble(user2.getId(), "Get Lucky", artists2,
				System.currentTimeMillis(), true, "Deezer", CREATED_BY);

		// ScrobbleDAOMongo is not a CascadeSaveDAO
		// it requires saving its references beforehand
		ScrobbleDAO<ObjectId> scrobbleDao = new ScrobbleDAOMongo();
		scrobbleDao.save(scrobble1);
		scrobbleDao.save(scrobble2);

		System.out.println(scrobbleDao.find().asList());

		List<Scrobble> scrobblesUser1 = scrobbleDao.findByUserId(user1.getId());
		List<Scrobble> scrobblesUser2 = scrobbleDao.findByUserId(user2.getId());

		assertEquals(scrobblesUser1.size(), 1);
		assertEquals(scrobblesUser2.size(), 1);

		assertTrue(scrobblesUser1.iterator().next().equals(scrobble1));
		assertTrue(scrobblesUser2.iterator().next().equals(scrobble2));
	}

	@Test
	public void testFindByUserIdWithOffset() {
		User user = new User("gabriel@example.com", "Gabriel Example");
		CascadeSaveDAO<User, ObjectId> userDao = new UserDAOMongo();
		userDao.cascadeSave(user);

		Scrobble scrobble1 = new Scrobble(user.getId(), "Take a Walk",
				"Passion Pit", System.currentTimeMillis(), false, "Spotify",
				CREATED_BY);

		Scrobble scrobble2 = new Scrobble(user.getId(), "Take a Walk 2",
				"Passion Pit", System.currentTimeMillis(), false, "Spotify",
				CREATED_BY);

		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.DATE, -2);
		Scrobble scrobble2DaysOld = new Scrobble(user.getId(),
				"Take a Walk (old)", "Passion Pit", calendar.getTimeInMillis(),
				false, "Spotify", CREATED_BY);

		// ScrobbleDAOMongo is not a CascadeSaveDAO
		// it requires saving its references beforehand
		ScrobbleDAO<ObjectId> scrobbleDao = new ScrobbleDAOMongo();
		scrobbleDao.save(scrobble1);
		scrobbleDao.save(scrobble2);
		scrobbleDao.save(scrobble2DaysOld);
		
		List<Scrobble> scrobbles1DayOld = scrobbleDao.findByUserIdWithDaysOffset(user.getId(), 1);
		List<Scrobble> scrobbles2DaysOld = scrobbleDao.findByUserIdWithDaysOffset(user.getId(), 2);

		assertEquals(scrobbles1DayOld.size(), 2);
		assertEquals(scrobbles2DaysOld.size(), 3);
	}

}
