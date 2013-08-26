package database.api;

import com.google.code.morphia.Key;
import com.google.code.morphia.dao.DAO;
import com.mongodb.WriteConcern;

public interface SongwichDAO<T, K> extends DAO<T, K> {
	@Deprecated
	@Override
	public Key<T> save(T entity);
	
	public Key<T> save(T entity, String devEmail);
	
	@Deprecated
	@Override
	public Key<T> save(T entity, WriteConcern wc);
	
	public Key<T> save(T entity, WriteConcern wc, String devEmail);

}
