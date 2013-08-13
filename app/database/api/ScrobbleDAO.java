package database.api;

import java.util.List;

import org.bson.types.ObjectId;

import models.api.Scrobble;

import com.google.code.morphia.dao.DAO;

public interface ScrobbleDAO<I> extends DAO<Scrobble, I> {

	public Scrobble findById(I id);

	public List<Scrobble> findByUserId(I userId);
	
	public List<Scrobble> findLastScrobblesByUserId(I userId,
			int results);

	public List<Scrobble> findByUserIdWithDaysOffset(I userId, int daysOffset);

	public List<Scrobble> findLastScrobblesByUserIdWithDaysOffset(
			ObjectId userId, int daysOffset, int results);
}
