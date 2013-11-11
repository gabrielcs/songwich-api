package views.api.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import play.libs.Json;
import util.api.SongwichAPIException;
import views.api.APIResponse;
import views.api.APIStatus_V0_4;
import views.api.scrobbles.ScrobbleDTO_V0_4;

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
		APIResponse apiResponse = new APIResponse(
				APIStatus_V0_4.SUCCESS, "Success");

		assertEquals(Json.toJson(apiResponse).toString(),
				"{\"status\":\"0\",\"message\":\"Success\"}");
	}

	@Test
	public void scrobbleResponseToJson() throws SongwichAPIException {
		ScrobbleDTO_V0_4 scrobbleDTO = new ScrobbleDTO_V0_4();
		scrobbleDTO.setTrackTitle("Title");
		List<String> artistsNames = new ArrayList<String>();
		artistsNames.add("Name1"); artistsNames.add("Name2");
		scrobbleDTO.setArtistsNames(artistsNames);
		scrobbleDTO.setChosenByUser("false");
		scrobbleDTO.setPlayer("Spotify");
		scrobbleDTO.setTimestamp("1012528800000");

		APIResponse scrobbleResponse = new APIResponse(
				APIStatus_V0_4.SUCCESS, "Success", scrobbleDTO);
		
		assertEquals(
				Json.toJson(scrobbleResponse).toString(),
				"{\"status\":\"0\",\"message\":\"Success\",\"scrobble\":{\"trackTitle\":\"Title\",\"artistsNames\":[\"Name1\",\"Name2\"],\"chosenByUser\":\"false\",\"player\":\"Spotify\",\"timestamp\":\"1012528800000\"}}");
	}
}
