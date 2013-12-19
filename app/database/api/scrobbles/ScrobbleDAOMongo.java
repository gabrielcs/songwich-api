package database.api.scrobbles;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import models.api.scrobbles.Scrobble;

import org.bson.types.ObjectId;

import com.google.code.morphia.query.Query;

import database.api.BasicDAOMongo;

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

	// pagination

	@Override
	public List<Scrobble> findLatestScrobblesByUserId(ObjectId userId,
			int results, boolean chosenByUserOnly) {

		Query<Scrobble> query = queryByUserId(userId);
		filterChosenByUserOnly(query, chosenByUserOnly);
		return order(query).limit(results).asList();
	}

	@Override
	public List<Scrobble> findScrobblesByUserIdSince(ObjectId userId,
			long since, int results, boolean chosenByUserOnly) {

		Query<Scrobble> query = queryByUserId(userId);
		filterTimestampSince(query, since);
		filterChosenByUserOnly(query, chosenByUserOnly);
		return order(query).limit(results).asList();
	}

	@Override
	public List<Scrobble> findScrobblesByUserIdUntil(ObjectId userId,
			long until, int results, boolean chosenByUserOnly) {
		Query<Scrobble> query = queryByUserId(userId);
		filterTimestampUntil(query, until);
		filterChosenByUserOnly(query, chosenByUserOnly);
		return order(query).limit(results).asList();
	}

	// no pagination

	@Override
	public List<Scrobble> findAllByUserId(ObjectId userId,
			boolean chosenByUserOnly) {
		Query<Scrobble> query = queryByUserId(userId);
		filterChosenByUserOnly(query, chosenByUserOnly);
		return order(query).asList();
	}

	@Override
	public List<Scrobble> findAllByUserIds(Set<ObjectId> userIds,
			boolean chosenByUserOnly) {
		Query<Scrobble> query = queryByUserIds(userIds);
		filterChosenByUserOnly(query, chosenByUserOnly);
		return order(query).asList();
	}

	// additional methods

	@Override
	public List<Scrobble> findLatestScrobblesByUserIds(Set<ObjectId> userIds,
			int results, boolean chosenByUserOnly) {
		Query<Scrobble> query = queryByUserIds(userIds);
		filterChosenByUserOnly(query, chosenByUserOnly);
		return order(query).limit(results).asList();
	}

	@Override
	public List<Scrobble> findByUserIdWithDaysOffset(ObjectId userId,
			int daysOffset, boolean chosenByUserOnly) {
		Query<Scrobble> query = queryByUserId(userId);
		filterDaysOffset(query, daysOffset);
		filterChosenByUserOnly(query, chosenByUserOnly);
		return order(query).asList();
	}

	@Override
	public List<Scrobble> findByUserIdsWithDaysOffset(Set<ObjectId> userIds,
			int daysOffset, boolean chosenByUserOnly) {
		Query<Scrobble> query = queryByUserIds(userIds);
		filterDaysOffset(query, daysOffset);
		filterChosenByUserOnly(query, chosenByUserOnly);
		return order(query).asList();
	}

	@Override
	public List<Scrobble> findLastScrobblesByUserIdWithDaysOffset(
			ObjectId userId, int daysOffset, int results,
			boolean chosenByUserOnly) {
		Query<Scrobble> query = queryByUserId(userId);
		filterDaysOffset(query, daysOffset);
		filterChosenByUserOnly(query, chosenByUserOnly);
		return order(query).limit(results).asList();
	}

	@Override
	public List<Scrobble> findLastScrobblesByUserIdsWithDaysOffset(
			Set<ObjectId> userIds, int daysOffset, int results,
			boolean chosenByUserOnly) {
		Query<Scrobble> query = queryByUserIds(userIds);
		filterDaysOffset(query, daysOffset);
		filterChosenByUserOnly(query, chosenByUserOnly);
		return order(query).limit(results).asList();
	}

	// private queries, filters and sorters

	private Query<Scrobble> queryByUserId(ObjectId userId) {
		return ds.find(Scrobble.class).filter("userId", userId);
	}

	private Query<Scrobble> queryByUserIds(Set<ObjectId> userIds) {
		// return ds.find(Scrobble.class).filter("userId in", userIds);
		return ds.find(Scrobble.class).field("userId").hasAnyOf(userIds);
	}

	private Query<Scrobble> filterChosenByUserOnly(Query<Scrobble> query,
			boolean chosenByUserOnly) {
		if (!chosenByUserOnly) {
			return query;
		}
		return query.filter("chosenByUser", true);
	}

	private Query<Scrobble> filterTimestampUntil(Query<Scrobble> query, long until) {
		return query.field("timestamp").lessThan(until);
	}

	private Query<Scrobble> filterTimestampSince(Query<Scrobble> query, long since) {
		return query.field("timestamp").greaterThan(since);
	}

	/*
	 * Order from the newest to the oldest.
	 */
	private Query<Scrobble> order(Query<Scrobble> query) {
		return query.order("-timestamp");
	}

	private Query<Scrobble> filterDaysOffset(Query<Scrobble> query,
			int daysOffset) {
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.DATE, -daysOffset);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long daysOffsetMillis = calendar.getTimeInMillis();
		// return query.filter("timestamp >", daysOffsetMillis);
		return query.field("timestamp").greaterThan(daysOffsetMillis);
	}
}
