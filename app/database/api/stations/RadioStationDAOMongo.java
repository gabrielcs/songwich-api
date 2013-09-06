package database.api.stations;

import java.util.List;

import models.api.scrobbles.User;
import models.api.stations.Group;
import models.api.stations.GroupMember;
import models.api.stations.RadioStation;
import models.api.stations.Scrobbler;

import org.bson.types.ObjectId;

import com.google.code.morphia.Key;

import database.api.BasicDAOMongo;
import database.api.CascadeSaveDAO;
import database.api.scrobbles.UserDAOMongo;

@SuppressWarnings("rawtypes")
public class RadioStationDAOMongo extends BasicDAOMongo<RadioStation> implements
		RadioStationDAO<ObjectId>, CascadeSaveDAO<RadioStation, ObjectId> {

	public RadioStationDAOMongo() {
	}

	@Override
	public Key<RadioStation> cascadeSave(RadioStation radioStation,
			String devEmail) {
		cascadeSaveScrobbler(radioStation, devEmail);
		return save(radioStation, devEmail);
	}

	private void cascadeSaveScrobbler(RadioStation radioStation, String devEmail) {
		Scrobbler scrobbler = radioStation.getScrobbler();
		if (scrobbler instanceof Group) {
			for (GroupMember groupMember : ((Group) scrobbler).getGroupMembers()) {
				cascadeSaveUser(groupMember.getUser(), devEmail);
			}
		} else if (scrobbler instanceof User) {
			cascadeSaveUser((User) scrobbler, devEmail);
		}
	}

	private void cascadeSaveUser(User user, String devEmail) {
		CascadeSaveDAO<User, ObjectId> userDao = new UserDAOMongo();
		userDao.cascadeSave(user, devEmail);
	}

	@Override
	public RadioStation findById(ObjectId id) {
		return ds.find(RadioStation.class).filter("id", id).get();
	}

	@Override
	public List<RadioStation> findByName(String name) {
		return ds.find(RadioStation.class).filter("name", name).asList();
	}
}
