package daos.api;

import java.util.UUID;

import models.MusicService;

import org.bson.types.ObjectId;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.dao.BasicDAO;

public class MusicServiceDAOMongo extends BasicDAO<MusicService, ObjectId> implements
		MusicServiceDAO<ObjectId> {

	public MusicServiceDAOMongo(Datastore ds) {
		super(ds);
	}

	@Override
	public MusicService findByAppAuthToken(UUID appAuthToken) {
		return ds.find(MusicService.class).filter("appAuthToken", appAuthToken).get();
	}

	@Override
	public MusicService findByName(String name) {
		return ds.find(MusicService.class).filter("name", name).get();
	}

	@Override
	public MusicService findById(ObjectId id) {
		return ds.find(MusicService.class).filter("id", id).get();
	}
}
