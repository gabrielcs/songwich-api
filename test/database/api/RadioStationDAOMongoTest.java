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
import models.api.stations.Track;

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
	private RadioStation nofxStation, fatMikeStation;
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

		nofxStation = new RadioStation("NOFX FM", nofx);
		linoleum = new Song("Linoleum", "NOFX");
		doWhatYouWant = new Song("Do What You Want", "Bad Religion");
		nofxStation.setNowPlaying(new Track(null, doWhatYouWant, null));
		nofxStation.setLookAhead(new Track(null, linoleum, null));

		fatMikeStation = new RadioStation("Fat Mike", fatMike);
		fatMikeStation.setNowPlaying(new Track(null, linoleum, null));
		fatMikeStation.setLookAhead(new Track(null, doWhatYouWant, null));

		RadioStationDAOMongo radioStationDAO = new RadioStationDAOMongo();
		radioStationDAO.cascadeSave(nofxStation, DEV_EMAIL);
		radioStationDAO.cascadeSave(fatMikeStation, DEV_EMAIL);
	}

	@Test
	public void testCountAndDelete() {
		assertTrue(radioStationDao.count() == 2);
		radioStationDao.delete(nofxStation);
		assertTrue(radioStationDao.count() == 1);
	}

	@Test
	public void testFindById() {
		RadioStation databaseStation = (RadioStation) radioStationDao
				.findById(nofxStation.getId());
		assertEquals(nofxStation, databaseStation);
		assertEquals(databaseStation, nofxStation);
	}

	@Test
	public void testFindByName() {
		List<RadioStation> radioStations = radioStationDao
				.findByName(nofxStation.getName());

		assertTrue(radioStations.size() == 1);
		RadioStation databaseStation = radioStations.iterator().next();

		assertEquals(databaseStation, nofxStation);
		assertEquals(nofxStation, databaseStation);
	}

	@Test
	public void testFindByScrobblerId() {
		List<RadioStation> mikeStations = radioStationDao
				.findByScrobblerId(fatMike.getId());
		assertTrue(mikeStations.size() == 2);
		assertTrue(mikeStations.contains(fatMikeStation));
		assertTrue(mikeStations.contains(nofxStation));
		
		List<RadioStation> hefeStations = radioStationDao
				.findByScrobblerId(elHefe.getId());
		assertTrue(hefeStations.size() == 1);
		assertTrue(hefeStations.contains(nofxStation));
		
	}
}
