package models.api;

import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;

public interface StationStrategy {
	
	public Song next(Set<ObjectId> scrobblersIds,
			List<StationHistoryEntry> history, Song lookAhead);
	
}
