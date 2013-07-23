package daos.api;

import static org.junit.Assert.*;
import models.App;
import models.AppUser;
import models.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.MongoClient;

public class UserDAOMongoTest {
	
	private Datastore ds;
	private String dbName = "songwich-api-test";

	@Before
	public void setUp() throws Exception {
		ds = new Morphia().createDatastore(new MongoClient(), dbName);
		//Logger.info("Connected to database " + dbName);
		System.out.println("Connected to database " + dbName);
	}
	
	@After
	public void tearDown() throws Exception {
		ds.getDB().dropDatabase();
		//Logger.info("Dropped database " + dbName);
		System.out.println("Dropped database " + dbName);
	}
	
	@Test
	public void testSaveAndDelete() {
		User user1 = new User("gabriel@example.com", "Gabriel Example");
		User user2 = new User("daniel@example.com", "Daniel Example");
		
		UserDAOMongo userDao = new UserDAOMongo(ds);
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
		
		UserDAOMongo userDao = new UserDAOMongo(ds);
		userDao.cascadeSave(user1);
		userDao.cascadeSave(user2);
		
		assertTrue(userDao.findById(user1.getId()).equals(user1));
		assertTrue(userDao.findById(user2.getId()).equals(user2));
	}

	@Test
	public void testFindByEmailAddress() {
		User user1 = new User("gabriel@example.com", "Gabriel Example");
		User user2 = new User("daniel@example.com", "Daniel Example");
		
		UserDAOMongo userDao = new UserDAOMongo(ds);
		userDao.cascadeSave(user1);
		userDao.cascadeSave(user2);
		
		assertTrue(userDao.findByEmailAddress("gabriel@example.com").equals(user1));
		assertTrue(userDao.findByEmailAddress("daniel@example.com").equals(user2));
	}
	
	@Test
	public void testFindByEmailAddressWithMusicServiceUser() {
		App service1 = new App("Spotify");
		App service2 = new App("Rdio");
		
		User user1 = new User("gabriel@example.com", "Gabriel Example");
		AppUser service1User1 = new AppUser(service1, "gabriel@spam.com");
		user1.addAppUser(service1User1);
		
		User user2 = new User("daniel@example.com", "Daniel Example");
		AppUser service2User2 = new AppUser(service2, "daniel@spam.com");
		user2.addAppUser(service2User2);
		
		UserDAOMongo userDao = new UserDAOMongo(ds);
		userDao.cascadeSave(user1);
		userDao.cascadeSave(user2);
		
		assertTrue(userDao.findByEmailAddress("gabriel@spam.com").equals(user1));
		assertTrue(userDao.findByEmailAddress("daniel@spam.com").equals(user2));
	}

}
