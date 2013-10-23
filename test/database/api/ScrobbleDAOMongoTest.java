package database.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	private Scrobble scrobble1, scrobble2, scrobble3, scrobble4, scrobble5,
			scrobble6;
	private User user1, user2, user3;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		scrobbleDao = new ScrobbleDAOMongo();
		initData();
	}

	private void initData() {
		user1 = new User("gabriel@example.com", "Gabriel Example");
		user2 = new User("daniel@example.com", "Daniel Example");
		user3 = new User("caon@example.com", "Caon Example");
		// ScrobbleDAOMongo is not a CascadeSaveDAO
		// it requires saving its references beforehand
		CascadeSaveDAO<User, ObjectId> userDao = new UserDAOMongo();
		userDao.cascadeSave(user1, DEV_EMAIL);
		userDao.cascadeSave(user2, DEV_EMAIL);
		userDao.cascadeSave(user3, DEV_EMAIL);

		List<String> artists1 = new ArrayList<String>();
		artists1.add("Passion Pit");
		List<String> artists2 = new ArrayList<String>();
		artists2.add("Daft Punk");
		artists2.add("Pharrell Williams");

		Calendar fiveDaysAgo = new GregorianCalendar();
		fiveDaysAgo.add(Calendar.DATE, -5);
		scrobble1 = new Scrobble(user1.getId(), new Song("Take a Walk",
				artists1), fiveDaysAgo.getTimeInMillis(), false, "Spotify");
		scrobbleDao.save(scrobble1, DEV_EMAIL);

		Calendar threeDaysAgo = new GregorianCalendar();
		threeDaysAgo.add(Calendar.DATE, -3);
		scrobble2 = new Scrobble(user2.getId(),
				new Song("Get Lucky", artists2),
				threeDaysAgo.getTimeInMillis(), true, "Deezer");
		scrobbleDao.save(scrobble2, DEV_EMAIL);

		scrobble3 = new Scrobble(user1.getId(), new Song("Take a Walk 2",
				artists1), System.currentTimeMillis(), true, "Spotify");
		scrobbleDao.save(scrobble3, DEV_EMAIL);

		scrobble4 = new Scrobble(user2.getId(), new Song("Get Lucky 2",
				artists2), System.currentTimeMillis(), true, "Deezer");
		scrobbleDao.save(scrobble4, DEV_EMAIL);

		scrobble5 = new Scrobble(user3.getId(), new Song("Get Lucky 3",
				artists2), System.currentTimeMillis(), true, "Deezer");
		scrobbleDao.save(scrobble5, DEV_EMAIL);

		scrobble6 = new Scrobble(user1.getId(), new Song("Take a Walk 3",
				artists1), System.currentTimeMillis(), false, "Spotify");
		scrobbleDao.save(scrobble6, DEV_EMAIL);
	}

	@Test
	public void testSaveAndDelete() {
		assertTrue(scrobbleDao.count() == 6);
		scrobbleDao.delete(scrobble1);
		scrobbleDao.delete(scrobble3);
		assertTrue(scrobbleDao.count() == 4);
	}

	@Test
	public void testFindById() {
		assertTrue(scrobbleDao.findById(scrobble1.getId()).equals(scrobble1));
		assertTrue(scrobbleDao.findById(scrobble2.getId()).equals(scrobble2));
	}

	@Test
	public void testFindByUserId() {
		List<Scrobble> scrobblesUser1 = scrobbleDao.findByUserId(user1.getId(),
				false);
		List<Scrobble> scrobblesUser2 = scrobbleDao.findByUserId(user2.getId(),
				false);
		List<Scrobble> scrobblesUser3 = scrobbleDao.findByUserId(user3.getId(),
				false);

		assertEquals(scrobblesUser1.size(), 3);
		assertEquals(scrobblesUser2.size(), 2);
		assertEquals(scrobblesUser3.size(), 1);

		assertTrue(scrobblesUser1.iterator().next().equals(scrobble6));
		assertTrue(scrobblesUser1.contains(scrobble3));
		assertTrue(scrobblesUser1.contains(scrobble1));

		assertTrue(scrobblesUser2.iterator().next().equals(scrobble4));
		assertTrue(scrobblesUser2.contains(scrobble2));

		assertTrue(scrobblesUser3.iterator().next().equals(scrobble5));
	}

	@Test
	public void chosenByUserOnly() {
		List<Scrobble> scrobblesUser1 = scrobbleDao.findByUserId(user1.getId(),
				true);
		assertEquals(scrobblesUser1.size(), 1);
		assertTrue(scrobblesUser1.contains(scrobble3));
		
		List<Scrobble> scrobblesUser2 = scrobbleDao.findByUserId(user2.getId(),
				true);
		assertEquals(scrobblesUser2.size(), 2);
		assertTrue(scrobblesUser2.contains(scrobble2));
		assertTrue(scrobblesUser2.contains(scrobble4));
		
		List<Scrobble> scrobblesUser3 = scrobbleDao.findByUserId(user3.getId(),
				true);
		assertEquals(scrobblesUser3.size(), 1);
		assertTrue(scrobblesUser3.contains(scrobble5));
	}

	@Test
	public void testFindLastScrobblesByUserId() {
		List<Scrobble> scrobblesUser1 = scrobbleDao.findLastScrobblesByUserId(
				user1.getId(), 2, false);

		assertEquals(scrobblesUser1.size(), 2);
		assertTrue(scrobblesUser1.iterator().next().equals(scrobble6));
		assertTrue(scrobblesUser1.contains(scrobble3));
	}

	@Test
	public void testFindLastScrobblesByUserIds() {
		Set<ObjectId> userIds = new HashSet<ObjectId>();
		userIds.add(user1.getId());
		userIds.add(user2.getId());
		List<Scrobble> scrobblesUser1User2 = scrobbleDao
				.findLastScrobblesByUserIds(userIds, 3, false);

		assertEquals(scrobblesUser1User2.size(), 3);
		assertTrue(scrobblesUser1User2.iterator().next().equals(scrobble6));
		assertTrue(scrobblesUser1User2.contains(scrobble4));
		assertTrue(scrobblesUser1User2.contains(scrobble3));
	}

	@Test
	public void testFindByUserIds() {
		Set<ObjectId> userIds = new HashSet<ObjectId>();
		userIds.add(user1.getId());
		userIds.add(user2.getId());
		List<Scrobble> scrobbles = scrobbleDao.findByUserIds(userIds, false);

		assertEquals(scrobbles.size(), 5);
		assertTrue(scrobbles.contains(scrobble1));
		assertTrue(!scrobbles.contains(scrobble5));
	}

	@Test
	public void testFindByUserIdWithDaysOffset() {
		Set<ObjectId> userIds = new HashSet<ObjectId>();
		userIds.add(user1.getId());
		userIds.add(user2.getId());

		List<Scrobble> scrobbles5DaysOld = scrobbleDao
				.findByUserIdsWithDaysOffset(userIds, 5, false);
		List<Scrobble> scrobbles3DaysOld = scrobbleDao
				.findByUserIdsWithDaysOffset(userIds, 3, false);
		List<Scrobble> scrobbles1DayOld = scrobbleDao
				.findByUserIdsWithDaysOffset(userIds, 1, false);

		assertEquals(5, scrobbles5DaysOld.size());
		assertEquals(4, scrobbles3DaysOld.size());
		assertEquals(3, scrobbles1DayOld.size());
	}

	@Test
	public void testFindByUserIdsWithDaysOffset() {
		List<Scrobble> scrobbles5DaysOld = scrobbleDao
				.findLastScrobblesByUserIdWithDaysOffset(user1.getId(), 5, 2,
						false);
		List<Scrobble> scrobbles1DayOld = scrobbleDao
				.findLastScrobblesByUserIdWithDaysOffset(user1.getId(), 1, 1,
						false);

		assertEquals(2, scrobbles5DaysOld.size());
		assertEquals(1, scrobbles1DayOld.size());
	}

	@Test
	public void testFindLastScrobblesByUserIdsWithDaysOffset() {
		List<Scrobble> scrobbles5DaysOld = scrobbleDao
				.findLastScrobblesByUserIdWithDaysOffset(user1.getId(), 5, 2,
						false);
		List<Scrobble> scrobbles1DayOld = scrobbleDao
				.findLastScrobblesByUserIdWithDaysOffset(user1.getId(), 1, 1,
						false);

		assertEquals(2, scrobbles5DaysOld.size());
		assertEquals(1, scrobbles1DayOld.size());
	}

}
