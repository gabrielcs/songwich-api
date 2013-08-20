package usecases.api;

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

public class RadioUseCases<I> extends UseCase {

	public RadioUseCases(RequestContext context) {
		super(context);
	}

	public StationHistoryEntry nextSong(ObjectId radioId) {
		RadioStationDAO<ObjectId> radioStationDao = new RadioStationDAOMongo();
		@SuppressWarnings("unchecked")
		RadioStation<? extends Scrobbler> radioStation = radioStationDao
				.findById(radioId);

		StationStrategy stationStrategy = new NaiveStationStrategy();

		if (radioStation.getNowPlaying() == null) {
			// brand new station
			radioStation.setLookAhead(stationStrategy.next(radioStation
					.getScrobbler().getActiveScrobblersUserIds(), radioStation
					.getHistory(), radioStation.getLookAhead()));
		}

		Song next = stationStrategy.next(radioStation.getScrobbler()
				.getActiveScrobblersUserIds(), radioStation.getHistory(),
				radioStation.getLookAhead());

		radioStation.setNowPlaying(radioStation.getLookAhead());
		radioStation.setLookAhead(next);

		StationHistoryEntry stationHistoryEntry = new StationHistoryEntry(
				radioStation.getNowPlaying(), System.currentTimeMillis());
		radioStation.getHistory().add(stationHistoryEntry);
		return stationHistoryEntry;
	}

}
