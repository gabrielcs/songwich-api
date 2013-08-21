package database.api;

import java.util.List;
import java.util.Set;

import models.api.Group;
import models.api.GroupMember;
import models.api.RadioStation;
import models.api.Scrobbler;
import models.api.User;

import org.bson.types.ObjectId;

import com.google.code.morphia.Key;

import database.api.util.BasicDAOMongo;
import database.api.util.CascadeSaveDAO;

@SuppressWarnings("rawtypes")
public class RadioStationDAOMongo extends BasicDAOMongo<RadioStation> implements
		RadioStationDAO<ObjectId>, CascadeSaveDAO<RadioStation, ObjectId> {

	public RadioStationDAOMongo() {
	}

	// TODO: test
	@Override
	public Key<RadioStation> cascadeSave(RadioStation radioStation) {
		cascadeSaveScrobbler(radioStation);
		return save(radioStation);
	}

	private void cascadeSaveScrobbler(RadioStation radioStation) {
		if (radioStation.getScrobbler() == null) {
			// there's nothing to save
			return;
		}

		Scrobbler scrobbler = radioStation.getScrobbler();
		if (scrobbler instanceof Group) {
			cascadeSaveGroup((Group) scrobbler);
		} else if (scrobbler instanceof User) {
			cascadeSaveUser((User) scrobbler);
		}
	}
	
	private void cascadeSaveGroup(Group group) {
		Set<GroupMember> groupMembers = group.getGroupMembers();
		for (GroupMember groupMember : groupMembers) {
			cascadeSaveUser(groupMember.getUser());
		}
	}

	private void cascadeSaveUser(User user) {
		CascadeSaveDAO<User, ObjectId> userDao = new UserDAOMongo();
		userDao.cascadeSave(user);
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
