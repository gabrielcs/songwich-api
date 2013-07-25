package daos.api;

import static org.junit.Assert.*;
import models.App;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import usecases.api.util.DatabaseContext;

import com.google.code.morphia.Key;
import com.mongodb.WriteResult;

public class MusicServiceDAOMongoTest {
	
	private static final String CREATED_BY = "gabriel@dev.com";
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
		App service1 = new App("Spotify", CREATED_BY);
		App service2 = new App("Deezer", CREATED_BY);
		
		AppDAO<ObjectId> musicServiceDao = new AppDAOMongo();
		Key<App> keySave = musicServiceDao.save(service1);
		musicServiceDao.save(service2);
		
		// updates a document using save() 
		service1.setName("Rdio");
		Key<App> keySaveAgain = musicServiceDao.save(service1);
		// checks that it doesn't save twice
		assertEquals(keySave.getId(), keySaveAgain.getId());
		
		assertTrue(musicServiceDao.count() == 2);
		
		// assert that it updates the object
		App spotifyFromDatabase = musicServiceDao.findByName("Spotify");
		App rdioFromDatabase = musicServiceDao.findByName("Rdio");
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
		App service1 = new App("Spotify", CREATED_BY);
		App service2 = new App("Deezer", CREATED_BY);
		
		AppDAO<ObjectId> musicServiceDao = new AppDAOMongo();
		musicServiceDao.save(service1);
		musicServiceDao.save(service2);
		
		assertTrue(musicServiceDao.findById(service1.getId()).equals(service1));
		assertTrue(musicServiceDao.findById(service2.getId()).equals(service2));
	}

	@Test
	public void testFindByName() {
		App service1 = new App("Spotify", CREATED_BY);
		App service2 = new App("Deezer", CREATED_BY);
		
		AppDAO<ObjectId> musicServiceDao = new AppDAOMongo();
		musicServiceDao.save(service1);
		musicServiceDao.save(service2);
		
		assertTrue(musicServiceDao.findByName(service1.getName()).equals(service1));
		assertTrue(musicServiceDao.findByName(service2.getName()).equals(service2));
	}

}
