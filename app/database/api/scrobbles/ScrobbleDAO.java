package database.api.scrobbles;

import java.util.List;
import java.util.Set;

import models.api.scrobbles.Scrobble;

import com.google.code.morphia.dao.DAO;

public interface ScrobbleDAO<I> extends DAO<Scrobble, I> {

	public Scrobble findById(I id);

	public List<Scrobble> findByUserId(I userId, boolean chosenByUserOnly);

	public List<Scrobble> findByUserIds(Set<I> userIds, boolean chosenByUserOnly);

	public List<Scrobble> findLastScrobblesByUserId(I userId, int results,
			boolean chosenByUserOnly);

	public List<Scrobble> findLastScrobblesByUserIds(Set<I> userIds,
			int results, boolean chosenByUserOnly);

	public List<Scrobble> findByUserIdWithDaysOffset(I userId, int daysOffset,
			boolean chosenByUserOnly);

	public List<Scrobble> findByUserIdsWithDaysOffset(Set<I> userId,
			int daysOffset, boolean chosenByUserOnly);

	public List<Scrobble> findLastScrobblesByUserIdWithDaysOffset(I userId,
			int daysOffset, int results, boolean chosenByUserOnly);

	public List<Scrobble> findLastScrobblesByUserIdsWithDaysOffset(
			Set<I> userIds, int daysOffset, int results,
			boolean chosenByUserOnly);
}
