package views.api.util;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import play.libs.Json;

import views.api.ScrobbleDTO_V0_4;
import controllers.api.util.SongwichAPIException;

public class APIResponseTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void apiResponseV0_4ToJson() throws SongwichAPIException {
		APIResponse_V0_4 apiResponse = new APIResponse_V0_4(
				APIStatus_V0_4.SUCCESS, "Success");

		assertEquals(Json.toJson(apiResponse).toString(),
				"{\"status\":\"0\",\"message\":\"Success\"}");
	}

	@Test
	public void scrobbleResponseToJson() throws SongwichAPIException {
		ScrobbleDTO_V0_4 scrobbleDTO = new ScrobbleDTO_V0_4();
		scrobbleDTO.setUserEmail("gabriel@example.com");
		scrobbleDTO.setTrackTitle("Title");
		scrobbleDTO.setArtistName("Name");
		scrobbleDTO.setChosenByUser("false");
		scrobbleDTO.setService("Spotify");
		scrobbleDTO.setTimestamp("1012528800000");

		ScrobbleResponse_V0_4 scrobbleResponse = new ScrobbleResponse_V0_4(
				APIStatus_V0_4.SUCCESS, "Success", scrobbleDTO);

		assertEquals(
				Json.toJson(scrobbleResponse).toString(),
				"{\"status\":\"0\",\"message\":\"Success\",\"scrobble\":{\"trackTitle\":\"Title\",\"artistName\":\"Name\",\"chosenByUser\":\"false\",\"service\":\"Spotify\",\"timestamp\":\"1012528800000\",\"user\":\"gabriel@example.com\"}}");
	}
}
