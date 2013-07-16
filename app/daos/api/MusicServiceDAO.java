package daos.api;

import java.util.UUID;

import models.MusicService;
import daos.api.util.DomainAccessObject;

public interface MusicServiceDAO extends DomainAccessObject<MusicService> {

	public MusicService findByAppAuthToken(UUID appAuthToken);

	public MusicService findByName(String name);
}
