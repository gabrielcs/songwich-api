package database.api.stations;

import java.util.List;

import models.api.stations.RadioStation;

import org.bson.types.ObjectId;

import database.api.SongwichDAO;

public interface RadioStationDAO<I> extends SongwichDAO<RadioStation, I> {

	public RadioStation findById(I id);
	
	public RadioStation findById(I id, boolean nonDeactivatedOnly);

	public List<RadioStation> findByName(String name);

	public List<RadioStation> findByScrobblerId(ObjectId scrobblerId);
}
