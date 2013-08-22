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
import models.api.stations.SongFeedback;
import models.api.stations.SongFeedback.FeedbackType;
import models.api.stations.StationHistoryEntry;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import database.api.scrobbles.UserDAOMongo;
import database.api.stations.RadioStationDAOMongo;
import database.api.stations.StationHistoryDAO;
import database.api.stations.StationHistoryDAOMongo;
import database.api.util.CleanDatabaseTest;

public class StationHistoryEntryDAOMongoTest extends CleanDatabaseTest {

	private StationHistoryDAO<ObjectId> stationHistoryDao;

	private User fatMike, elHefe;
	private GroupMember fatMikeFromNofx, elHefeFromNofx;
	private Set<GroupMember> nofxGroupMembers;
	private Group nofx;
	private RadioStation<Group> nofxRadioStation;
	private App spotify, rdio;
	private AppUser fatMikeOnSpotify, elHefeOnRdio;
	private Song linoleum, doWhatYouWant;
	private StationHistoryEntry linoleumEntry;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		stationHistoryDao = new StationHistoryDAOMongo();
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

		// saves the radio station
		RadioStationDAOMongo radioStationDao = new RadioStationDAOMongo();
		radioStationDao.cascadeSave(nofxRadioStation);

		linoleumEntry = new StationHistoryEntry(nofxRadioStation.getId(),
				linoleum, System.currentTimeMillis(), DEV_EMAIL);
		// saves the radio station history entry
		stationHistoryDao.save(linoleumEntry);

		// adds feedback
		User gabriel = new User("gabriel@example.com", "Gabriel Cypriano",
				DEV_EMAIL);
		User daniel = new User("daniel@example.com", "Daniel Caon",
				DEV_EMAIL);
		AppUser gabrielOnSpotify = new AppUser(spotify, "fatmike@nofx.com",
				AuthToken.createUserAuthToken(), DEV_EMAIL);
		AppUser danielOnRdio = new AppUser(rdio, "daniel@example.com",
				AuthToken.createUserAuthToken(), DEV_EMAIL);
		gabriel.addAppUser(gabrielOnSpotify, DEV_EMAIL);
		daniel.addAppUser(danielOnRdio, DEV_EMAIL);
		// saves the users so they have an id
		UserDAOMongo userDao = new UserDAOMongo();
		userDao.cascadeSave(gabriel);
		userDao.cascadeSave(daniel);

		SongFeedback linoleumFeedbackGabriel = new SongFeedback(
				FeedbackType.THUMBS_UP, gabriel.getId(), DEV_EMAIL);
		SongFeedback linoleumFeedbackDaniel = new SongFeedback(
				FeedbackType.THUMBS_DOWN, daniel.getId(), DEV_EMAIL);
		linoleumEntry.addSongFeedback(linoleumFeedbackGabriel, DEV_EMAIL);
		linoleumEntry.addSongFeedback(linoleumFeedbackDaniel, DEV_EMAIL);
		// updates the feedback entry
		linoleumEntry.setLastModifiedAt(System.currentTimeMillis());
		linoleumEntry.setLastModifiedBy(DEV_EMAIL);
		stationHistoryDao.save(linoleumEntry);
	}

	@Test
	public void testSaveAndDelete() {
		assertTrue(stationHistoryDao.count() == 1);
		stationHistoryDao.delete(linoleumEntry);
		assertTrue(stationHistoryDao.count() == 0);
		//System.out.println(linoleumEntry);
	}

	@Test
	public void testFindById() {
		assertTrue(stationHistoryDao.findById(linoleumEntry.getId()).equals(
				linoleumEntry));
	}

	@Test
	public void testFindByStationId() {
		assertTrue(stationHistoryDao.findByStationId(nofxRadioStation.getId())
				.size() == 1);
	}
}
