package models.api;

import java.util.Set;

import org.bson.types.ObjectId;

public interface Scrobbler {
	
	public Set<ObjectId> getActiveScrobblersUserIds();

}
