package database.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import models.api.MongoModel;
import models.api.scrobbles.App;
import models.api.scrobbles.AppDeveloper;
import models.api.scrobbles.AppUser;
import models.api.scrobbles.AuthToken;
import models.api.scrobbles.Song;
import models.api.scrobbles.User;
import models.api.stations.Group;
import models.api.stations.GroupMember;
import models.api.stations.RadioStation;
import models.api.stations.Track;

import org.junit.Before;
import org.junit.Test;

import database.api.scrobbles.AppDAOMongo;
import database.api.stations.RadioStationDAOMongo;
import database.api.util.CleanDatabaseTest;

public class ModelAndDAOTest extends CleanDatabaseTest {
	private RadioStationDAOMongo radioStationDao;
	private AppDAOMongo appDao; 

	private User fatMike, elHefe;
	private GroupMember fatMikeFromNofx, elHefeFromNofx;
	private Set<GroupMember> nofxGroupMembers;
	private Group nofx;
	private RadioStation nofxRadioStation;
	private App spotify, rdio;
	private AppUser fatMikeOnSpotify, elHefeOnRdio;
	private Song linoleum, doWhatYouWant;
	
	private final static String UPDATE_DEV_EMAIL = "update@songwich.com";

	@Before
	public void setUp() throws Exception {
		super.setUp();
		radioStationDao = new RadioStationDAOMongo();
		appDao = new AppDAOMongo();
		initData();
	}
	
	private void initData() {
		fatMike = new User("fatmike@nofx.com", "Fat Mike");
		fatMikeFromNofx = new GroupMember(fatMike, System.currentTimeMillis());
		elHefe = new User("elhefe@nofx.com", "El Hefe");
		elHefeFromNofx = new GroupMember(elHefe, System.currentTimeMillis());
		nofxGroupMembers = new HashSet<GroupMember>();
		nofxGroupMembers.add(fatMikeFromNofx);
		nofxGroupMembers.add(elHefeFromNofx);
		nofx = new Group("NOFX", nofxGroupMembers);

		spotify = new App("Spotify");
		rdio = new App("Rdio");
		fatMikeOnSpotify = new AppUser(spotify, "fatmike@nofx.com",
				AuthToken.createUserAuthToken());
		elHefeOnRdio = new AppUser(rdio, "elhefe@nofx.com",
				AuthToken.createUserAuthToken());

		fatMike.addAppUser(fatMikeOnSpotify);
		elHefe.addAppUser(elHefeOnRdio);

		nofxRadioStation = new RadioStation("NOFX FM", nofx);
		linoleum = new Song("Linoleum", "NOFX");
		doWhatYouWant = new Song("Do What You Want", "Bad Religion");
		nofxRadioStation.setNowPlaying(new Track(null, doWhatYouWant, null));
		nofxRadioStation.setLookAhead(new Track(null, linoleum, null));

		radioStationDao.cascadeSave(nofxRadioStation, DEV_EMAIL);
	}
	
	@Test
	public void testModelSave() {
		spotify = appDao.findById(spotify.getId());
		assertEquals(DEV_EMAIL, spotify.getCreatedBy());
	}

	@Test
	public void testModelUpdate() {
		// updates a document using save()
		AppDeveloper appDeveloper = new AppDeveloper(UPDATE_DEV_EMAIL,
				"Updater", AuthToken.createDevAuthToken());
		spotify.addAppDeveloper(appDeveloper);
		appDao.save(spotify, UPDATE_DEV_EMAIL);

		spotify = appDao.findById(spotify.getId());
		assertEquals(DEV_EMAIL, spotify.getCreatedBy());
		assertEquals(UPDATE_DEV_EMAIL, spotify.getLastModifiedBy());
		assertTrue(spotify.getCreatedAt() < spotify.getLastModifiedAt());
	}
	
	@Test
	public void testEmbeddedModelsCreate() throws IllegalArgumentException, IllegalAccessException {
		RadioStation databaseStation = radioStationDao.findById(nofxRadioStation.getId());
		testEmbeddedModelsCreate(databaseStation.getEmbeddedModels());
	}
	
	private void testEmbeddedModelsCreate(Set<MongoModel> models) throws IllegalArgumentException, IllegalAccessException {
		for (MongoModel model : models) {
			assertTrue(model.getCreatedBy().equals(DEV_EMAIL));
			assertTrue(model.getCreatedAt() < System.currentTimeMillis());
			testEmbeddedModelsCreate(model.getEmbeddedModels());
		}
	}
}
