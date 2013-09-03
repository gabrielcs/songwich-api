package database.api.stations;

import java.util.List;
import java.util.Set;

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

	// TODO: test
	@Override
	public Key<RadioStation> cascadeSave(RadioStation radioStation,
			String devEmail) {
		cascadeSaveScrobbler(radioStation, devEmail);
		return save(radioStation, devEmail);
	}

	private void cascadeSaveScrobbler(RadioStation radioStation, String devEmail) {
		if (radioStation.getScrobbler() == null) {
			// there's nothing to save
			return;
		}

		Scrobbler scrobbler = radioStation.getScrobbler();
		if (scrobbler instanceof Group) {
			cascadeSaveGroup((Group) scrobbler, devEmail);
		} else if (scrobbler instanceof User) {
			cascadeSaveUser((User) scrobbler, devEmail);
		}
	}

	private void cascadeSaveGroup(Group group, String devEmail) {
		Set<GroupMember> groupMembers = group.getGroupMembers();
		for (GroupMember groupMember : groupMembers) {
			cascadeSaveUser(groupMember.getUser(), devEmail);
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

	// TODO: test
	@Override
	public List<RadioStation> findByName(String name) {
		return ds.find(RadioStation.class).filter("name", name).asList();
	}
}
