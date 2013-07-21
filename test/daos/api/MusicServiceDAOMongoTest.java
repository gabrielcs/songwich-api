package daos.api;

import static org.junit.Assert.*;
import models.MusicService;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.google.code.morphia.Morphia;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

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
		Key<MusicService> keySave = musicServiceDao.save(service1);
		musicServiceDao.save(service2);
		
		// updates a document using save() 
		service1.setName("Rdio");
		Key<MusicService> keySaveAgain = musicServiceDao.save(service1);
		// checks that it doesn't save twice
		assertEquals(keySave.getId(), keySaveAgain.getId());
		
		assertTrue(musicServiceDao.count() == 2);
		
		// assert that it updates the object
		MusicService spotifyFromDatabase = musicServiceDao.findByName("Spotify");
		MusicService rdioFromDatabase = musicServiceDao.findByName("Rdio");
		assertNull(spotifyFromDatabase);
		assertNotNull(rdioFromDatabase);
		
		// assert deletion is working properly
		musicServiceDao.delete(service1);
		musicServiceDao.delete(service2);
		// try to delete a document twice
		WriteResult writeResultDeleteTwice = musicServiceDao.delete(service2);
		assertNull(writeResultDeleteTwice.getError());
		
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
