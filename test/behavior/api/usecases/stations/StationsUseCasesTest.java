package behavior.api.usecases.stations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
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
import database.api.util.WithRequestContextTest;

public class StationsUseCasesTest extends WithRequestContextTest {
	private User gabriel, daniel, john;
	private RadioStationDTO_V0_4 gabrielStationDTO, danielStationDTO,
			danielAndJohnStationDTO;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		createUsers();
		createScrobbles();
		createStationDTOs();
	}

	private void createUsers() {
		gabriel = new User("gabriel@example.com", "Gabriel");
		daniel = new User("daniel@example.com", "Daniel");
		john = new User("john@example.com", "John");

		UserDAO<ObjectId> userDAO = new UserDAOMongo();
		userDAO.save(gabriel, getContext().getAppDeveloper().getEmailAddress());
		userDAO.save(daniel, getContext().getAppDeveloper().getEmailAddress());
		userDAO.save(john, getContext().getAppDeveloper().getEmailAddress());
	}

	private void createScrobbles() {
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
	}

	private void createStationDTOs() {
		gabrielStationDTO = new RadioStationDTO_V0_4();
		gabrielStationDTO.setStationName("Gabriel FM");
		gabrielStationDTO.setScrobblerIds(Arrays.asList(gabriel.getId()
				.toString()));

		danielStationDTO = new RadioStationDTO_V0_4();
		danielStationDTO.setStationName("Daniel FM");
		danielStationDTO.setScrobblerIds(Arrays.asList(daniel.getId()
				.toString()));

		danielAndJohnStationDTO = new RadioStationDTO_V0_4();
		danielAndJohnStationDTO.setStationName("Daniel and John FM");
		danielAndJohnStationDTO.setGroupName("Daniel and John");
		danielAndJohnStationDTO.setScrobblerIds(Arrays.asList(daniel.getId()
				.toString(), john.getId().toString()));
	}

	@Test
	public void postStationsTest() throws SongwichAPIException {
		StationsUseCases stationsUseCases = new StationsUseCases(getContext());
		RadioStationDAO<ObjectId> radioStationDAO = new RadioStationDAOMongo();
		RadioStation station;

		// Gabriel FM should be active
		setRequestContextUser(gabriel);
		stationsUseCases.postStations(gabrielStationDTO);
		station = radioStationDAO.findById(new ObjectId(gabrielStationDTO
				.getStationId()));
		System.out.println(gabrielStationDTO);

		assertTrue(station.isActive());
		assertNull(gabrielStationDTO.getStationReadiness());

		assertNotNull(station.getNowPlaying());
		assertNotNull(station.getLookAhead());
		assertNotNull(gabrielStationDTO.getNowPlaying());
		assertNotNull(gabrielStationDTO.getLookAhead());

		// Daniel FM should be inactive
		setRequestContextUser(daniel);
		stationsUseCases.postStations(danielStationDTO);
		station = radioStationDAO.findById(new ObjectId(danielStationDTO
				.getStationId()));
		System.out.println(danielStationDTO);

		assertFalse(station.isActive());
		assertNotNull(danielStationDTO.getStationReadiness());

		assertNull(station.getNowPlaying());
		assertNull(station.getLookAhead());
		assertNull(danielStationDTO.getNowPlaying());
		assertNull(danielStationDTO.getLookAhead());

		// Daniel and John FM should be active and have "recent scrobblers"
		setRequestContextUser(john);
		stationsUseCases.postStations(danielAndJohnStationDTO);
		station = radioStationDAO.findById(new ObjectId(danielAndJohnStationDTO
				.getStationId()));
		System.out.println(danielAndJohnStationDTO);

		assertTrue(station.isActive());
		assertNull(danielAndJohnStationDTO.getStationReadiness());

		assertNotNull(station.getNowPlaying());
		assertNotNull(station.getLookAhead());
		assertNotNull(danielAndJohnStationDTO.getNowPlaying());
		assertNotNull(danielAndJohnStationDTO.getLookAhead());

		// it won't work if we don't setup the users' names
		assertNotNull(station.getNowPlaying().getSongScrobblers());
		assertNotNull(station.getLookAhead().getSongScrobblers());
		assertNotNull(danielAndJohnStationDTO.getNowPlaying()
				.getRecentScrobblers());
		assertNotNull(danielAndJohnStationDTO.getLookAhead()
				.getRecentScrobblers());
	}

	@Test
	public void getStationsTest() throws SongwichAPIException {
		StationsUseCases stationsUseCases = new StationsUseCases(getContext());

		// Gabriel FM should be active
		setRequestContextUser(gabriel);
		stationsUseCases.postStations(gabrielStationDTO);
		gabrielStationDTO = stationsUseCases.getStations(gabrielStationDTO
				.getStationId());
		System.out.println(gabrielStationDTO);

		assertNull(gabrielStationDTO.getStationReadiness());
		assertNotNull(gabrielStationDTO.getNowPlaying());
		assertNotNull(gabrielStationDTO.getLookAhead());

		// Daniel FM should be inactive
		setRequestContextUser(daniel);
		stationsUseCases.postStations(danielStationDTO);
		danielStationDTO = stationsUseCases.getStations(danielStationDTO
				.getStationId());
		System.out.println(danielStationDTO);

		assertNotNull(danielStationDTO.getStationReadiness());
		assertNull(danielStationDTO.getNowPlaying());
		assertNull(danielStationDTO.getLookAhead());

		// Daniel and John FM should be active and have "recent scrobblers"
		setRequestContextUser(john);
		stationsUseCases.postStations(danielAndJohnStationDTO);
		danielAndJohnStationDTO = stationsUseCases
				.getStations(danielAndJohnStationDTO.getStationId());
		System.out.println(danielAndJohnStationDTO);

		assertNull(danielAndJohnStationDTO.getStationReadiness());
		assertNotNull(danielAndJohnStationDTO.getNowPlaying());
		assertNotNull(danielAndJohnStationDTO.getLookAhead());

		// it won't work if we don't setup the users' names
		assertNotNull(danielAndJohnStationDTO.getNowPlaying()
				.getRecentScrobblers());
		assertNotNull(danielAndJohnStationDTO.getLookAhead()
				.getRecentScrobblers());
	}
	
	@Test
	public void getMultipleStationsTest() throws SongwichAPIException {
		StationsUseCases stationsUseCases = new StationsUseCases(getContext());

		setRequestContextUser(gabriel);
		stationsUseCases.postStations(gabrielStationDTO);
		setRequestContextUser(daniel);
		stationsUseCases.postStations(danielStationDTO);
		setRequestContextUser(john);
		stationsUseCases.postStations(danielAndJohnStationDTO);
		
		List<RadioStationDTO_V0_4> stationsDTO = stationsUseCases.getStations();
		System.out.println(stationsDTO);
		
		for (RadioStationDTO_V0_4 stationDTO : stationsDTO) {
			assertNotNull(stationDTO.getIsActive());
			assertNull(stationDTO.getStationReadiness());
			assertNull(stationDTO.getNowPlaying());
			assertNull(stationDTO.getLookAhead());
		}
	}

	private void generateScrobbles(
			Map<Integer, Integer> scrobblesArtistsGenerationMap, ObjectId userId) {
		ScrobbleDAO<ObjectId> scrobbleDAO = new ScrobbleDAOMongo();
		Song song;
		Scrobble scrobble;
		int registeredScrobbles = 0;
		int registeredArtists = 0;

		for (int scrobblesPerArtist : scrobblesArtistsGenerationMap.keySet()) {
			int nArtists = scrobblesArtistsGenerationMap
					.get(scrobblesPerArtist);
			for (int i = 0; i < nArtists; i++) {
				for (int j = 0; j < scrobblesPerArtist; j++) {
					song = new Song("Title "
							+ String.valueOf(registeredScrobbles + i
									* scrobblesPerArtist + j + 1), "Artist "
							+ String.valueOf(i + registeredArtists + 1));
					scrobble = new Scrobble(userId, song,
							System.currentTimeMillis(), true, null);
					scrobbleDAO.save(scrobble, getContext().getAppDeveloper()
							.getEmailAddress());
				}
			}
			registeredScrobbles = registeredScrobbles + nArtists
					* scrobblesPerArtist;
			registeredArtists = registeredArtists + nArtists;
		}
	}
}
