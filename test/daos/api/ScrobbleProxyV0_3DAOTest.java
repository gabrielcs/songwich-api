package daos.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

import play.Logger;
import views.api.deprecated.ScrobbleProxyV0_3;
import daos.FakeWebAppWithTestData;

public class ScrobbleProxyV0_3DAOTest extends FakeWebAppWithTestData {

	public static List<String> getAllArtistNamesFromTestData() {
		return new ArrayList<String>(Arrays.asList("The Strokes",
				"Os Paralamas do Sucesso", "The Killers", "Slash", "Lenine"));
	}

	public static String getScrobbleFromTestData() {
		return "The Killers";
	}

	public static String getScrobbleNotInTestData() {
		return "Red Hot Chili Peppers";
	}

	@Test
	public void retrieveFromTestData() {
		ScrobbleProxyV0_3 scrobble = retrieveFromTestData(getScrobbleFromTestData());
		assertNotNull(scrobble);
		assertEquals(getScrobbleFromTestData(), scrobble.getName());
	}

	public static ScrobbleProxyV0_3 retrieveFromTestData(String user_id) {
		ScrobbleProxyV0_3 scrobble = ScrobbleDAO.findByUserId(getScrobbleFromTestData());
		return scrobble;
	}

	@Test
	public void saveScrobble() {
		String scrobble = getScrobbleNotInTestData();
		ScrobbleDAO.insert(new ScrobbleProxyV0_3(scrobble));

		ScrobbleProxyV0_3 artist = ScrobbleDAO.findByUserId(user_id);
		assertNotNull(artist);
		assertEquals(user_id, artist.getName());
	}

	@Test
	public void findAllScrobbles() {
		List<String> artistsNames = getAllArtistNamesFromTestData();
		List<ScrobbleProxyV0_3> scrobbles = ScrobbleDAO.findAll();
		assertNotNull(scrobbles);
		assertEquals(artistsNames.size(), scrobbles.size());
		for (ScrobbleProxyV0_3 scrobble : scrobbles) {
			assertTrue(artistsNames.contains(scrobble.getUser_id()));
			Logger.debug(scrobble.toString());
		}
	}
}
