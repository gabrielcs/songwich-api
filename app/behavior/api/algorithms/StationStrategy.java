package behavior.api.algorithms;

import java.util.Set;

import models.api.scrobbles.Song;

import org.bson.types.ObjectId;

public interface StationStrategy {

	public Song getNextSong();
	
	public Set<ObjectId> getRecentScrobblers();

}
