package usecases.api;

import java.util.List;

import models.api.NaiveStationStrategy;
import models.api.RadioStation;
import models.api.Scrobbler;
import models.api.Song;
import models.api.StationHistoryEntry;
import models.api.StationStrategy;

import org.bson.types.ObjectId;

import usecases.api.util.RequestContext;
import usecases.api.util.UseCase;
import database.api.RadioStationDAO;
import database.api.RadioStationDAOMongo;
import database.api.StationHistoryDAO;
import database.api.StationHistoryDAOMongo;

public class RadioUseCases<I> extends UseCase {

	public RadioUseCases(RequestContext context) {
		super(context);
	}

	public StationHistoryEntry nextSong(ObjectId radioId) {
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

}
