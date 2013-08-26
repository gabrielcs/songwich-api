package database.api.stations;

import java.util.List;

import models.api.stations.RadioStation;
import database.api.SongwichDAO;

@SuppressWarnings("rawtypes")
public interface RadioStationDAO<I> extends SongwichDAO<RadioStation, I> {
	
	public RadioStation findById(I id);

	public List<RadioStation> findByName(String name);
}
