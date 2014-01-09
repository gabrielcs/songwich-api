package models.api;

import org.bson.types.ObjectId;

public interface MongoEntity extends Entity<ObjectId>, MongoModel {
	
	public ObjectId getId();
	
}
