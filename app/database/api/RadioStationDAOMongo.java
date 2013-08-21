package database.api;

import java.util.List;

import models.api.RadioStation;

import org.bson.types.ObjectId;

import database.api.util.BasicDAOMongo;

@SuppressWarnings("rawtypes")
public class RadioStationDAOMongo extends BasicDAOMongo<RadioStation> implements
		RadioStationDAO<ObjectId> {

	public RadioStationDAOMongo() {
	}

	// TODO: test
	@Override
	public RadioStation findById(ObjectId id) {
		return ds.find(RadioStation.class).filter("id", id).get();
	}

	// TODO: test
	@Override
	public List<RadioStation> findByName(String name) {
		return ds.find(RadioStation.class).filter("name", name).asList();
	}
}
