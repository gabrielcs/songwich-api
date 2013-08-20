package database.api;

import java.util.List;
import java.util.Set;

import models.api.Scrobble;

import com.google.code.morphia.dao.DAO;

public interface ScrobbleDAO<I> extends DAO<Scrobble, I> {

	public Scrobble findById(I id);

	public List<Scrobble> findByUserId(I userId);

	public List<Scrobble> findByUserIds(Set<I> userIds);

	public List<Scrobble> findLastScrobblesByUserId(I userId, int results);

	public List<Scrobble> findLastScrobblesByUserIds(Set<I> userIds, int results);

	public List<Scrobble> findByUserIdWithDaysOffset(I userId, int daysOffset);

	public List<Scrobble> findByUserIdsWithDaysOffset(Set<I> userId,
			int daysOffset);

	public List<Scrobble> findLastScrobblesByUserIdWithDaysOffset(I userId,
			int daysOffset, int results);

	public List<Scrobble> findLastScrobblesByUserIdsWithDaysOffset(
			Set<I> userIds, int daysOffset, int results);
}
