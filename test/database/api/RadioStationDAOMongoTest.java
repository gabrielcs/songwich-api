package database.api;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import models.api.App;
import models.api.AppUser;
import models.api.AuthToken;
import models.api.Group;
import models.api.GroupMember;
import models.api.RadioStation;
import models.api.Song;
import models.api.User;

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
		initData();
		radioStationDao = new RadioStationDAOMongo();
	}

	private void initData() {
		fatMike = new User("fatmike@nofx.com", "Fat Mike", CREATED_BY);
		fatMikeFromNofx = new GroupMember(fatMike, System.currentTimeMillis(),
				CREATED_BY);
		elHefe = new User("elhefe@nofx.com", "El Hefe", CREATED_BY);
		elHefeFromNofx = new GroupMember(elHefe, System.currentTimeMillis(),
				CREATED_BY);
		nofxGroupMembers = new HashSet<GroupMember>();
		nofxGroupMembers.add(fatMikeFromNofx);
		nofxGroupMembers.add(elHefeFromNofx);
		nofx = new Group(nofxGroupMembers, CREATED_BY);

		spotify = new App("Spotify", CREATED_BY);
		rdio = new App("Rdio", CREATED_BY);
		fatMikeOnSpotify = new AppUser(spotify, "fatmike@nofx.com",
				AuthToken.createUserAuthToken(), CREATED_BY);
		elHefeOnRdio = new AppUser(rdio, "elhefe@nofx.com",
				AuthToken.createUserAuthToken(), CREATED_BY);

		fatMike.addAppUser(fatMikeOnSpotify);
		elHefe.addAppUser(elHefeOnRdio);
		
		AppDAO<ObjectId> appDao = new AppDAOMongo();
		appDao.save(spotify);
		appDao.save(rdio);
		UserDAO<ObjectId> userDao = new UserDAOMongo();
		userDao.save(fatMike);
		userDao.save(elHefe);
		
		nofxRadioStation = new RadioStation<Group>("NOFX", nofx, CREATED_BY);
		linoleum = new Song("Linoleum", "NOFX");
		doWhatYouWant = new Song("Do What You Want", "Bad Religion");
		nofxRadioStation.setNowPlaying(doWhatYouWant);
		nofxRadioStation.setLookAhead(linoleum);
		
		RadioStationDAO<ObjectId> radioStationDAO = new RadioStationDAOMongo();
		radioStationDAO.save(nofxRadioStation);
		
		System.out.println(nofxRadioStation);
	}

	@Test
	public void testCountAndDelete() {
		assertTrue(radioStationDao.count() == 1);
		radioStationDao.delete(nofxRadioStation);
		assertTrue(radioStationDao.count() == 0);
	}

	@Test
	public void testFindById() {
		assertTrue(radioStationDao.findById(nofxRadioStation.getId()).equals(
				nofxRadioStation));
	}
}
