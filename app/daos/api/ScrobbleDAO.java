package daos.api;

import java.util.List;

import models.Scrobble;
import daos.api.util.DatabaseId;
import daos.api.util.DomainAccessObject;

public interface ScrobbleDAO extends DomainAccessObject<Scrobble> {

	public void save(Scrobble scrobble);
	
	public List<Scrobble> findByUserId(DatabaseId userId);
}
