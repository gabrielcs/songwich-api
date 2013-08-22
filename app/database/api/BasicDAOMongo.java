package database.api;

import org.bson.types.ObjectId;

import util.api.DatabaseContext;



import com.google.code.morphia.dao.BasicDAO;

public abstract class BasicDAOMongo<T> extends BasicDAO<T, ObjectId> {
	
	protected BasicDAOMongo() {
		super(DatabaseContext.getDatastore());
	}
	
}
