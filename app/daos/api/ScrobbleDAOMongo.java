package daos.api;

import java.util.List;

import models.Scrobble;

import org.bson.types.ObjectId;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.dao.BasicDAO;

public class ScrobbleDAOMongo extends BasicDAO<Scrobble, ObjectId> implements
		ScrobbleDAO<ObjectId> {

	public ScrobbleDAOMongo(Datastore ds) {
		super(ds);
	}

	@Override
	public List<Scrobble> findByUserId(ObjectId userId) {
		return ds.find(Scrobble.class).filter("user.id", userId).asList();
	}

	@Override
	public Scrobble findById(ObjectId id) {
		return ds.find(Scrobble.class).filter("id", id).get();
	}
}
