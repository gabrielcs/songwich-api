package behavior.api.algorithms;

import java.util.List;
import java.util.Set;

import models.api.scrobbles.Scrobble;
import models.api.scrobbles.Song;

import org.bson.types.ObjectId;

import database.api.scrobbles.ScrobbleDAO;
import database.api.scrobbles.ScrobbleDAOMongo;

/*
 * Naive algorithm that gets all songs scrobbled by each participant 
 * on the radio and randomly chooses one to be the next to be played. The 
 * only check it does is that it doesn't play a song that's been playing in 
 * the previous 2 rounds.
 */
public class NaiveStationStrategy extends AbstractStationStrategy implements
		StationStrategy {

	private NaiveStationReadinessCalculator readinessCalculator;
	private Song nextSong;
	private List<Scrobble> relevantScrobbles;

	public NaiveStationStrategy() {
	}
	
	@Override
	public StationStrategy reset() {
		readinessCalculator = null;
		relevantScrobbles = null;
		nextSong = null;
		return super.reset();
	}

	@Override
	public Song getNextSong() {
		if (getStation() == null) {
			throw new IllegalStateException("setStation() should be called first");
		}
		
		if (nextSong != null) {
			// the algorithm has already been invoked
			return nextSong;
		}

		// make sure we don't compare a Track to a Song nor have a
		// NullPointerException
		Song previousNowPlaying = (getStation().getNowPlaying() == null) ? null
				: getStation().getNowPlaying().getSong();
		Song previousLookAhead = (getStation().getLookAhead() == null) ? null
				: getStation().getLookAhead().getSong();

		int index;
		do {
			index = (int) (Math.random() * getRelevantScrobbles().size());
			nextSong = getRelevantScrobbles().get(index).getSong();
		} while (nextSong.equals(previousNowPlaying)
				|| nextSong.equals(previousLookAhead)); // Song.equals() is case insensitive

		return nextSong;
	}

	@Override
	protected List<Scrobble> getRelevantScrobbles() {
		if (getStation() == null) {
			throw new IllegalStateException("setStation() should be called first");
		}
		
		if (relevantScrobbles != null) {
			return relevantScrobbles;
		}

		// all scrobbles by the station's active scrobblers
		Set<ObjectId> scrobblersIds = getStation().getScrobbler()
				.getActiveScrobblersUserIds();
		ScrobbleDAO<ObjectId> scrobbleDao = new ScrobbleDAOMongo();
		relevantScrobbles = scrobbleDao.findAllByUserIds(scrobblersIds, true);
		return relevantScrobbles;
	}

	@Override
	protected StationReadinessCalculator getStationReadinessCalculator() {
		if (readinessCalculator == null) {
			readinessCalculator = this.new NaiveStationReadinessCalculator();
		}
		return readinessCalculator;
	}

	@Override
	public String toString() {
		return "NaiveStationStrategy [readinessCalculator="
				+ readinessCalculator + ", nextSong=" + nextSong
				+ ", relevantScrobbles=" + relevantScrobbles
				+ ", super.toString()=" + super.toString() + "]";
	}

	public class NaiveStationReadinessCalculator extends
			AbstractStationReadinessCalculator implements
			StationReadinessCalculator {

		protected NaiveStationReadinessCalculator() {
		}

		@Override
		protected Float calculateStationReadiness() {
			// minimum of 3 relevant scrobbles
			final float MIN_SONGS = 3f;
			return ((getRelevantScrobbles().size() / MIN_SONGS) >= 1) ? 1f
					: (getRelevantScrobbles().size() / MIN_SONGS);
		}

		@Override
		public String toString() {
			return "NaiveStationReadinessCalculator [super.toString()="
					+ super.toString() + "]";
		}
	}

}
