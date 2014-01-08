package database.api.scrobbles;

import java.util.Calendar;
import java.util.Collections;
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

	// paging

	@Override
	public List<Scrobble> findLatestScrobblesByUserId(ObjectId userId,
			int results, boolean chosenByUserOnly) {

		Query<Scrobble> query = queryByUserId(userId);
		filterChosenByUserOnly(query, chosenByUserOnly);
		return order(query).limit(results).asList();
	}

	// not including 'since'
	@Override
	public List<Scrobble> findScrobblesByUserIdSince(ObjectId userId,
			long sinceTimestamp, ObjectId sinceObjectId, boolean inclusive,
			int results, boolean chosenByUserOnly) {

		Query<Scrobble> query = queryByUserId(userId);
		filterSince(query, sinceTimestamp, sinceObjectId, inclusive);
		filterChosenByUserOnly(query, chosenByUserOnly);
		// gets the oldest scrobbles from the selected bunch and limit results
		query = orderReverse(query).limit(results);

		// order by newest scrobbles
		List<Scrobble> result = query.asList();
		Collections.reverse(result);
		return result;
	}

	@Override
	public List<Scrobble> findScrobblesByUserIdUntil(ObjectId userId,
			long untilTimestamp, ObjectId untilObjectId, boolean inclusive,
			int results, boolean chosenByUserOnly) {
		Query<Scrobble> query = queryByUserId(userId);
		filterUntil(query, untilTimestamp, untilObjectId, inclusive);
		filterChosenByUserOnly(query, chosenByUserOnly);
		return order(query).limit(results).asList();
	}

	// no paging

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

	// if 'timestamp' is the same then it compares 'id'
	private Query<Scrobble> filterUntil(Query<Scrobble> query, long until,
			ObjectId untilObjectId, boolean inclusive) {

		if (inclusive) {
			query.or(query.criteria("timestamp").lessThan(until), query.and(
					query.criteria("timestamp").equal(until), query.criteria("id")
					.lessThanOrEq(untilObjectId)));
		} else {
			query.or(query.criteria("timestamp").lessThan(until), query.and(
					query.criteria("timestamp").equal(until), query.criteria("id")
					.lessThan(untilObjectId)));
		}
		return query;
	}

	// if 'timestamp' is the same then it compares 'id'
	private Query<Scrobble> filterSince(Query<Scrobble> query, long since,
			ObjectId sinceObjectId, boolean inclusive) {
		
		if (inclusive) {
			query.or(query.criteria("timestamp").greaterThan(since), query.and(
					query.criteria("timestamp").equal(since), query.criteria("id")
					.greaterThanOrEq(sinceObjectId)));
		} else {
			query.or(query.criteria("timestamp").greaterThan(since), query.and(
					query.criteria("timestamp").equal(since), query.criteria("id")
					.greaterThan(sinceObjectId)));
		}
		return query;
	}

	/*
	 * Order from the newest to the oldest.
	 */
	private Query<Scrobble> order(Query<Scrobble> query) {
		// orders by id in case they have the same timestamp
		return query.order("-timestamp, -id");
	}

	/*
	 * Order from the oldest to the newest.
	 */
	private Query<Scrobble> orderReverse(Query<Scrobble> query) {
		// orders by id in case they have the same timestamp
		return query.order("timestamp, id");
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
