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
import models.api.SongFeedback;
import models.api.SongFeedback.FeedbackType;
import models.api.StationHistoryEntry;
import models.api.User;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

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

		nofxRadioStation = new RadioStation<Group>("NOFX", nofx, CREATED_BY);
		linoleum = new Song("Linoleum", "NOFX");
		doWhatYouWant = new Song("Do What You Want", "Bad Religion");
		nofxRadioStation.setNowPlaying(doWhatYouWant);
		nofxRadioStation.setLookAhead(linoleum);

		// saves the radio station
		RadioStationDAOMongo radioStationDao = new RadioStationDAOMongo();
		radioStationDao.cascadeSave(nofxRadioStation);

		linoleumEntry = new StationHistoryEntry(nofxRadioStation.getId(),
				linoleum, System.currentTimeMillis(), CREATED_BY);
		// saves the radio station history entry
		stationHistoryDao.save(linoleumEntry);

		// adds feedback
		User gabriel = new User("gabriel@example.com", "Gabriel Cypriano",
				CREATED_BY);
		User daniel = new User("daniel@example.com", "Daniel Caon",
				CREATED_BY);
		AppUser gabrielOnSpotify = new AppUser(spotify, "fatmike@nofx.com",
				AuthToken.createUserAuthToken(), CREATED_BY);
		AppUser danielOnRdio = new AppUser(rdio, "daniel@example.com",
				AuthToken.createUserAuthToken(), CREATED_BY);
		gabriel.addAppUser(gabrielOnSpotify);
		daniel.addAppUser(danielOnRdio);
		// saves the users so they have an id
		UserDAOMongo userDao = new UserDAOMongo();
		userDao.cascadeSave(gabriel);
		userDao.cascadeSave(daniel);

		SongFeedback linoleumFeedbackGabriel = new SongFeedback(
				FeedbackType.THUMBS_UP, gabriel.getId(), CREATED_BY);
		SongFeedback linoleumFeedbackDaniel = new SongFeedback(
				FeedbackType.THUMBS_DOWN, daniel.getId(), CREATED_BY);
		linoleumEntry.addSongFeedback(linoleumFeedbackGabriel);
		linoleumEntry.addSongFeedback(linoleumFeedbackDaniel);
		// updates the feedback entry
		linoleumEntry.setLastModifiedAt(System.currentTimeMillis());
		linoleumEntry.setLastModifiedBy(CREATED_BY);
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
