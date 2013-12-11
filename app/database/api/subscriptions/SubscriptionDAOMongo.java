package database.api.subscriptions;

import java.util.List;

import models.api.subscriptions.Subscription;

import org.bson.types.ObjectId;

import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryResults;

import database.api.BasicDAOMongo;

public class SubscriptionDAOMongo extends BasicDAOMongo<Subscription> implements
		SubscriptionDAO<ObjectId> {

	public SubscriptionDAOMongo() {
	}

	@Override
	public QueryResults<Subscription> find() {
		Query<Subscription> query = ds.find(Subscription.class);
		filterExpired(query);
		return query;
	}

	@Override
	public QueryResults<Subscription> find(boolean activeOnly) {
		if (activeOnly) {
			return find();
		} else {
			return super.find();
		}
	}

	@Override
	public long count() {
		Query<Subscription> query = ds.find(Subscription.class);
		filterExpired(query);
		return super.count(query);
	}

	@Override
	public long count(boolean activeOnly) {
		if (activeOnly) {
			return count();
		} else {
			return super.count();
		}
	}

	@Override
	public Subscription findById(ObjectId id) {
		return ds.find(Subscription.class).filter("id", id).get();
	}

	@Override
	public List<Subscription> findByUserId(ObjectId userId) {
		return queryByUserId(userId, true).asList();
	}

	@Override
	public List<Subscription> findByUserId(ObjectId userId, boolean activeOnly) {
		return queryByUserId(userId, activeOnly).asList();
	}

	private Query<Subscription> queryByUserId(ObjectId userId,
			boolean activeOnly) {
		Query<Subscription> query = ds.find(Subscription.class).filter(
				"userId", userId);
		if (activeOnly) {
			filterExpired(query);
		}
		return query;
	}

	@Override
	public List<Subscription> findByStationId(ObjectId stationId) {
		return queryByStationId(stationId, true).asList();
	}

	@Override
	public List<Subscription> findByStationId(ObjectId stationId,
			boolean activeOnly) {
		return queryByStationId(stationId, activeOnly).asList();
	}

	private Query<Subscription> queryByStationId(ObjectId stationId,
			boolean activeOnly) {
		Query<Subscription> query = ds.find(Subscription.class).filter(
				"stationId", stationId);
		if (activeOnly) {
			filterExpired(query);
		}
		return query;
	}
	
	@Override
	public long countByStationId(ObjectId stationId) {
		return queryByStationId(stationId, true).countAll();
	}

	@Override
	public long countByStationId(ObjectId stationId, boolean activeOnly) {
		return queryByStationId(stationId, activeOnly).countAll();
	}

	private Query<Subscription> filterExpired(Query<Subscription> query) {
		return query.field("endDate").doesNotExist();
	}
}
