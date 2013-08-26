package database.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import models.api.scrobbles.Scrobble;
import models.api.scrobbles.Song;
import models.api.scrobbles.User;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import database.api.scrobbles.ScrobbleDAO;
import database.api.scrobbles.ScrobbleDAOMongo;
import database.api.scrobbles.UserDAOMongo;
import database.api.util.CleanDatabaseTest;

public class ScrobbleDAOMongoTest extends CleanDatabaseTest {

	private ScrobbleDAO<ObjectId> scrobbleDao;
	private Scrobble scrobble1, scrobble2;
	private User user1, user2;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		scrobbleDao = new ScrobbleDAOMongo();
		initData();
	}

	private void initData() {
		user1 = new User("gabriel@example.com", "Gabriel Example");
		user2 = new User("daniel@example.com", "Daniel Example");
		// ScrobbleDAOMongo is not a CascadeSaveDAO
		// it requires saving its references beforehand
		CascadeSaveDAO<User, ObjectId> userDao = new UserDAOMongo();
		userDao.cascadeSave(user1);
		userDao.cascadeSave(user2);

		List<String> artists1 = new ArrayList<String>();
		artists1.add("Passion Pit");
		List<String> artists2 = new ArrayList<String>();
		artists2.add("Daft Punk");
		artists2.add("Pharrell Williams");

		scrobble1 = new Scrobble(user1.getId(), new Song("Take a Walk",
				artists1), System.currentTimeMillis(), false, "Spotify",
				DEV_EMAIL);
		scrobble2 = new Scrobble(user2.getId(),
				new Song("Get Lucky", artists2), System.currentTimeMillis(),
				true, "Deezer", DEV_EMAIL);

		// saves the scrobbles
		scrobbleDao.save(scrobble1);
		scrobbleDao.save(scrobble2);
	}

	@Test
	public void testSaveAndDelete() {
		assertTrue(scrobbleDao.count() == 2);
		scrobbleDao.delete(scrobble1);
		scrobbleDao.delete(scrobble2);
		assertTrue(scrobbleDao.count() == 0);
	}

	@Test
	public void testFindById() {
		assertTrue(scrobbleDao.findById(scrobble1.getId()).equals(scrobble1));
		assertTrue(scrobbleDao.findById(scrobble2.getId()).equals(scrobble2));
	}

	@Test
	public void testFindByUserId() {
		List<Scrobble> scrobblesUser1 = scrobbleDao.findByUserId(user1.getId(), false);
		List<Scrobble> scrobblesUser2 = scrobbleDao.findByUserId(user2.getId(), false);

		assertEquals(scrobblesUser1.size(), 1);
		assertEquals(scrobblesUser2.size(), 1);

		assertTrue(scrobblesUser1.iterator().next().equals(scrobble1));
		assertTrue(scrobblesUser2.iterator().next().equals(scrobble2));
	}

	@Test
	public void testFindByUserIdWithDaysOffset() {
		Scrobble scrobble3 = new Scrobble(user1.getId(), new Song(
				"Are You Gonna Be My Girl", "Jet"), System.currentTimeMillis(),
				false, "Spotify", DEV_EMAIL);

		Calendar fiveDaysAgo = new GregorianCalendar();
		fiveDaysAgo.add(Calendar.DATE, -5);
		System.out.println(fiveDaysAgo);
		Scrobble scrobble5DaysOld = new Scrobble(user1.getId(), new Song(
				"Take a Walk (old)", "Passion Pit"),
				fiveDaysAgo.getTimeInMillis(), false, "Spotify", DEV_EMAIL);

		scrobbleDao.save(scrobble3);
		scrobbleDao.save(scrobble5DaysOld);

		List<Scrobble> scrobbles2DaysOld = scrobbleDao
				.findByUserIdWithDaysOffset(user1.getId(), 5, false);
		List<Scrobble> scrobbles1DayOld = scrobbleDao
				.findByUserIdWithDaysOffset(user1.getId(), 1, false);

		assertEquals(3, scrobbles2DaysOld.size());
		assertEquals(2, scrobbles1DayOld.size());
	}

}
