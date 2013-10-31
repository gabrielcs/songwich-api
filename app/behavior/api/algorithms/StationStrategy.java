package behavior.api.algorithms;

import java.util.Set;

import models.api.scrobbles.Song;

import org.bson.types.ObjectId;

import util.api.SongwichAPIException;

public interface StationStrategy {

	public Song getNextSong() throws SongwichAPIException;
	
	public Set<ObjectId> getRecentScrobblers();

}
