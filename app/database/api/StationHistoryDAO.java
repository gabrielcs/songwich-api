package database.api;

import java.util.List;

import models.api.StationHistoryEntry;

import com.google.code.morphia.dao.DAO;

public interface StationHistoryDAO<I> extends DAO<StationHistoryEntry, I> {
	
	public StationHistoryEntry findById(I id);

	public List<StationHistoryEntry> findByStationId(I stationId);
}
