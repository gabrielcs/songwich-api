package models.api;

import org.bson.types.ObjectId;

public interface MongoEntity extends MongoModel {
	
	public ObjectId getId();
	
}
