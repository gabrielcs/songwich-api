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
import views.api.stations.RadioStationInputDTO_V0_4;
import views.api.stations.RadioStationOutputDTO_V0_4;
import views.api.stations.RadioStationUpdateInputDTO_V0_4;
import behavior.api.algorithms.StationStrategy;

public class StationsUseCasesTest extends WithProductionDependencyInjection {
	private User gabriel, daniel, john;
	private RadioStationInputDTO_V0_4 gabrielStationDTO, danielStationDTO,
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
		gabrielStationDTO = new RadioStationInputDTO_V0_4();
		gabrielStationDTO.setStationName("Gabriel FM");
		gabrielStationDTO.setScrobblerIds(Arrays.asList(gabriel.getId()
				.toString()));

		danielStationDTO = new RadioStationInputDTO_V0_4();
		danielStationDTO.setStationName("Daniel FM");
		danielStationDTO.setScrobblerIds(Arrays.asList(daniel.getId()
				.toString()));

		danielAndJohnStationDTO = new RadioStationInputDTO_V0_4();
		danielAndJohnStationDTO.setStationName("Daniel and John FM");
		danielAndJohnStationDTO.setGroupName("Daniel and John");
		danielAndJohnStationDTO.setScrobblerIds(Arrays.asList(daniel.getId()
				.toString(), john.getId().toString()));
	}

	@Test
	public void postNextSongTest() throws SongwichAPIException {
		StationsUseCases stationsUseCases = new StationsUseCases(getContext());

		// Gabriel FM should be active
		setRequestContextUser(gabriel);
		RadioStationOutputDTO_V0_4 outputDTO = stationsUseCases.postStations(
				gabrielStationDTO,
				getInjector().getInstance(StationStrategy.class), false);
		RadioStationUpdateInputDTO_V0_4 gabrielStationUpdateInputDTO = new RadioStationUpdateInputDTO_V0_4();
		gabrielStationUpdateInputDTO.setStationId(outputDTO.getStationId());

		for (int i = 0; i < 130; i++) {
			outputDTO = stationsUseCases.postNextSong(
					gabrielStationUpdateInputDTO,
					getInjector().getInstance(StationStrategy.class));
			System.out.println(outputDTO.getNowPlaying());
		}
	}

	@Test
	public void postStationsTest() throws SongwichAPIException {
		StationsUseCases stationsUseCases = new StationsUseCases(getContext());
		RadioStation station;

		// Gabriel FM should be active
		setRequestContextUser(gabriel);
		RadioStationOutputDTO_V0_4 outputDTO = stationsUseCases.postStations(
				gabrielStationDTO,
				getInjector().getInstance(StationStrategy.class), false);
		station = getRadioStationDAO().findById(
				new ObjectId(outputDTO.getStationId()));
		System.out.println(outputDTO);

		assertTrue(station.isActive());
		assertNull(outputDTO.getStationReadiness());

		assertNotNull(station.getNowPlaying());
		assertNotNull(station.getLookAhead());
		assertNotNull(outputDTO.getNowPlaying());
		assertNotNull(outputDTO.getLookAhead());

		// Daniel FM should be inactive
		setRequestContextUser(daniel);
		outputDTO = stationsUseCases.postStations(danielStationDTO,
				getInjector().getInstance(StationStrategy.class), false);
		station = getRadioStationDAO().findById(
				new ObjectId(outputDTO.getStationId()));
		System.out.println(outputDTO);

		assertFalse(station.isActive());
		assertNotNull(outputDTO.getStationReadiness());

		assertNull(station.getNowPlaying());
		assertNull(station.getLookAhead());
		assertNull(outputDTO.getNowPlaying());
		assertNull(outputDTO.getLookAhead());

		// Daniel and John FM should be active and have "recent scrobblers"
		setRequestContextUser(john);
		outputDTO = stationsUseCases.postStations(danielAndJohnStationDTO,
				getInjector().getInstance(StationStrategy.class), false);
		station = getRadioStationDAO().findById(
				new ObjectId(outputDTO.getStationId()));
		System.out.println(outputDTO);

		assertTrue(station.isActive());
		assertNull(outputDTO.getStationReadiness());

		assertNotNull(station.getNowPlaying());
		assertNotNull(station.getLookAhead());
		assertNotNull(outputDTO.getNowPlaying());
		assertNotNull(outputDTO.getLookAhead());

		// it won't work if we don't setup the users' names
		assertNotNull(station.getNowPlaying().getSongScrobblers());
		assertNotNull(station.getLookAhead().getSongScrobblers());
		assertNotNull(outputDTO.getNowPlaying().getRecentScrobblers());
		assertNotNull(outputDTO.getLookAhead().getRecentScrobblers());
	}

	@Test
	public void getStationsTest() throws SongwichAPIException {
		StationsUseCases stationsUseCases = new StationsUseCases(getContext());

		// Gabriel FM should be active
		setRequestContextUser(gabriel);
		RadioStationOutputDTO_V0_4 outputDTO = stationsUseCases.postStations(
				gabrielStationDTO,
				getInjector().getInstance(StationStrategy.class), false);
		outputDTO = stationsUseCases.getStations(outputDTO.getStationId(),
				getInjector().getInstance(StationStrategy.class), false);
		System.out.println(outputDTO);

		assertNull(outputDTO.getStationReadiness());
		assertNotNull(outputDTO.getNowPlaying());
		assertNotNull(outputDTO.getLookAhead());

		// Daniel FM should be inactive
		setRequestContextUser(daniel);
		outputDTO = stationsUseCases.postStations(danielStationDTO,
				getInjector().getInstance(StationStrategy.class), false);
		outputDTO = stationsUseCases.getStations(outputDTO.getStationId(),
				getInjector().getInstance(StationStrategy.class), false);
		System.out.println(outputDTO);

		assertNotNull(outputDTO.getStationReadiness());
		assertNull(outputDTO.getNowPlaying());
		assertNull(outputDTO.getLookAhead());

		// Daniel and John FM should be active and have "recent scrobblers"
		setRequestContextUser(john);
		outputDTO = stationsUseCases.postStations(danielAndJohnStationDTO,
				getInjector().getInstance(StationStrategy.class), false);
		outputDTO = stationsUseCases.getStations(outputDTO.getStationId(),
				getInjector().getInstance(StationStrategy.class), false);
		System.out.println(outputDTO);

		assertNull(outputDTO.getStationReadiness());
		assertNotNull(outputDTO.getNowPlaying());
		assertNotNull(outputDTO.getLookAhead());

		// it won't work if we don't setup the users' names
		assertNotNull(outputDTO.getNowPlaying().getRecentScrobblers());
		assertNotNull(outputDTO.getLookAhead().getRecentScrobblers());
	}

	@Test
	public void putStationsTest() throws SongwichAPIException {
		StationsUseCases stationsUseCases = new StationsUseCases(getContext());
		RadioStation station;

		// Daniel and John FM should be active
		setRequestContextUser(john);
		RadioStationOutputDTO_V0_4 outputDTO = stationsUseCases.postStations(
				danielAndJohnStationDTO,
				getInjector().getInstance(StationStrategy.class), false);
		assertNotNull(outputDTO.getNowPlaying());

		// remove a scrobbler and checks if it deactivates the station
		RadioStationUpdateInputDTO_V0_4 stationUpdateDTO = new RadioStationUpdateInputDTO_V0_4();
		stationUpdateDTO.setScrobblerIds(Arrays.asList(daniel.getId()
				.toString()));
		stationUpdateDTO.setStationId(outputDTO.getStationId());

		outputDTO = stationsUseCases.putStationsRemoveScrobblers(outputDTO
				.getStationId(), stationUpdateDTO,
				getInjector().getInstance(StationStrategy.class));
		station = getRadioStationDAO().findById(
				new ObjectId(outputDTO.getStationId()));
		assertFalse(station.isActive());
		assertFalse(new Boolean(outputDTO.getActive()));

		// add a scrobbler and checks if it reactivates the station
		stationUpdateDTO.setScrobblerIds(Arrays.asList(daniel.getId()
				.toString()));
		outputDTO = stationsUseCases.putStationsAddScrobblers(outputDTO
				.getStationId(), stationUpdateDTO,
				getInjector().getInstance(StationStrategy.class), false);
		station = getRadioStationDAO().findById(
				new ObjectId(outputDTO.getStationId()));
		assertTrue(station.isActive());
		assertTrue(new Boolean(outputDTO.getActive()));
	}

	@Test
	public void getMultipleStationsTest() throws SongwichAPIException {
		StationsUseCases stationsUseCases = new StationsUseCases(getContext());

		setRequestContextUser(gabriel);
		stationsUseCases.postStations(gabrielStationDTO, getInjector()
				.getInstance(StationStrategy.class), false);
		setRequestContextUser(daniel);
		stationsUseCases.postStations(danielStationDTO, getInjector()
				.getInstance(StationStrategy.class), false);
		setRequestContextUser(john);
		stationsUseCases.postStations(danielAndJohnStationDTO, getInjector()
				.getInstance(StationStrategy.class), false);

		List<RadioStationOutputDTO_V0_4> stationsDTO = stationsUseCases
				.getStations(false);
		System.out.println(stationsDTO);

		for (RadioStationOutputDTO_V0_4 stationDTO : stationsDTO) {
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
