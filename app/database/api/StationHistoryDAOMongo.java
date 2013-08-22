package database.api;

import java.util.List;

import models.api.stations.StationHistoryEntry;

import org.bson.types.ObjectId;

import com.google.code.morphia.Key;

import database.api.util.BasicDAOMongo;
import database.api.util.CascadeSaveDAO;

public class StationHistoryDAOMongo extends BasicDAOMongo<StationHistoryEntry>
		implements StationHistoryDAO<ObjectId>,
		CascadeSaveDAO<StationHistoryEntry, ObjectId> {

	public StationHistoryDAOMongo() {
	}

	// TODO: test
	@Override
	public Key<StationHistoryEntry> cascadeSave(StationHistoryEntry t) {
		// nothing to cascade
		return save(t);
	}

	// TODO: test
	@Override
	public StationHistoryEntry findById(ObjectId id) {
		return ds.find(StationHistoryEntry.class).filter("id", id).get();
	}

	// TODO: test
	@Override
	public List<StationHistoryEntry> findByStationId(ObjectId stationId) {
		return ds.find(StationHistoryEntry.class)
				.filter("stationId", stationId).asList();
	}
}
