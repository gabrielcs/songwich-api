package database.api.stations;

import java.util.List;

import models.api.stations.RadioStation;

import org.bson.types.ObjectId;

import com.google.code.morphia.query.QueryResults;

import database.api.SongwichDAO;

public interface RadioStationDAO<I> extends SongwichDAO<RadioStation, I> {

	/** Filters deactivated RadioStation's */
	public QueryResults<RadioStation> find();

	public QueryResults<RadioStation> find(boolean nonDeactivatedOnly);

	/** Filters deactivated RadioStation's */
	public long count();

	public long count(boolean nonDeactivatedOnly);

	/** Filters deactivated RadioStation's */
	public RadioStation findById(I id);

	public RadioStation findById(I id, boolean nonDeactivatedOnly);

	/** Filters deactivated RadioStation's */
	public List<RadioStation> findByName(String name);

	public List<RadioStation> findByName(String name, boolean nonDeactivatedOnly);

	/** Filters deactivated RadioStation's */
	public List<RadioStation> findByScrobblerId(ObjectId scrobblerId);

	public List<RadioStation> findByScrobblerId(ObjectId scrobblerId,
			boolean nonDeactivatedOnly);
}
