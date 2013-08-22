package database.api;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

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

import database.api.util.CleanDatabaseTest;

public class RadioStationDAOMongoTest extends CleanDatabaseTest {

	private RadioStationDAO<ObjectId> radioStationDao;
	
	private User fatMike, elHefe;
	private GroupMember fatMikeFromNofx, elHefeFromNofx;
	private Set<GroupMember> nofxGroupMembers;
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
		fatMike = new User("fatmike@nofx.com", "Fat Mike", DEV_EMAIL);
		fatMikeFromNofx = new GroupMember(fatMike, System.currentTimeMillis(),
				DEV_EMAIL);
		elHefe = new User("elhefe@nofx.com", "El Hefe", DEV_EMAIL);
		elHefeFromNofx = new GroupMember(elHefe, System.currentTimeMillis(),
				DEV_EMAIL);
		nofxGroupMembers = new HashSet<GroupMember>();
		nofxGroupMembers.add(fatMikeFromNofx);
		nofxGroupMembers.add(elHefeFromNofx);
		nofx = new Group(nofxGroupMembers, DEV_EMAIL);

		spotify = new App("Spotify", DEV_EMAIL);
		rdio = new App("Rdio", DEV_EMAIL);
		fatMikeOnSpotify = new AppUser(spotify, "fatmike@nofx.com",
				AuthToken.createUserAuthToken(), DEV_EMAIL);
		elHefeOnRdio = new AppUser(rdio, "elhefe@nofx.com",
				AuthToken.createUserAuthToken(), DEV_EMAIL);

		fatMike.addAppUser(fatMikeOnSpotify, DEV_EMAIL);
		elHefe.addAppUser(elHefeOnRdio, DEV_EMAIL);
		
		nofxRadioStation = new RadioStation<Group>("NOFX", nofx, DEV_EMAIL);
		linoleum = new Song("Linoleum", "NOFX");
		doWhatYouWant = new Song("Do What You Want", "Bad Religion");
		nofxRadioStation.setNowPlaying(doWhatYouWant, DEV_EMAIL);
		nofxRadioStation.setLookAhead(linoleum, DEV_EMAIL);
		
		RadioStationDAOMongo radioStationDAO = new RadioStationDAOMongo();
		radioStationDAO.cascadeSave(nofxRadioStation);
	}

	@Test
	public void testCountAndDelete() {
		assertTrue(radioStationDao.count() == 1);
		radioStationDao.delete(nofxRadioStation);
		assertTrue(radioStationDao.count() == 0);
		//System.out.println(nofxRadioStation);
	}

	@Test
	public void testFindById() {
		assertTrue(radioStationDao.findById(nofxRadioStation.getId()).equals(
				nofxRadioStation));
	}
}
