package behavior.api.algorithms;

import java.util.Set;

import models.api.scrobbles.Song;

import org.bson.types.ObjectId;

import util.api.SongwichAPIException;

public interface StationStrategy extends StationReadinessCalculator {

	public Song getNextSong() throws SongwichAPIException;

	public Set<ObjectId> getNextSongRecentScrobblers()
			throws SongwichAPIException;
}
