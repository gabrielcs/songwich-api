package daos.api;

import java.util.UUID;

import models.MusicService;

import com.google.code.morphia.dao.DAO;

public interface MusicServiceDAO<I> extends DAO<MusicService, I> {
	
	public MusicService findById(I id);

	public MusicService findByAppAuthToken(UUID appAuthToken);

	public MusicService findByName(String name);
}
