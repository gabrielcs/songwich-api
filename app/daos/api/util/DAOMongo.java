package daos.api.util;

import org.bson.types.ObjectId;

public abstract class DAOMongo<T> implements DomainAccessObject<T> {

	public abstract T findById(ObjectId id);
	
}
