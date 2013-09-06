package database.api;

import models.api.MongoEntity;

import com.google.code.morphia.Key;

public interface CascadeSaveDAO<T extends MongoEntity, K> extends SongwichDAO<T, K> {
	
	public Key<T> cascadeSave(T t, String devEmail);
	
}
