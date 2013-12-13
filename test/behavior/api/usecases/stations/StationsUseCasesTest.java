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
import util.api.WithProductionDependencyInjection;
import views.api.stations.RadioStationDTO_V0_4;
import views.api.stations.RadioStationUpdateDTO_V0_4;
import behavior.api.algorithms.StationStrategy;

public class StationsUseCasesTest extends WithProductionDependencyInjection {
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

		getUserDAO().save(gabriel,
				getContext().getAppDeveloper().getEmailAddress());
		getUserDAO().save(daniel,
				getContext().getAppDeveloper().getEmailAddress());
		getUserDAO().save(john,
				getContext().getAppDeveloper().getEmailAddress());
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
		RadioStation station;

		// Gabriel FM should be active
		setRequestContextUser(gabriel);
		stationsUseCases.postStations(gabrielStationDTO, getInjector()
				.getInstance(StationStrategy.class));
		station = getRadioStationDAO().findById(
				new ObjectId(gabrielStationDTO.getStationId()));
		System.out.println(gabrielStationDTO);

		assertTrue(station.isActive());
		assertNull(gabrielStationDTO.getStationReadiness());

		assertNotNull(station.getNowPlaying());
		assertNotNull(station.getLookAhead());
		assertNotNull(gabrielStationDTO.getNowPlaying());
		assertNotNull(gabrielStationDTO.getLookAhead());

		// Daniel FM should be inactive
		setRequestContextUser(daniel);
		stationsUseCases.postStations(danielStationDTO, getInjector()
				.getInstance(StationStrategy.class));
		station = getRadioStationDAO().findById(
				new ObjectId(danielStationDTO.getStationId()));
		System.out.println(danielStationDTO);

		assertFalse(station.isActive());
		assertNotNull(danielStationDTO.getStationReadiness());

		assertNull(station.getNowPlaying());
		assertNull(station.getLookAhead());
		assertNull(danielStationDTO.getNowPlaying());
		assertNull(danielStationDTO.getLookAhead());

		// Daniel and John FM should be active and have "recent scrobblers"
		setRequestContextUser(john);
		stationsUseCases.postStations(danielAndJohnStationDTO, getInjector()
				.getInstance(StationStrategy.class));
		station = getRadioStationDAO().findById(
				new ObjectId(danielAndJohnStationDTO.getStationId()));
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
		stationsUseCases.postStations(gabrielStationDTO, getInjector()
				.getInstance(StationStrategy.class));
		gabrielStationDTO = stationsUseCases.getStations(
				gabrielStationDTO.getStationId(),
				getInjector().getInstance(StationStrategy.class), false);
		System.out.println(gabrielStationDTO);

		assertNull(gabrielStationDTO.getStationReadiness());
		assertNotNull(gabrielStationDTO.getNowPlaying());
		assertNotNull(gabrielStationDTO.getLookAhead());

		// Daniel FM should be inactive
		setRequestContextUser(daniel);
		stationsUseCases.postStations(danielStationDTO, getInjector()
				.getInstance(StationStrategy.class));
		danielStationDTO = stationsUseCases.getStations(
				danielStationDTO.getStationId(),
				getInjector().getInstance(StationStrategy.class), false);
		System.out.println(danielStationDTO);

		assertNotNull(danielStationDTO.getStationReadiness());
		assertNull(danielStationDTO.getNowPlaying());
		assertNull(danielStationDTO.getLookAhead());

		// Daniel and John FM should be active and have "recent scrobblers"
		setRequestContextUser(john);
		stationsUseCases.postStations(danielAndJohnStationDTO, getInjector()
				.getInstance(StationStrategy.class));
		danielAndJohnStationDTO = stationsUseCases.getStations(
				danielAndJohnStationDTO.getStationId(), getInjector()
						.getInstance(StationStrategy.class), false);
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
	public void putStationsTest() throws SongwichAPIException {
		StationsUseCases stationsUseCases = new StationsUseCases(getContext());
		RadioStation station;

		// Daniel and John FM should be active
		setRequestContextUser(john);
		stationsUseCases.postStations(danielAndJohnStationDTO, getInjector()
				.getInstance(StationStrategy.class));
		assertNotNull(danielAndJohnStationDTO.getNowPlaying());

		// remove a scrobbler and checks if it deactivates the station
		RadioStationUpdateDTO_V0_4 stationUpdateDTO = new RadioStationUpdateDTO_V0_4();
		stationUpdateDTO.setScrobblerIds(Arrays.asList(daniel.getId()
				.toString()));
		stationsUseCases.putStationsRemoveScrobblers(
				danielAndJohnStationDTO.getStationId(), stationUpdateDTO,
				getInjector().getInstance(StationStrategy.class));
		station = getRadioStationDAO().findById(
				new ObjectId(danielAndJohnStationDTO.getStationId()));
		assertFalse(station.isActive());
		assertFalse(new Boolean(stationUpdateDTO.getActive()));

		// add a scrobbler and checks if it reactivates the station
		stationUpdateDTO = new RadioStationUpdateDTO_V0_4();
		stationUpdateDTO.setScrobblerIds(Arrays.asList(daniel.getId()
				.toString()));
		stationsUseCases.putStationsAddScrobblers(
				danielAndJohnStationDTO.getStationId(), stationUpdateDTO,
				getInjector().getInstance(StationStrategy.class));
		station = getRadioStationDAO().findById(
				new ObjectId(danielAndJohnStationDTO.getStationId()));
		assertTrue(station.isActive());
		assertTrue(new Boolean(stationUpdateDTO.getActive()));
	}

	@Test
	public void getMultipleStationsTest() throws SongwichAPIException {
		StationsUseCases stationsUseCases = new StationsUseCases(getContext());

		setRequestContextUser(gabriel);
		stationsUseCases.postStations(gabrielStationDTO, getInjector()
				.getInstance(StationStrategy.class));
		setRequestContextUser(daniel);
		stationsUseCases.postStations(danielStationDTO, getInjector()
				.getInstance(StationStrategy.class));
		setRequestContextUser(john);
		stationsUseCases.postStations(danielAndJohnStationDTO, getInjector()
				.getInstance(StationStrategy.class));

		List<RadioStationDTO_V0_4> stationsDTO = stationsUseCases.getStations(false);
		System.out.println(stationsDTO);

		for (RadioStationDTO_V0_4 stationDTO : stationsDTO) {
			assertNotNull(stationDTO.getActive());
			assertNull(stationDTO.getStationReadiness());
			assertNull(stationDTO.getNowPlaying());
			assertNull(stationDTO.getLookAhead());
		}
	}

	private void generateScrobbles(
			Map<Integer, Integer> scrobblesArtistsGenerationMap, ObjectId userId) {
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
					getScrobbleDAO().save(scrobble,
							getContext().getAppDeveloper().getEmailAddress());
				}
			}
			registeredScrobbles = registeredScrobbles + nArtists
					* scrobblesPerArtist;
			registeredArtists = registeredArtists + nArtists;
		}
	}
}
