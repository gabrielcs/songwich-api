package models.api.stations;

import java.util.Set;

import models.api.MongoModel;

import org.bson.types.ObjectId;

public interface Scrobbler extends MongoModel {
	
	public Set<ObjectId> getActiveScrobblersUserIds();

}
