package database.api;

import java.util.List;

import models.api.RadioStation;

import com.google.code.morphia.dao.DAO;

@SuppressWarnings("rawtypes")
public interface RadioStationDAO<I> extends DAO<RadioStation, I> {
	
	public RadioStation findById(I id);

	public List<RadioStation> findByName(String name);
}
