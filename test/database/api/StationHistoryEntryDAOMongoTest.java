package database.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;
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
import models.api.stations.SongFeedback;
import models.api.stations.SongFeedback.FeedbackType;
import models.api.stations.StationHistoryEntry;
import models.api.stations.Track;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import util.api.WithRequestContext;
import database.api.scrobbles.UserDAOMongo;
import database.api.stations.RadioStationDAOMongo;
import database.api.stations.StationHistoryDAO;
import database.api.stations.StationHistoryDAOMongo;

public class StationHistoryEntryDAOMongoTest extends WithRequestContext {

	private StationHistoryDAO<ObjectId> stationHistoryDao;

	private User fatMike, elHefe, gabriel, daniel;
	private GroupMember fatMikeFromNofx, elHefeFromNofx;
	private HashSet<GroupMember> nofxGroupMembers;
	private Group nofx;
	private RadioStation nofxStation, fatMikeStation;
	private App spotify, rdio;
	private AppUser fatMikeOnSpotify, elHefeOnRdio;
	private Song linoleum, doWhatYouWant, dontCallMeWhite;
	private StationHistoryEntry linoleumEntry, dontCallMeWhiteFatMikeEntry,
			dontCallMeWhiteNofx4HoursOldEntry, doWhatYouWantEntry,
			doWhatYouWantEntry4HoursOldEntry;

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

		linoleum = new Song("Linoleum", "NOFX");
		doWhatYouWant = new Song("Do What You Want", "Bad Religion");
		dontCallMeWhite = new Song("Don't Call Me White", "NOFX");
		nofxStation = new RadioStation("NOFX FM", nofx);

		fatMikeStation = new RadioStation("Fat Mike FM", fatMike);

		// saves the radio station
		RadioStationDAOMongo radioStationDao = new RadioStationDAOMongo();
		radioStationDao.cascadeSave(nofxStation, getContext().getAppDeveloper().getEmailAddress());
		radioStationDao.cascadeSave(fatMikeStation, getContext().getAppDeveloper().getEmailAddress());

