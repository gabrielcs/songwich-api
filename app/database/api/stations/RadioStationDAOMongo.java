package database.api.stations;

import java.util.List;

import models.api.scrobbles.User;
import models.api.stations.GroupMember;
import models.api.stations.RadioStation;
import models.api.stations.ScrobblerBridge;

import org.bson.types.ObjectId;

import com.google.code.morphia.Key;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryResults;

import database.api.BasicDAOMongo;
import database.api.CascadeSaveDAO;
import database.api.scrobbles.UserDAOMongo;

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
		ScrobblerBridge scrobbler = radioStation.getScrobbler();
		if (scrobbler.isGroupStation()) {
			for (GroupMember groupMember : scrobbler.getGroup()
					.getGroupMembers()) {
				cascadeSaveUser(groupMember.getUser(), devEmail);
			}
		} else if (scrobbler.isIndividualStation()) {
			cascadeSaveUser(scrobbler.getUser(), devEmail);
		}
	}

	private void cascadeSaveUser(User user, String devEmail) {
		CascadeSaveDAO<User, ObjectId> userDao = new UserDAOMongo();
		userDao.cascadeSave(user, devEmail);
	}

	/** Finds all RadioStation's that are not deactivated */
	@Override
	public QueryResults<RadioStation> find() {
		Query<RadioStation> query = ds.find(RadioStation.class);
		filterDeactivated(query);
		return query;
	}

	@Override
	public QueryResults<RadioStation> find(boolean nonDeactivatedOnly) {
		if (nonDeactivatedOnly) {
			return find();
		} else {
			return super.find();
		}
	}

	/** Counts RadioStation's that are not deactivated */
	@Override
	public long count() {
		Query<RadioStation> query = ds.find(RadioStation.class);
		filterDeactivated(query);
		return super.count(query);
	}

	@Override
	public long count(boolean nonDeactivatedOnly) {
		if (nonDeactivatedOnly) {
			return count();
		} else {
			return super.count();
		}
	}

	@Override
	public RadioStation findById(ObjectId id) {
		return queryById(id, true).get();
	}

	@Override
	public RadioStation findById(ObjectId id, boolean nonDeactivatedOnly) {
		return queryById(id, nonDeactivatedOnly).get();
	}

	private Query<RadioStation> queryById(Object id, boolean nonDeactivatedOnly) {
		Query<RadioStation> query = ds.find(RadioStation.class)
				.filter("id", id);
		if (nonDeactivatedOnly) {
			filterDeactivated(query);
		}
		return query;
	}

	@Override
	public List<RadioStation> findByName(String name) {
		return queryByName(name, true).asList();
	}

	@Override
	public List<RadioStation> findByName(String name, boolean nonDeactivatedOnly) {
		return queryByName(name, nonDeactivatedOnly).asList();
	}

	private Query<RadioStation> queryByName(String name,
			boolean nonDeactivatedOnly) {
		Query<RadioStation> query = ds.find(RadioStation.class).filter("name",
				name);
		if (nonDeactivatedOnly) {
			filterDeactivated(query);
		}
		return query;
	}

	@Override
	public List<RadioStation> findByScrobblerId(ObjectId scrobblerId) {
		return queryByScrobblerId(scrobblerId, true).asList();
	}

	@Override
	public List<RadioStation> findByScrobblerId(ObjectId scrobblerId,
			boolean nonDeactivatedOnly) {
		return queryByScrobblerId(scrobblerId, nonDeactivatedOnly).asList();
	}

	private Query<RadioStation> queryByScrobblerId(ObjectId scrobblerId,
			boolean nonDeactivatedOnly) {
		Query<RadioStation> query = ds.find(RadioStation.class).filter(
				"scrobbler.activeScrobblersUserIds", scrobblerId);
		filterDeactivated(query);
		return query;
	}

	private Query<RadioStation> filterDeactivated(Query<RadioStation> query) {
		Boolean deactivated = true;
		//return query.filter("deactivated !=", deactivated);
		return query.field("deactivated").notEqual(deactivated);
	}
}
