package daos.api;

import java.util.List;

import models.Scrobble;

import org.bson.types.ObjectId;

import com.google.code.morphia.Datastore;

import daos.api.util.BasicDAOMongo;

/*
 * ScrobbleDAOMongo is not a CascadeSaveDAO.
 * It requires saving its references beforehand. 
 */
public class ScrobbleDAOMongo extends BasicDAOMongo<Scrobble> implements
		ScrobbleDAO<ObjectId> {

	public ScrobbleDAOMongo(Datastore ds) {
		super(ds);
	}

	@Override
	public Scrobble findById(ObjectId id) {
		return ds.find(Scrobble.class).filter("id", id).get();
	}

	@Override
	public List<Scrobble> findByUserId(ObjectId userId) {
		return ds.find(Scrobble.class).filter("userId", userId).asList();
	}
}
