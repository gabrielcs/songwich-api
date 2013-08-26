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

		if (radioStation.getNowPlaying() == null) {
			// brand new station
			radioStation.setLookAhead(stationStrategy.next(radioStation
					.getScrobbler().getActiveScrobblersUserIds(),
					stationHistory, radioStation.getLookAhead()));
		}

		Song next = stationStrategy.next(radioStation.getScrobbler()
				.getActiveScrobblersUserIds(), stationHistory, radioStation
				.getLookAhead());

		radioStation.setNowPlaying(radioStation.getLookAhead());
		radioStation.setLookAhead(next);

		StationHistoryEntry stationHistoryEntry = new StationHistoryEntry(
				radioStation.getId(), radioStation.getNowPlaying(),
				System.currentTimeMillis());
		stationHistoryDao.save(stationHistoryEntry, getContext()
				.getAppDeveloper().getEmailAddress());
		return stationHistoryEntry;
	}

	public void postSongFeedback(ObjectId stationHistoryEntryId,
			FeedbackType feedback) {

		StationHistoryDAO<ObjectId> stationHistoryDao = new StationHistoryDAOMongo();
		StationHistoryEntry stationHistoryEntry = stationHistoryDao
				.findById(stationHistoryEntryId);
		SongFeedback songFeedback = new SongFeedback(feedback, getContext()
				.getUser().getId());
		stationHistoryEntry.addSongFeedback(songFeedback);

		// execute the update
		stationHistoryEntry.setLastModifiedAt(System.currentTimeMillis());
		stationHistoryEntry.setLastModifiedBy(getContext().getAppDeveloper()
				.getEmailAddress());
		stationHistoryDao.save(stationHistoryEntry, getContext()
				.getAppDeveloper().getEmailAddress());
	}
}
