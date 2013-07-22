package views.api.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
	public void apiResponseV0_5ToJson() throws SongwichAPIException {
		APIResponse_V0_4 apiResponse = new APIResponse_V0_4(
				views.api.util.Status.SUCCESS, "Success");

		assertEquals(apiResponse.toJson().toString(),
				"{\"status\":\"0\",\"message\":\"Success\"}");
	}

	@Test
	public void scrobbleResponseToJson() throws SongwichAPIException {
		ScrobbleResponse_V0_4 scrobbleResponse = new ScrobbleResponse_V0_4(
				views.api.util.Status.SUCCESS, "Success", new ScrobbleDTO_V0_4(
						"gabriel@example.com", "Title", "Name", "false",
						"Spotify", "1012528800000"));

		assertEquals(
				scrobbleResponse.toJson().toString(),
				"{\"status\":\"0\",\"message\":\"Success\",\"scrobble\":{\"trackTitle\":\"Title\",\"artistName\":\"Name\",\"chosenByUser\":\"false\",\"service\":\"Spotify\",\"timestamp\":\"1012528800000\",\"user\":\"gabriel@example.com\"}}");
	}

}
