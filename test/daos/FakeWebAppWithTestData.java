package daos;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

import java.util.List;
import java.util.Map;

import models.music_streaming.Artist;
import models.music_streaming.StreamingService;
import models.radio_waiting_list.PotentialNewArtistManager;
import models.radio_waiting_list.PotentialNewListener;

import org.junit.Before;

import play.libs.Yaml;
import play.test.WithApplication;

import com.avaje.ebean.Ebean;

import daos.music_streaming.ArtistDAO;
import daos.music_streaming.StreamingServiceDAO;

public abstract class FakeWebAppWithTestData extends WithApplication {

	@Before
	public void setUp() throws Exception {
		startWebApp();
		loadTestData();
	}

	private void startWebApp() {
		start(fakeApplication(inMemoryDatabase()));
	}

	// load test data from conf/test-data.yml
	private void loadTestData() {
		if (Ebean.find(Artist.class).findRowCount() == 0) {
			// load yaml
			Map<String, List<Object>> testData = (Map<String, List<Object>>) Yaml
					.load("test-data.yml");

			saveArtists(testData);
			saveStreamingServices(testData);
			savePotentialNewListeners(testData);
			savePotentialNewArtistManagers(testData);
		}
	}

	private void saveArtists(Map<String, List<Object>> testData) {
		Ebean.save(testData.get("artists"));
	}

	private void saveStreamingServices(Map<String, List<Object>> testData) {
		Ebean.save(testData.get("streamingServices"));
	}

	private void retrieveArtistsFromDb(
			PotentialNewArtistManager potentialNewArtistManager) {
		String managedArtistName = potentialNewArtistManager.getManagedArtist()
				.getName();
		Artist dbArtist = ArtistDAO.findByName(managedArtistName);
		// it's necessary to call setManagedArtist()
		potentialNewArtistManager.setManagedArtist(dbArtist);
	}

	private void retrieveArtistsFromDb(PotentialNewListener potentialNewListener) {
		List<Artist> artists = potentialNewListener.getInterestInArtists();
		for (Artist artist : artists) {
			Long dbArtistId = ArtistDAO.findByName(artist.getName())
					.getDatabaseId();
			artist.setDatabaseId(dbArtistId);
		}
	}

	private void retrieveStreamingServicesFromDb(
			PotentialNewListener potentialNewListener) {
		List<MusicService> streamingServices = potentialNewListener
				.getPayingSubscriptions();
		for (MusicService streamingService : streamingServices) {
			Long dbStreamingServiceId = StreamingServiceDAO.findByName(
					streamingService.getName()).getDatabaseId();
			streamingService.setDatabaseId(dbStreamingServiceId);
		}
	}
}
