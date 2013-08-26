package database.api;

import models.api.MongoEntity;

import com.google.code.morphia.Key;
import com.google.code.morphia.dao.DAO;

public interface CascadeSaveDAO<T extends MongoEntity, K> extends DAO<T, K> {
	public Key<T> cascadeSave(T t, String devEmail);
}
