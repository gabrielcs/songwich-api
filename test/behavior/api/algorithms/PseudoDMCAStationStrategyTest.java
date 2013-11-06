package behavior.api.algorithms;

import models.api.scrobbles.Scrobble;
import models.api.scrobbles.Song;
import models.api.scrobbles.User;
import models.api.stations.RadioStation;
import models.api.stations.StationHistoryEntry;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import util.api.SongwichAPIException;
import database.api.scrobbles.ScrobbleDAO;
import database.api.scrobbles.ScrobbleDAOMongo;
import database.api.scrobbles.UserDAO;
import database.api.scrobbles.UserDAOMongo;
import database.api.stations.RadioStationDAO;
import database.api.stations.RadioStationDAOMongo;
import database.api.stations.StationHistoryDAO;
import database.api.stations.StationHistoryDAOMongo;
import database.api.util.CleanDatabaseTest;

public class PseudoDMCAStationStrategyTest extends CleanDatabaseTest {
	User gabriel;
	private RadioStation gabrielFM;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		initData();
	}

	private void initData() {
		gabriel = new User("gabriel@example.com");
		UserDAO<ObjectId> userDAO = new UserDAOMongo();
		userDAO.save(gabriel, DEV.getEmailAddress());

		gabrielFM = new RadioStation("Gabriel FM", gabriel);
		RadioStationDAO<ObjectId> radioStationDAO = new RadioStationDAOMongo();
		radioStationDAO.save(gabrielFM, DEV.getEmailAddress());
	}

	@Test
	public void testOneRound1x10And2x6And3x4And5x2And8x1()
			throws SongwichAPIException {
		generateScrobbles20x1And8x2And4x3And3x6And1x12(gabriel.getId());
		runGetNextSong(60, 1);
	}

	@Test
	public void testTenRounds1x10And2x6And3x4And5x2And8x1()
			throws SongwichAPIException {
		generateScrobbles20x1And8x2And4x3And3x6And1x12(gabriel.getId());
		runGetNextSong(60, 10);
	}

	@Test
	public void testOneRound21x3() throws SongwichAPIException {
		generateScrobbles(gabriel.getId(), 21, 0, 3, 0);
		runGetNextSong(60, 1);
	}

	@Test
	public void testTenRounds21x3() throws SongwichAPIException {
		generateScrobbles(gabriel.getId(), 21, 0, 3, 0);
		runGetNextSong(60, 10);
	}

	private void generateScrobbles20x1And8x2And4x3And3x6And1x12(ObjectId userId) {
		int registeredScrobbles = 0;
		int registeredArtists = 0;

		generateScrobbles(userId, 20, registeredArtists, 1, registeredScrobbles);
		registeredScrobbles = registeredScrobbles + 20 * 1;
		registeredArtists = registeredArtists + 20;

		generateScrobbles(userId, 8, registeredArtists, 2, registeredScrobbles);
		registeredScrobbles = registeredScrobbles + 8 * 2;
		registeredArtists = registeredArtists + 8;

		generateScrobbles(userId, 4, registeredArtists, 3, registeredScrobbles);
		registeredScrobbles = registeredScrobbles + 4 * 3;
		registeredArtists = registeredArtists + 4;

		generateScrobbles(userId, 3, registeredArtists, 6, registeredScrobbles);
		registeredScrobbles = registeredScrobbles + 3 * 6;
		registeredArtists = registeredArtists + 3;

		generateScrobbles(userId, 1, registeredArtists, 12, registeredScrobbles);
		registeredScrobbles = registeredScrobbles + 1 * 12;
		registeredArtists = registeredArtists + 1;
	}

	private void generateScrobbles(ObjectId userId, int nArtists,
			int registeredArtists, int scrobblesPerArtist,
			int registeredScrobbles) {

		ScrobbleDAO<ObjectId> scrobbleDAO = new ScrobbleDAOMongo();
		Song song;
		Scrobble scrobble;
		for (int i = 0; i < nArtists; i++) {
			for (int j = 0; j < scrobblesPerArtist; j++) {

				song = new Song("Title "
						+ String.valueOf(registeredScrobbles + i
								* scrobblesPerArtist + j + 1), "Artist "
						+ String.valueOf(i + registeredArtists + 1));
				scrobble = new Scrobble(userId, song,
						System.currentTimeMillis(), true, null);
				scrobbleDAO.save(scrobble, DEV.getEmailAddress());
			}
		}
	}

	private void runGetNextSong(int numberOfScrobbles, float numberOfRounds)
			throws SongwichAPIException {
		Song song = null;
		StationHistoryDAO<ObjectId> stationHistoryDAO = new StationHistoryDAOMongo();
		StationHistoryEntry stationHistoryEntry;

		for (int i = 0; i < numberOfScrobbles * numberOfRounds; i++) {
			StationStrategy stationStrategy = new PseudoDMCAStationStrategy(
					gabrielFM);
			song = stationStrategy.getNextSong();
			stationHistoryEntry = new StationHistoryEntry(gabrielFM.getId(),
					song, System.currentTimeMillis());
			stationHistoryDAO.save(stationHistoryEntry, DEV.getEmailAddress());
		}
	}
}
