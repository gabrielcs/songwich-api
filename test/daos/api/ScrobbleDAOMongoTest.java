package daos.api;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.MusicService;
import models.Scrobble;
import models.User;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.MongoClient;

import daos.api.util.CascadeSaveDAO;

public class ScrobbleDAOMongoTest {

	private Datastore ds;
	private String dbName = "songwich-api-test";

	@Before
	public void setUp() throws Exception {
		ds = new Morphia().createDatastore(new MongoClient(), dbName);
		// Logger.info("Connected to database " + dbName);
		System.out.println("Connected to database " + dbName);
	}

	@After
	public void tearDown() throws Exception {
		ds.getDB().dropDatabase();
		// Logger.info("Dropped database " + dbName);
		System.out.println("Dropped database " + dbName);
	}

	@Test
	public void testSaveAndDelete() {
		User user1 = new User("gabriel@example.com", "Gabriel Example");
		User user2 = new User("daniel@example.com", "Daniel Example");
		CascadeSaveDAO<User, ObjectId> userDao = new UserDAOMongo(ds);
		userDao.cascadeSave(user1);
		userDao.cascadeSave(user2);

		MusicService musicService1 = new MusicService("Spotify");
		MusicService musicService2 = new MusicService("Deezer");
		CascadeSaveDAO<MusicService, ObjectId> musicServiceDao = new MusicServiceDAOMongo(
				ds);
		musicServiceDao.cascadeSave(musicService1);
		musicServiceDao.cascadeSave(musicService2);

		List<String> artists1 = new ArrayList<String>();
		artists1.add("Passion Pit");

		List<String> artists2 = new ArrayList<String>();
		artists2.add("Daft Punk");
		artists2.add("Pharrell Williams");

		Date date1 = new Date(System.currentTimeMillis());
		Date date2 = new Date(System.currentTimeMillis());

		Scrobble scrobble1 = new Scrobble(user1.getId(), "Take a Walk",
				artists1, date1, false, musicService1);
		Scrobble scrobble2 = new Scrobble(user2.getId(), "Get Lucky", artists2,
				date2, true, musicService2);

		// ScrobbleDAOMongo is not a CascadeSaveDAO
		// it requires saving its references beforehand
		ScrobbleDAO<ObjectId> scrobbleDao = new ScrobbleDAOMongo(ds);
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
		CascadeSaveDAO<User, ObjectId> userDao = new UserDAOMongo(ds);
		userDao.cascadeSave(user1);
		userDao.cascadeSave(user2);

		MusicService musicService1 = new MusicService("Spotify");
		MusicService musicService2 = new MusicService("Deezer");
		CascadeSaveDAO<MusicService, ObjectId> musicServiceDao = new MusicServiceDAOMongo(
				ds);
		musicServiceDao.cascadeSave(musicService1);
		musicServiceDao.cascadeSave(musicService2);

		List<String> artists1 = new ArrayList<String>();
		artists1.add("Passion Pit");

		List<String> artists2 = new ArrayList<String>();
		artists2.add("Daft Punk");
		artists2.add("Pharrell Williams");

		Date date1 = new Date(System.currentTimeMillis());
		Date date2 = new Date(System.currentTimeMillis());

		Scrobble scrobble1 = new Scrobble(user1.getId(), "Take a Walk",
				artists1, date1, false, musicService1);
		Scrobble scrobble2 = new Scrobble(user2.getId(), "Get Lucky", artists2,
				date2, true, musicService2);

		// ScrobbleDAOMongo is not a CascadeSaveDAO
		// it requires saving its references beforehand
		ScrobbleDAO<ObjectId> scrobbleDao = new ScrobbleDAOMongo(ds);
		scrobbleDao.save(scrobble1);
		scrobbleDao.save(scrobble2);

		assertTrue(scrobbleDao.findById(scrobble1.getId()).equals(scrobble1));
		assertTrue(scrobbleDao.findById(scrobble2.getId()).equals(scrobble2));
	}

	@Test
	public void testFindByUserId() {
		User user1 = new User("gabriel@example.com", "Gabriel Example");
		User user2 = new User("daniel@example.com", "Daniel Example");
		CascadeSaveDAO<User, ObjectId> userDao = new UserDAOMongo(ds);
		userDao.cascadeSave(user1);
		userDao.cascadeSave(user2);

		MusicService musicService1 = new MusicService("Spotify");
		MusicService musicService2 = new MusicService("Deezer");
		CascadeSaveDAO<MusicService, ObjectId> musicServiceDao = new MusicServiceDAOMongo(
				ds);
		musicServiceDao.cascadeSave(musicService1);
		musicServiceDao.cascadeSave(musicService2);

		List<String> artists2 = new ArrayList<String>();
		artists2.add("Daft Punk");
		artists2.add("Pharrell Williams");

		Date date1 = new Date(System.currentTimeMillis());
		Date date2 = new Date(System.currentTimeMillis());

		Scrobble scrobble1 = new Scrobble(user1.getId(), "Take a Walk",
				"Passion Pit", date1, false, musicService1);
		Scrobble scrobble2 = new Scrobble(user2.getId(), "Get Lucky", artists2,
				date2, true, musicService2);

		// ScrobbleDAOMongo is not a CascadeSaveDAO
		// it requires saving its references beforehand
		ScrobbleDAO<ObjectId> scrobbleDao = new ScrobbleDAOMongo(ds);
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

}