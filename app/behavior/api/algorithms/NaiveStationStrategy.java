package behavior.api.algorithms;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.api.scrobbles.Scrobble;
import models.api.scrobbles.Song;
import models.api.stations.RadioStation;

import org.bson.types.ObjectId;

import util.api.MyLogger;
import database.api.scrobbles.ScrobbleDAO;
import database.api.scrobbles.ScrobbleDAOMongo;

/*
 * Naive algorithm that gets all songs scrobbled by each participant 
 * on the radio and randomly chooses one to be the next to be played. The 
 * only check it does is that it doesn't play a song that's been playing in 
 * the previous 2 rounds.
 */
public class NaiveStationStrategy //implements StationStrategy 
{

	private RadioStation radioStation;
	private Song previousNowPlaying, previousLookAhead, nextSong;
	List<Scrobble> scrobbles;
	private Set<ObjectId> recentScrobblers;

	public NaiveStationStrategy(RadioStation radioStation) {
		super();
		this.radioStation = radioStation;

		// make sure we don't compare a Track to a Song nor have a
		// NullPointerException
		previousNowPlaying = (radioStation.getNowPlaying() == null) ? null
				: radioStation.getNowPlaying().getSong();
		previousLookAhead = (radioStation.getLookAhead() == null) ? null
				: radioStation.getLookAhead().getSong();
	}

	//@Override
	public Song getNextSong() {
		if (nextSong != null) {
			// the algorithm has already been invoked
			return nextSong;
		}

		Set<ObjectId> scrobblersIds = radioStation.getScrobbler()
				.getActiveScrobblersUserIds();
		MyLogger.debug("radioStation: " + radioStation);
		MyLogger.debug("scrobblersIds: " + scrobblersIds);
		ScrobbleDAO<ObjectId> scrobbleDao = new ScrobbleDAOMongo();
		scrobbles = scrobbleDao.findByUserIds(scrobblersIds, true);
		MyLogger.debug("scrobbles: " + scrobbles);
				
		int index;
		do {
			index = (int) (Math.random() * scrobbles.size());
			MyLogger.debug(String.format("index = %s); scrobbles.size = %s", index, scrobbles.size()));
			nextSong = scrobbles.get(index).getSong();
		} while (nextSong.equals(previousNowPlaying)
				|| nextSong.equals(previousLookAhead));

		return nextSong;
	}

	//@Override
	public Set<ObjectId> getRecentScrobblers() {
		if (recentScrobblers != null) {
			// scrobblers have already been identified
			return recentScrobblers;
		}

		recentScrobblers = new HashSet<ObjectId>();
		for (Scrobble scrobble : scrobbles) {
			if (scrobble.getSong().equals(nextSong)) {
				recentScrobblers.add(scrobble.getUserId());
			}
		}
		return recentScrobblers;
	}

	@Override
	public String toString() {
		return "NaiveStationStrategy []";
	}
}
