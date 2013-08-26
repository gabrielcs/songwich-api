package behavior.api.algorithms;

import java.util.List;
import java.util.Set;

import models.api.scrobbles.Scrobble;
import models.api.scrobbles.Song;
import models.api.stations.StationHistoryEntry;

import org.bson.types.ObjectId;

import database.api.scrobbles.ScrobbleDAO;
import database.api.scrobbles.ScrobbleDAOMongo;

/*
 * Naive algorithm that gets the 5 last songs scrobbled by each participant 
 * on the radio and randomly chooses one to be the next to be played. The 
 * only check it does is that it doesn't play the same song twice in a row.
 */
public class NaiveStationStrategy implements StationStrategy {

	public NaiveStationStrategy() {
		super();
	}

	@Override
	public Song next(Set<ObjectId> scrobblersIds,
			List<StationHistoryEntry> history, Song lookAhead) {
		
		ScrobbleDAO<ObjectId> scrobbleDao = new ScrobbleDAOMongo();
		List<Scrobble> scrobbles = scrobbleDao.findLastScrobblesByUserIds(
				scrobblersIds, 5, false);

		Song next;
		int index;
		do {
			index = (int) (Math.random() * scrobbles.size());
			next = scrobbles.get(index).getSong();
		} while (next.equals(lookAhead));

		return next;
	}

	@Override
	public String toString() {
		return "NaiveStationStrategy []";
	}
}