		linoleumEntry = new StationHistoryEntry(nofxStation.getId(), linoleum,
				System.currentTimeMillis());
		// saves the radio station history entry
		stationHistoryDao.save(linoleumEntry, getContext().getAppDeveloper().getEmailAddress());

		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.HOUR, -4);
		dontCallMeWhiteNofx4HoursOldEntry = new StationHistoryEntry(
				nofxStation.getId(), dontCallMeWhite,
				calendar.getTimeInMillis());
		// saves the radio station history entry
		stationHistoryDao.save(dontCallMeWhiteNofx4HoursOldEntry, getContext().getAppDeveloper().getEmailAddress());

		dontCallMeWhiteFatMikeEntry = new StationHistoryEntry(
				fatMikeStation.getId(), dontCallMeWhite,
				System.currentTimeMillis());
		// saves the radio station history entry
		stationHistoryDao.save(dontCallMeWhiteFatMikeEntry, getContext().getAppDeveloper().getEmailAddress());

		doWhatYouWantEntry = new StationHistoryEntry(nofxStation.getId(),
				doWhatYouWant, System.currentTimeMillis());
		// saves the radio station history entry
		stationHistoryDao.save(doWhatYouWantEntry, getContext().getAppDeveloper().getEmailAddress());

		doWhatYouWantEntry4HoursOldEntry = new StationHistoryEntry(
				nofxStation.getId(), doWhatYouWant, calendar.getTimeInMillis());
		// saves the radio station history entry
		stationHistoryDao.save(doWhatYouWantEntry4HoursOldEntry, getContext().getAppDeveloper().getEmailAddress());

		// sets nofxStation's nowPlaying and lookAhead
		nofxStation.setNowPlaying(new Track(doWhatYouWantEntry, null));
		nofxStation.setLookAhead(new Track(linoleumEntry, null));
		radioStationDao.save(nofxStation, getContext().getAppDeveloper().getEmailAddress());

		// adds feedback
		gabriel = new User("gabriel@example.com", "Gabriel Cypriano");
		daniel = new User("daniel@example.com", "Daniel Caon");
		AppUser gabrielOnSpotify = new AppUser(spotify, "fatmike@nofx.com",
				AuthToken.createUserAuthToken());
		AppUser danielOnRdio = new AppUser(rdio, "daniel@example.com",
				AuthToken.createUserAuthToken());
		gabriel.addAppUser(gabrielOnSpotify);
		daniel.addAppUser(danielOnRdio);
		// saves the users so they have an id
		UserDAOMongo userDao = new UserDAOMongo();
		userDao.cascadeSave(gabriel, getContext().getAppDeveloper().getEmailAddress());
		userDao.cascadeSave(daniel, getContext().getAppDeveloper().getEmailAddress());

		SongFeedback linoleumFeedbackGabriel = new SongFeedback(
				FeedbackType.THUMBS_UP, gabriel.getId());
		SongFeedback linoleumFeedback2Gabriel = new SongFeedback(
				FeedbackType.STAR, gabriel.getId());
		SongFeedback linoleumFeedbackDaniel = new SongFeedback(
				FeedbackType.THUMBS_DOWN, daniel.getId());
		linoleumEntry.addSongFeedback(linoleumFeedbackGabriel);
		linoleumEntry.addSongFeedback(linoleumFeedback2Gabriel);
		linoleumEntry.addSongFeedback(linoleumFeedbackDaniel);
		stationHistoryDao.save(linoleumEntry, getContext().getAppDeveloper().getEmailAddress());

		SongFeedback dontCallMeWhiteFeedbackGabriel = new SongFeedback(
				FeedbackType.THUMBS_UP, gabriel.getId());
		SongFeedback dontCallMeWhiteFeedbackDaniel = new SongFeedback(
				FeedbackType.STAR, daniel.getId());
		dontCallMeWhiteNofx4HoursOldEntry
				.addSongFeedback(dontCallMeWhiteFeedbackGabriel);
		dontCallMeWhiteNofx4HoursOldEntry
				.addSongFeedback(dontCallMeWhiteFeedbackDaniel);
		stationHistoryDao.save(dontCallMeWhiteNofx4HoursOldEntry, getContext().getAppDeveloper().getEmailAddress());
	}

	@Test
	public void testSaveAndDelete() {
		assertTrue(stationHistoryDao.count() == 5);
		stationHistoryDao.delete(linoleumEntry);
		assertTrue(stationHistoryDao.count() == 4);
	}

	@Test
	public void testFindById() {
		assertTrue(stationHistoryDao.findById(linoleumEntry.getId()).equals(
				linoleumEntry));
	}

	@Test
	public void testCountByStationId() {
		assertEquals(4, stationHistoryDao.countByStationId(nofxStation.getId()));
	}

	@Test
	public void testFindByStationId() {
		List<StationHistoryEntry> entries = stationHistoryDao
				.findByStationId(nofxStation.getId());
		assertEquals(4, entries.size());
		assertTrue(entries.contains(linoleumEntry));
		assertTrue(entries.contains(dontCallMeWhiteNofx4HoursOldEntry));
		assertTrue(entries.contains(doWhatYouWantEntry));
		assertTrue(entries.contains(doWhatYouWantEntry4HoursOldEntry));
	}

	@Test
	public void testCountByStationIdAndArtist() {
		assertEquals(2, stationHistoryDao.countByStationIdAndArtist(
				nofxStation.getId(), "NOFX"));
	}

	@Test
	public void testCountByStationIdAndArtistWithHourOffset() {
		assertEquals(1,
				stationHistoryDao.countByStationIdAndArtistWithHourOffset(
						nofxStation.getId(), "NOFX", 3));
		assertEquals(2,
				stationHistoryDao.countByStationIdAndArtistWithHourOffset(
						nofxStation.getId(), "NOFX", 5));
	}

	@Test
	public void testCountByStationIdAndSongWithHourOffset() {
		assertEquals(1,
				stationHistoryDao.countByStationIdAndSongWithHourOffset(
						nofxStation.getId(), doWhatYouWant, 3));
		assertEquals(2,
				stationHistoryDao.countByStationIdAndSongWithHourOffset(
						nofxStation.getId(), doWhatYouWant, 5));
	}

	@Test
	public void testFindByStationIdAndArtist() {
		List<StationHistoryEntry> entries = stationHistoryDao
				.findByStationIdAndArtist(nofxStation.getId(), "NOFX");
		assertEquals(2, entries.size());
		assertTrue(entries.contains(linoleumEntry));
		assertTrue(entries.contains(dontCallMeWhiteNofx4HoursOldEntry));
	}

	@Test
	public void testFindByStationIdAndArtistWithHourOffset() {
		List<StationHistoryEntry> entries = stationHistoryDao
				.findByStationIdAndArtistWithHourOffset(nofxStation.getId(),
						"NOFX", 3);
		assertEquals(1, entries.size());
		assertTrue(entries.contains(linoleumEntry));

		entries = stationHistoryDao.findByStationIdAndArtistWithHourOffset(
				nofxStation.getId(), "NOFX", 5);
		assertEquals(2, entries.size());
		assertTrue(entries.contains(linoleumEntry));
		assertTrue(entries.contains(dontCallMeWhiteNofx4HoursOldEntry));
	}

	@Test
	public void testFindByStationIdWithHourOffset() {
		List<StationHistoryEntry> entries = stationHistoryDao
				.findByStationIdWithHourOffset(nofxStation.getId(), 3);
		assertEquals(2, entries.size());
		assertTrue(entries.contains(linoleumEntry));
		assertTrue(entries.contains(doWhatYouWantEntry));

		entries = stationHistoryDao.findByStationIdWithHourOffset(
				nofxStation.getId(), 5);
		assertEquals(4, entries.size());
		assertTrue(entries.contains(linoleumEntry));
		assertTrue(entries.contains(doWhatYouWantEntry));
		assertTrue(entries.contains(dontCallMeWhiteNofx4HoursOldEntry));
		assertTrue(entries.contains(doWhatYouWantEntry4HoursOldEntry));
	}

	@Test
	public void testFindLastEntriesByStationId() {
		List<StationHistoryEntry> entries = stationHistoryDao
				.findLastEntriesByStationId(nofxStation.getId(), 2);
		assertEquals(2, entries.size());
		assertTrue(entries.contains(doWhatYouWantEntry));
		assertTrue(entries.contains(linoleumEntry));

		entries = stationHistoryDao.findLastEntriesByStationId(
				nofxStation.getId(), 1);
		assertEquals(1, entries.size());
		assertTrue(entries.contains(doWhatYouWantEntry));
	}

	@Test
	public void testFindStarredByUserId() {
		List<StationHistoryEntry> entriesGabriel = stationHistoryDao
				.findStarredByUserId(gabriel.getId());
		assertEquals(1, entriesGabriel.size());
		assertTrue(entriesGabriel.contains(linoleumEntry));

		List<StationHistoryEntry> entriesDaniel = stationHistoryDao
				.findStarredByUserId(daniel.getId());
		assertEquals(1, entriesDaniel.size());
		assertTrue(entriesDaniel.contains(dontCallMeWhiteNofx4HoursOldEntry));
	}
}
