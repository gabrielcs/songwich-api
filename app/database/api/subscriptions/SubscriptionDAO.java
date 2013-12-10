package database.api.subscriptions;

import java.util.List;

import models.api.subscriptions.Subscription;

import org.bson.types.ObjectId;

import com.google.code.morphia.query.QueryResults;

import database.api.SongwichDAO;

public interface SubscriptionDAO<I> extends SongwichDAO<Subscription, I> {

	/** Filters expired Subscription's */
	public QueryResults<Subscription> find();

	public QueryResults<Subscription> find(boolean activeOnly);

	/** Filters expired Subscription's */
	public long count();

	public long count(boolean activeOnly);

	public Subscription findById(I id);

	/** Filters expired Subscription's */
	public List<Subscription> findByStationId(ObjectId stationId);
	
	public List<Subscription> findByStationId(ObjectId stationId, boolean activeOnly);

	/** Filters expired Subscription's */
	public List<Subscription> findByUserId(ObjectId stationId);
	
	public List<Subscription> findByUserId(ObjectId stationId, boolean activeOnly);
}
