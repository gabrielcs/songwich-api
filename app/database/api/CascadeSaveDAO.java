package database.api;

import com.google.code.morphia.Key;
import com.google.code.morphia.dao.DAO;

public interface CascadeSaveDAO<T,K> extends DAO<T,K> {
	public Key<T> cascadeSave(T t);
}
