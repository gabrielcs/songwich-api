package behavior.api.usecases.stations;

import java.util.List;

import models.api.scrobbles.Song;
import models.api.stations.RadioStation;
import models.api.stations.Scrobbler;
import models.api.stations.SongFeedback;
import models.api.stations.StationHistoryEntry;
import models.api.stations.SongFeedback.FeedbackType;

import org.bson.types.ObjectId;

import behavior.api.algorithms.NaiveStationStrategy;
import behavior.api.algorithms.StationStrategy;
import behavior.api.usecases.RequestContext;
import behavior.api.usecases.UseCase;

import database.api.stations.RadioStationDAO;
import database.api.stations.RadioStationDAOMongo;
import database.api.stations.StationHistoryDAO;
import database.api.stations.StationHistoryDAOMongo;

public class StationsUseCases<I> extends UseCase {

	public StationsUseCases(RequestContext context) {
		super(context);
	}

	public StationHistoryEntry getNextSong(ObjectId radioId) {
		RadioStationDAO<ObjectId> radioStationDao = new RadioStationDAOMongo();
		@SuppressWarnings("unchecked")
		RadioStation<? extends Scrobbler> radioStation = radioStationDao
				.findById(radioId);

		StationHistoryDAO<ObjectId> stationHistoryDao = new StationHistoryDAOMongo();
		List<StationHistoryEntry> stationHistory = stationHistoryDao
				.findByStationId(radioStation.getId());

		StationStrategy stationStrategy = new NaiveStationStrategy();
		String modifiedBy = getContext().getAppDeveloper().getEmailAddress();

		if (radioStation.getNowPlaying() == null) {
			// brand new station
			radioStation.setLookAhead(stationStrategy.next(radioStation
					.getScrobbler().getActiveScrobblersUserIds(),
					stationHistory, radioStation.getLookAhead()), modifiedBy);
		}

		Song next = stationStrategy.next(radioStation.getScrobbler()
				.getActiveScrobblersUserIds(), stationHistory, radioStation
				.getLookAhead());

		radioStation.setNowPlaying(radioStation.getLookAhead(), modifiedBy);
		radioStation.setLookAhead(next, modifiedBy);

		StationHistoryEntry stationHistoryEntry = new StationHistoryEntry(
				radioStation.getId(), radioStation.getNowPlaying(),
				System.currentTimeMillis(), getContext().getAppDeveloper()
						.getEmailAddress());
		stationHistoryDao.save(stationHistoryEntry);
		return stationHistoryEntry;
	}

	public void postSongFeedback(ObjectId stationHistoryEntryId,
			FeedbackType feedback) {

		StationHistoryDAO<ObjectId> stationHistoryDao = new StationHistoryDAOMongo();
		StationHistoryEntry stationHistoryEntry = stationHistoryDao
				.findById(stationHistoryEntryId);
		String appDevEmail = getContext().getAppDeveloper()
				.getEmailAddress();
		SongFeedback songFeedback = new SongFeedback(feedback, getContext()
				.getUser().getId(), appDevEmail);
		stationHistoryEntry.addSongFeedback(songFeedback, appDevEmail);

		// execute the update
		stationHistoryEntry.setLastModifiedAt(System.currentTimeMillis());
		stationHistoryEntry.setLastModifiedBy(getContext().getAppDeveloper()
				.getEmailAddress());
		stationHistoryDao.save(stationHistoryEntry);
	}
}
