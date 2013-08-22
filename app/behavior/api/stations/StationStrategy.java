package behavior.api.stations;

import java.util.List;
import java.util.Set;

import models.api.scrobbles.Song;
import models.api.stations.StationHistoryEntry;

import org.bson.types.ObjectId;

public interface StationStrategy {
	
	public Song next(Set<ObjectId> scrobblersIds,
			List<StationHistoryEntry> history, Song lookAhead);
	
}
