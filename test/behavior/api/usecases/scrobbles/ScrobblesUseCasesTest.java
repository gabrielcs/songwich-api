package behavior.api.usecases.scrobbles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import models.api.scrobbles.Scrobble;
import models.api.scrobbles.Song;
import models.api.scrobbles.User;

import org.apache.commons.lang3.tuple.Pair;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import util.api.SongwichAPIException;
import util.api.WithProductionDependencyInjection;
import views.api.scrobbles.ScrobblesDTO_V0_4;
import views.api.scrobbles.ScrobblesPagingDTO_V0_4;

public class ScrobblesUseCasesTest extends WithProductionDependencyInjection {
	private User gabriel;
	private ArrayList<Scrobble> scrobbles;
	private final String LOCALHOST = "localhost:9000";

	@Before
	public void setUp() throws Exception {
		super.setUp();
		createScrobbles();
	}

	private void createScrobbles() {
		gabriel = new User("gabriel@example.com", "Gabriel");
		getUserDAO().save(gabriel,
				getContext().getAppDeveloper().getEmailAddress());

		// scrobbles
		Map<Integer, Integer> scrobblesArtistsGenerationMap = new LinkedHashMap<Integer, Integer>(
				10);
		// for example, scrobbles: 1 song each for 20 artists
		scrobblesArtistsGenerationMap.put(1, 20);
		scrobblesArtistsGenerationMap.put(2, 10);
		scrobblesArtistsGenerationMap.put(3, 4);
		scrobblesArtistsGenerationMap.put(5, 2);
		scrobblesArtistsGenerationMap.put(12, 1);
		// total: 74 scrobbles
		generateScrobbles(scrobblesArtistsGenerationMap, gabriel.getId());
	}

	@Test
	public void testGetScrobbles() throws SongwichAPIException {
		setRequestContextUser(gabriel);
		ScrobblesUseCases scrobblesUseCases = new ScrobblesUseCases(
				getContext());

		final int RESULTS = 30;
		Pair<List<ScrobblesDTO_V0_4>, ScrobblesPagingDTO_V0_4> response = scrobblesUseCases
				.getScrobbles(LOCALHOST, gabriel.getId().toString(), RESULTS,
						false, System.currentTimeMillis());

		assertEquals(RESULTS, response.getLeft().size());
		assertEquals(scrobbles.get(scrobbles.size() - 1).getId().toString(),
				response.getLeft().get(0).getScrobbleId());
		assertEquals(scrobbles.get(scrobbles.size() - RESULTS).getId()
				.toString(),
				response.getLeft().get(response.getLeft().size() - 1)
						.getScrobbleId());

		assertTrue(response
				.getRight()
				.getNewerScrobbles()
				.contains(
						"since="
								+ scrobbles.get(scrobbles.size() - 1)
										.getTimestamp()));
		assertTrue(response
				.getRight()
				.getOlderScrobbles()
				.contains(
						"until="
								+ scrobbles.get(scrobbles.size() - RESULTS)
										.getTimestamp()));
	}

	@Test
	public void testGetScrobblesUntil() throws SongwichAPIException {
		setRequestContextUser(gabriel);
		ScrobblesUseCases scrobblesUseCases = new ScrobblesUseCases(
				getContext());

		final int RESULTS = 30;
		final int UNTIL_INDEX = scrobbles.size() / 2;
		final long UNTIL = scrobbles.get(UNTIL_INDEX).getTimestamp();

		Pair<List<ScrobblesDTO_V0_4>, ScrobblesPagingDTO_V0_4> response = scrobblesUseCases
				.getScrobblesUntil(LOCALHOST, gabriel.getId().toString(),
						UNTIL, RESULTS, false);

		assertEquals(RESULTS, response.getLeft().size());
		assertEquals(scrobbles.get(UNTIL_INDEX - 1).getId().toString(),
				response.getLeft().get(0).getScrobbleId());
		assertEquals(scrobbles.get(UNTIL_INDEX - RESULTS).getId().toString(),
				response.getLeft().get(response.getLeft().size() - 1)
						.getScrobbleId());

		assertTrue(response
				.getRight()
				.getOlderScrobbles()
				.contains(
						"until="
								+ scrobbles.get(UNTIL_INDEX - RESULTS)
										.getTimestamp()));

		assertTrue(response
				.getRight()
				.getNewerScrobbles()
				.contains(
						"since="
								+ scrobbles.get(UNTIL_INDEX - 1).getTimestamp()));
	}

	@Test
	public void testGetScrobblesSince() throws SongwichAPIException {
		setRequestContextUser(gabriel);
		ScrobblesUseCases scrobblesUseCases = new ScrobblesUseCases(
				getContext());

		final int RESULTS = 30;
		final int SINCE_INDEX = scrobbles.size() / 2;
		final long SINCE = scrobbles.get(SINCE_INDEX).getTimestamp();

		Pair<List<ScrobblesDTO_V0_4>, ScrobblesPagingDTO_V0_4> response = scrobblesUseCases
				.getScrobblesSince(LOCALHOST, gabriel.getId().toString(),
						SINCE, RESULTS, false);

		System.out.println(SINCE_INDEX);
		System.out.println(SINCE);
		System.out.println(response);

		assertEquals(RESULTS, response.getLeft().size());
		assertEquals(scrobbles.get(SINCE_INDEX + RESULTS).getId().toString(),
				response.getLeft().get(0).getScrobbleId());
		assertEquals(scrobbles.get(SINCE_INDEX + 1).getId().toString(),
				response.getLeft().get(response.getLeft().size() - 1)
						.getScrobbleId());

		assertTrue(response
				.getRight()
				.getNewerScrobbles()
				.contains(
						"since="
								+ scrobbles.get(SINCE_INDEX + RESULTS)
										.getTimestamp()));
		assertTrue(response
				.getRight()
				.getOlderScrobbles()
				.contains(
						"until="
								+ scrobbles.get(SINCE_INDEX + 1).getTimestamp()));
	}

	private void generateScrobbles(
			Map<Integer, Integer> scrobblesArtistsGenerationMap, ObjectId userId) {

		scrobbles = new ArrayList<Scrobble>(
				scrobblesArtistsGenerationMap.size());
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
					scrobbles.add(scrobble);
				}
			}
			registeredScrobbles = registeredScrobbles + nArtists
					* scrobblesPerArtist;
			registeredArtists = registeredArtists + nArtists;
		}
	}
}
