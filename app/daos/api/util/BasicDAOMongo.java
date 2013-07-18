package daos.api.util;

import org.bson.types.ObjectId;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.dao.BasicDAO;

public abstract class BasicDAOMongo<T> extends BasicDAO<T, ObjectId> {
	protected BasicDAOMongo(Datastore ds) {
		super(ds);
	}
}
