package database.api;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;

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
	private HashSet<GroupMember> nofxGroupMembers;
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

		// saves the radio station
		RadioStationDAOMongo radioStationDao = new RadioStationDAOMongo();
		radioStationDao.cascadeSave(nofxRadioStation, DEV_EMAIL);

		linoleumEntry = new StationHistoryEntry(nofxRadioStation.getId(),
				linoleum, System.currentTimeMillis());
		// saves the radio station history entry
		stationHistoryDao.save(linoleumEntry, DEV_EMAIL);

		// adds feedback
		User gabriel = new User("gabriel@example.com", "Gabriel Cypriano");
		User daniel = new User("daniel@example.com", "Daniel Caon");
		AppUser gabrielOnSpotify = new AppUser(spotify, "fatmike@nofx.com",
				AuthToken.createUserAuthToken());
		AppUser danielOnRdio = new AppUser(rdio, "daniel@example.com",
				AuthToken.createUserAuthToken());
		gabriel.addAppUser(gabrielOnSpotify);
		daniel.addAppUser(danielOnRdio);
		// saves the users so they have an id
		UserDAOMongo userDao = new UserDAOMongo();
		userDao.cascadeSave(gabriel, DEV_EMAIL);
		userDao.cascadeSave(daniel, DEV_EMAIL);

		SongFeedback linoleumFeedbackGabriel = new SongFeedback(
				FeedbackType.THUMBS_UP, gabriel.getId());
		SongFeedback linoleumFeedbackDaniel = new SongFeedback(
				FeedbackType.THUMBS_DOWN, daniel.getId());
		linoleumEntry.addSongFeedback(linoleumFeedbackGabriel);
		linoleumEntry.addSongFeedback(linoleumFeedbackDaniel);
		stationHistoryDao.save(linoleumEntry, DEV_EMAIL);
	}

	@Test
	public void testSaveAndDelete() {
		assertTrue(stationHistoryDao.count() == 1);
		stationHistoryDao.delete(linoleumEntry);
		assertTrue(stationHistoryDao.count() == 0);
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
