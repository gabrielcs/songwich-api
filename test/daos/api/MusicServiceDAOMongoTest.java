package daos.api;

import static org.junit.Assert.assertTrue;
import models.MusicService;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.MongoClient;

public class MusicServiceDAOMongoTest {
	
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
		MusicService service1 = new MusicService("Spotify");
		MusicService service2 = new MusicService("Deezer");
		
		MusicServiceDAO<ObjectId> musicServiceDao = new MusicServiceDAOMongo(ds);
		musicServiceDao.save(service1);
		musicServiceDao.save(service2);
		
		assertTrue(musicServiceDao.count() == 2);
		
		musicServiceDao.delete(service1);
		musicServiceDao.delete(service2);
		
		assertTrue(musicServiceDao.count() == 0);
	}
	
	@Test
	public void testFindById() {
		MusicService service1 = new MusicService("Spotify");
		MusicService service2 = new MusicService("Deezer");
		
		MusicServiceDAO<ObjectId> musicServiceDao = new MusicServiceDAOMongo(ds);
		musicServiceDao.save(service1);
		musicServiceDao.save(service2);
		
		assertTrue(musicServiceDao.findById(service1.getId()).equals(service1));
		assertTrue(musicServiceDao.findById(service2.getId()).equals(service2));
	}

	@Test
	public void testFindByName() {
		MusicService service1 = new MusicService("Spotify");
		MusicService service2 = new MusicService("Deezer");
		
		MusicServiceDAO<ObjectId> musicServiceDao = new MusicServiceDAOMongo(ds);
		musicServiceDao.save(service1);
		musicServiceDao.save(service2);
		
		assertTrue(musicServiceDao.findByName(service1.getName()).equals(service1));
		assertTrue(musicServiceDao.findByName(service2.getName()).equals(service2));
	}

}
