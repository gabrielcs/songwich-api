package database.api.scrobbles;

import java.util.List;
import java.util.Set;

import models.api.scrobbles.Scrobble;
import database.api.SongwichDAO;

public interface ScrobbleDAO<I> extends SongwichDAO<Scrobble, I> {

	public Scrobble findById(I id);

	// paging

	public List<Scrobble> findLatestScrobblesByUserId(I userId, int results,
			boolean chosenByUserOnly);

	public List<Scrobble> findScrobblesByUserIdSince(I userId, long since,
			int results, boolean chosenByUserOnly);

	public List<Scrobble> findScrobblesByUserIdUntil(I userId, long until,
			int results, boolean chosenByUserOnly);

	// no paging

	public List<Scrobble> findAllByUserId(I userId, boolean chosenByUserOnly);

	public List<Scrobble> findAllByUserIds(Set<I> userIds,
			boolean chosenByUserOnly);

	// additional methods

	public List<Scrobble> findLatestScrobblesByUserIds(Set<I> userIds,
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
