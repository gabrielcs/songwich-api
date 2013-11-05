package behavior.api.algorithms;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.api.scrobbles.Scrobble;
import models.api.scrobbles.Song;
import models.api.stations.RadioStation;

import org.bson.types.ObjectId;

import util.api.SongwichAPIException;

public abstract class AbstractStationStrategy implements StationStrategy {

	private RadioStation station;

	private Set<ObjectId> nextSongRecentScrobblersIds;

	protected AbstractStationStrategy(RadioStation station) {
		this.station = station;
	}

	protected abstract List<Scrobble> getRelevantScrobbles();

	protected abstract StationReadinessCalculator getStationReadinessCalculator();

	@Override
	public abstract Song getNextSong() throws SongwichAPIException;

	@Override
	public Set<ObjectId> getNextSongRecentScrobblers()
			throws SongwichAPIException {

		if (nextSongRecentScrobblersIds != null) {
			// scrobblers have already been identified
			return nextSongRecentScrobblersIds;
		}

		nextSongRecentScrobblersIds = new HashSet<ObjectId>();
		for (Scrobble scrobble : getRelevantScrobbles()) {
			if (scrobble.getSong().equals(getNextSong())) {
				nextSongRecentScrobblersIds.add(scrobble.getUserId());
			}
		}
		return nextSongRecentScrobblersIds;
	}

	protected Set<ObjectId> getActiveScrobblers() {
		return station.getScrobbler().getActiveScrobblersUserIds();
	}

	@Override
	public Float getStationReadiness() {
		return getStationReadinessCalculator().getStationReadiness();
	}

	@Override
	public Boolean isStationReady() {
		return getStationReadinessCalculator().isStationReady();
	}
	
	protected RadioStation getStation() {
		return station;
	}

	@Override
	public String toString() {
		return "AbstractStationStrategy [station=" + station
				+ ", nextSongRecentScrobblersIds="
				+ nextSongRecentScrobblersIds + "]";
	}
}
