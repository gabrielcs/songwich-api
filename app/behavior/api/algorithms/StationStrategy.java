package behavior.api.algorithms;

import java.util.Set;

import models.api.scrobbles.Song;
import models.api.stations.RadioStation;

import org.bson.types.ObjectId;

import util.api.SongwichAPIException;

public interface StationStrategy extends StationReadinessCalculator {
	/**
	 * @throws IllegalStateException if setStation() has already been called
	 */
	public void setStation(RadioStation station);
	
	/**
	 * @throws IllegalStateException if setStation() hasn't been called first
	 */
	public Song getNextSong() throws SongwichAPIException, IllegalStateException;

	/**
	 * @throws IllegalStateException if setStation() hasn't been called first
	 */
	public Set<ObjectId> getNextSongRecentScrobblers()
			throws SongwichAPIException, IllegalStateException;
}
