package views.api.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import views.api.ScrobbleDTO;
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
	public void apiResponseV0_5ToJson() throws SongwichAPIException {
		APIResponseV0_5 apiResponse = new APIResponseV0_5(
				views.api.util.Status.SUCCESS, "Success");

		assertEquals(apiResponse.toJson().toString(),
				"{\"status\":\"0\",\"message\":\"Success\"}");
	}

	@Test
	public void scrobbleResponseToJson() throws SongwichAPIException {
		ScrobbleResponse scrobbleResponse = new ScrobbleResponse(
				views.api.util.Status.SUCCESS, "Success", new ScrobbleDTO(
						"gabriel@example.com", "Title", "Name", "false",
						"Spotify", "1012528800000"));

		assertEquals(
				scrobbleResponse.toJson().toString(),
				"{\"status\":\"0\",\"message\":\"Success\",\"scrobble\":{\"trackTitle\":\"Title\",\"artistName\":\"Name\",\"chosenByUser\":\"false\",\"service\":\"Spotify\",\"timestamp\":\"1012528800000\",\"user\":\"gabriel@example.com\"}}");
	}

}
