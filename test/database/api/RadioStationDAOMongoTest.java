package database.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;

import models.api.scrobbles.App;
import models.api.scrobbles.AppUser;
import models.api.scrobbles.AuthToken;
import models.api.scrobbles.Song;
import models.api.scrobbles.User;
import models.api.stations.Group;
import models.api.stations.GroupMember;
import models.api.stations.RadioStation;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import database.api.stations.RadioStationDAO;
import database.api.stations.RadioStationDAOMongo;
import database.api.util.CleanDatabaseTest;

public class RadioStationDAOMongoTest extends CleanDatabaseTest {

	private RadioStationDAO<ObjectId> radioStationDao;

	private User fatMike, elHefe;
	private GroupMember fatMikeFromNofx, elHefeFromNofx;
	private HashSet<GroupMember> nofxGroupMembers;
	private Group nofx;
	private RadioStation<Group> nofxRadioStation;
	private App spotify, rdio;
	private AppUser fatMikeOnSpotify, elHefeOnRdio;
	private Song linoleum, doWhatYouWant;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		radioStationDao = new RadioStationDAOMongo();
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

		nofxRadioStation = new RadioStation<Group>("NOFX FM", nofx);
		linoleum = new Song("Linoleum", "NOFX");
		doWhatYouWant = new Song("Do What You Want", "Bad Religion");
		nofxRadioStation.setNowPlaying(doWhatYouWant);
		nofxRadioStation.setLookAhead(linoleum);

		RadioStationDAOMongo radioStationDAO = new RadioStationDAOMongo();
		radioStationDAO.cascadeSave(nofxRadioStation, DEV_EMAIL);
	}

	@Test
	public void testCountAndDelete() {
		assertTrue(radioStationDao.count() == 1);
		radioStationDao.delete(nofxRadioStation);
		assertTrue(radioStationDao.count() == 0);
	}

	@Test
	public void testFindById() {
		@SuppressWarnings("unchecked")
		RadioStation<Group> databaseStation = (RadioStation<Group>) radioStationDao
				.findById(nofxRadioStation.getId());
		assertEquals(nofxRadioStation, databaseStation);
		assertEquals(databaseStation, nofxRadioStation);
		
		System.out.println(databaseStation);
	}

	@Test
	public void testFindByName() {
		@SuppressWarnings("rawtypes")
		List<RadioStation> radioStations = radioStationDao
				.findByName(nofxRadioStation.getName());

		assertTrue(radioStations.size() == 1);
		@SuppressWarnings("unchecked")
		RadioStation<Group> databaseStation = radioStations.iterator().next();

		assertEquals(databaseStation, nofxRadioStation);
		assertEquals(nofxRadioStation, databaseStation);
	}
}
