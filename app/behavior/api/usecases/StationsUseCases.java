package behavior.api.usecases;

import java.util.List;

import models.api.scrobbles.Song;
import models.api.stations.RadioStation;
import models.api.stations.Scrobbler;
import models.api.stations.SongFeedback;
import models.api.stations.StationHistoryEntry;
import models.api.stations.SongFeedback.FeedbackType;

import org.bson.types.ObjectId;

import behavior.api.stations.NaiveStationStrategy;
import behavior.api.stations.StationStrategy;

import database.api.RadioStationDAO;
import database.api.RadioStationDAOMongo;
import database.api.StationHistoryDAO;
import database.api.StationHistoryDAOMongo;

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
