package daos.api;

import java.util.UUID;

import models.MusicService;

import org.bson.types.ObjectId;

import com.google.code.morphia.Datastore;

import daos.api.util.BasicDAOMongo;
import daos.api.util.CascadeSaveDAO;

public class MusicServiceDAOMongo extends BasicDAOMongo<MusicService> implements
		MusicServiceDAO<ObjectId>, CascadeSaveDAO<MusicService, ObjectId> {

	public MusicServiceDAOMongo(Datastore ds) {
		super(ds);
	}

	// TODO: test
	@Override
	public MusicService findByAppAuthToken(UUID appAuthToken) {
		return ds.find(MusicService.class).filter("appAuthToken", appAuthToken)
				.get();
	}

	@Override
	public MusicService findByName(String name) {
		return ds.find(MusicService.class).filter("name", name).get();
	}

	@Override
	public MusicService findById(ObjectId id) {
		return ds.find(MusicService.class).filter("id", id).get();
	}

	@Override
	public void cascadeSave(MusicService t) {
		// nothing to cascade
		save(t);
	}

}
