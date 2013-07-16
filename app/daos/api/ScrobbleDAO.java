package daos.api;

import java.util.List;

import models.Scrobble;

import com.google.code.morphia.dao.DAO;

public interface ScrobbleDAO<I> extends DAO<Scrobble, I> {
	
	public Scrobble findById(I id);
	
	public List<Scrobble> findByUserId(I userId);
}
