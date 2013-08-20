package usecases.api;

import java.util.List;

import models.api.NaiveStationStrategy;
import models.api.RadioStation;
import models.api.Scrobbler;
import models.api.Song;
import models.api.SongFeedback;
import models.api.SongFeedback.FeedbackType;
import models.api.StationHistoryEntry;
import models.api.StationStrategy;

import org.bson.types.ObjectId;

import usecases.api.util.RequestContext;
import usecases.api.util.UseCase;
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
				radioStation.getNowPlaying(), System.currentTimeMillis(),
				getContext().getAppDeveloper().getEmailAddress());
		stationHistoryDao.save(stationHistoryEntry);
		return stationHistoryEntry;
	}

	public void postSongFeedback(ObjectId stationHistoryEntryId,
			FeedbackType feedback) {

		StationHistoryDAO<ObjectId> stationHistoryDao = new StationHistoryDAOMongo();
		StationHistoryEntry stationHistoryEntry = stationHistoryDao
				.findById(stationHistoryEntryId);
		SongFeedback songFeedback = new SongFeedback(feedback, getContext().getUser().getId(),
				getContext().getAppDeveloper().getEmailAddress()); 
		stationHistoryEntry.addSongFeedback(songFeedback);
		
		// execute the update
		stationHistoryEntry.setLastModifiedAt(System.currentTimeMillis());
		stationHistoryEntry.setLastModifiedBy(getContext().getAppDeveloper().getEmailAddress());
		stationHistoryDao.save(stationHistoryEntry);
	}

}
