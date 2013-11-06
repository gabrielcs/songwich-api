package behavior.api.usecases.stations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import models.api.scrobbles.Scrobble;
import models.api.scrobbles.Song;
import models.api.scrobbles.User;
import models.api.stations.RadioStation;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import util.api.SongwichAPIException;
import views.api.stations.RadioStationDTO_V0_4;
import database.api.scrobbles.ScrobbleDAO;
import database.api.scrobbles.ScrobbleDAOMongo;
import database.api.scrobbles.UserDAO;
import database.api.scrobbles.UserDAOMongo;
import database.api.stations.RadioStationDAO;
import database.api.stations.RadioStationDAOMongo;
import database.api.util.CleanDatabaseTest;

public class StationsUseCasesTest extends CleanDatabaseTest {
	User gabriel, daniel, john;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		initData();

	}

	private void initData() {
		gabriel = new User("gabriel@example.com");
		daniel = new User("daniel@example.com");
		john = new User("john@example.com");

		UserDAO<ObjectId> userDAO = new UserDAOMongo();
		userDAO.save(gabriel, DEV.getEmailAddress());
		userDAO.save(daniel, DEV.getEmailAddress());
		userDAO.save(john, DEV.getEmailAddress());
	}

	@Test
	public void postStationsAuthorizedTest() throws SongwichAPIException {
		// gabriel's scrobbles
		Map<Integer, Integer> scrobblesArtistsGenerationMap = new LinkedHashMap<Integer, Integer>(
				10);
		// for example, scrobbles: 1 song each for 20 artists
		scrobblesArtistsGenerationMap.put(1, 20);
		scrobblesArtistsGenerationMap.put(2, 10);
		scrobblesArtistsGenerationMap.put(3, 4);
		scrobblesArtistsGenerationMap.put(5, 2);
		scrobblesArtistsGenerationMap.put(12, 1);
		// total: 63 countable scrobbles (not more than 3 songs per artist)
		generateScrobbles(scrobblesArtistsGenerationMap, gabriel.getId());

		// daniel's scrobbles
		scrobblesArtistsGenerationMap = new LinkedHashMap<Integer, Integer>(10);
		scrobblesArtistsGenerationMap.put(5, 3);
		scrobblesArtistsGenerationMap.put(8, 4);
		scrobblesArtistsGenerationMap.put(13, 2);
		// total: 27 countable scrobbles (not more than 3 songs per artist)
		generateScrobbles(scrobblesArtistsGenerationMap, daniel.getId());

		// john's scrobbles
		scrobblesArtistsGenerationMap = new LinkedHashMap<Integer, Integer>(10);
		scrobblesArtistsGenerationMap.put(1, 9);
		scrobblesArtistsGenerationMap.put(2, 6);
		scrobblesArtistsGenerationMap.put(5, 3);
		scrobblesArtistsGenerationMap.put(7, 2);
		scrobblesArtistsGenerationMap.put(13, 3);
		// total: 36 countable scrobbles (not more than 3 songs per artist)
		// the 9 first ones are ignored in the group station
		generateScrobbles(scrobblesArtistsGenerationMap, john.getId());

		RadioStationDTO_V0_4 gabrielStationDTO = new RadioStationDTO_V0_4();
		gabrielStationDTO.setStationName("Gabriel FM");
		gabrielStationDTO.setScrobblerIds(Arrays.asList(gabriel.getId()
				.toString()));

		RadioStationDTO_V0_4 danielStationDTO = new RadioStationDTO_V0_4();
		danielStationDTO.setStationName("Daniel FM");
		danielStationDTO.setScrobblerIds(Arrays.asList(daniel.getId()
				.toString()));

		RadioStationDTO_V0_4 groupStationDTO = new RadioStationDTO_V0_4();
		groupStationDTO.setStationName("Daniel and John FM");
		groupStationDTO.setGroupName("Daniel and John");
		groupStationDTO.setScrobblerIds(Arrays.asList(
				daniel.getId().toString(), john.getId().toString()));
		
		StationsUseCases stationsUseCases = new StationsUseCases(
				REQUEST_CONTEXT);
		RadioStationDAO<ObjectId> radioStationDAO = new RadioStationDAOMongo();
		RadioStation station;

		// Gabriel FM should be active
		stationsUseCases.postStationsAuthorized(gabrielStationDTO);
		station = radioStationDAO.findById(new ObjectId(gabrielStationDTO
				.getStationId()));
		assertTrue(station.isActive());

		// Daniel FM should be inactive
		stationsUseCases.postStationsAuthorized(danielStationDTO);
		station = radioStationDAO.findById(new ObjectId(danielStationDTO
				.getStationId()));
		assertFalse(station.isActive());

		// Daniel and John FM should be active
		stationsUseCases.postStationsAuthorized(groupStationDTO);
		station = radioStationDAO.findById(new ObjectId(groupStationDTO
				.getStationId()));
		assertTrue(station.isActive());
	}

	private void generateScrobbles(
			Map<Integer, Integer> scrobblesArtistsGenerationMap, ObjectId userId) {
		ScrobbleDAO<ObjectId> scrobbleDAO = new ScrobbleDAOMongo();
		Song song;
		Scrobble scrobble;
		int registeredScrobbles = 0;
		int registeredArtists = 0;

		for (int scrobblesPerArtist : scrobblesArtistsGenerationMap.keySet()) {
			int nArtists = scrobblesArtistsGenerationMap.get(scrobblesPerArtist);
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
			registeredScrobbles = registeredScrobbles + nArtists
					* scrobblesPerArtist;
			registeredArtists = registeredArtists + nArtists;
		}
	}
}
