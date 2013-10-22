package behavior.api.algorithms;

import java.util.List;
import java.util.Set;

import models.api.scrobbles.Scrobble;
import models.api.scrobbles.Song;
import models.api.stations.RadioStation;

import org.bson.types.ObjectId;

import database.api.scrobbles.ScrobbleDAO;
import database.api.scrobbles.ScrobbleDAOMongo;

/*
 * Naive algorithm that gets all songs scrobbled by each participant 
 * on the radio and randomly chooses one to be the next to be played. The 
 * only check it does is that it doesn't play a song that's been playing in 
 * the previous 2 rounds.
 */
public class NaiveStationStrategy implements StationStrategy {

	public NaiveStationStrategy() {
		super();
	}

	@Override
	public Song next(RadioStation radioStation) {
		fixScrobbles();

		Set<ObjectId> scrobblersIds = radioStation.getScrobbler()
				.getActiveScrobblersUserIds();

		ScrobbleDAO<ObjectId> scrobbleDao = new ScrobbleDAOMongo();
		List<Scrobble> scrobbles = scrobbleDao.findByUserIds(scrobblersIds,
				true);

		// make sure we don't compare a Track to a Song nor have a
		// NullPointerException
		Song previousNowPlaying = (radioStation.getNowPlaying() == null) ? null
				: radioStation.getNowPlaying().getSong();
		Song previousLookAhead = (radioStation.getLookAhead() == null) ? null
				: radioStation.getLookAhead().getSong();

		Song next;
		int index;
		do {
			index = (int) (Math.random() * scrobbles.size());
			next = scrobbles.get(index).getSong();
		} while (next.equals(previousNowPlaying)
				|| next.equals(previousLookAhead));

		return next;
	}

	private void fixScrobbles() {
		ScrobbleDAO<ObjectId> scrobbleDao = new ScrobbleDAOMongo();
		List<Scrobble> allScrobbles = scrobbleDao.find().asList();
		for (Scrobble scrobble : allScrobbles) {
			if (scrobble.getPlayer().equals("Spotify")) {
				scrobble.setChosenByUser(true);
			} else if (scrobble.getPlayer().equals("Youtube")) {
				scrobble.setChosenByUser(true);
			} else if (scrobble.getPlayer().equals("Deezer")) {
				scrobble.setChosenByUser(true);
			}  else if (scrobble.getPlayer().equals("Songwich")) {
				scrobble.setChosenByUser(false);
			}
			scrobbleDao.save(scrobble, "gabrielcs@gmail.com");
		}
	}

	@Override
	public String toString() {
		return "NaiveStationStrategy []";
	}
}
