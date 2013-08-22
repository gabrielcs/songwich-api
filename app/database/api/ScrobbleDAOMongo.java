package database.api;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import models.api.scrobbles.Scrobble;

import org.bson.types.ObjectId;

import database.api.util.BasicDAOMongo;

/*
 * ScrobbleDAOMongo is not a CascadeSaveDAO.
 * It requires saving its references beforehand. 
 */
public class ScrobbleDAOMongo extends BasicDAOMongo<Scrobble> implements
		ScrobbleDAO<ObjectId> {

	public ScrobbleDAOMongo() {
	}

	@Override
	public Scrobble findById(ObjectId id) {
		return ds.find(Scrobble.class).filter("id", id).get();
	}

	@Override
	public List<Scrobble> findByUserId(ObjectId userId) {
		return ds.find(Scrobble.class).filter("userId", userId)
				.order("-timestamp").asList();
	}

	// TODO: test
	@Override
	public List<Scrobble> findByUserIds(Set<ObjectId> userIds) {
		return ds.find(Scrobble.class).filter("userId in", userIds)
				.order("-timestamp").asList();
	}

	@Override
	public List<Scrobble> findLastScrobblesByUserId(ObjectId userId, int results) {
		return ds.find(Scrobble.class).filter("userId", userId)
				.order("-timestamp").limit(results).asList();
	}

	// TODO: test
	@Override
	public List<Scrobble> findLastScrobblesByUserIds(Set<ObjectId> userIds,
			int results) {
		return ds.find(Scrobble.class).filter("userId in", userIds)
				.order("-timestamp").limit(results).asList();
	}

	@Override
	public List<Scrobble> findByUserIdWithDaysOffset(ObjectId userId,
			int daysOffset) {
		long offsetMillis = calculateOffsetMillis(daysOffset);
		return ds.find(Scrobble.class).filter("userId", userId)
				.filter("timestamp >", offsetMillis).order("-timestamp")
				.asList();
	}

	// TODO: test
	@Override
	public List<Scrobble> findByUserIdsWithDaysOffset(Set<ObjectId> userIds,
			int daysOffset) {
		long offsetMillis = calculateOffsetMillis(daysOffset);
		return ds.find(Scrobble.class).filter("userId in", userIds)
				.filter("timestamp >", offsetMillis).order("-timestamp")
				.asList();
	}

	private long calculateOffsetMillis(int daysOffset) {
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.DATE, -daysOffset);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	@Override
	public List<Scrobble> findLastScrobblesByUserIdWithDaysOffset(
			ObjectId userId, int daysOffset, int results) {
		long offsetMillis = calculateOffsetMillis(daysOffset);
		return ds.find(Scrobble.class).filter("userId", userId)
				.filter("timestamp >", offsetMillis).order("-timestamp")
				.limit(results).asList();
	}

	// TODO: test
	@Override
	public List<Scrobble> findLastScrobblesByUserIdsWithDaysOffset(
			Set<ObjectId> userIds, int daysOffset, int results) {
		long offsetMillis = calculateOffsetMillis(daysOffset);
		return ds.find(Scrobble.class).filter("userId in", userIds)
				.filter("timestamp >", offsetMillis).order("-timestamp")
				.limit(results).asList();
	}
}
