package daos.api.util;

import org.bson.types.ObjectId;

import usecases.api.util.DatabaseContext;

import com.google.code.morphia.dao.BasicDAO;

public abstract class BasicDAOMongo<T> extends BasicDAO<T, ObjectId> {
	
	protected BasicDAOMongo() {
		super(DatabaseContext.getDatastore());
	}
	
}
