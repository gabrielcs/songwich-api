package models.api.stations;

import java.util.HashSet;
import java.util.Set;

import models.api.scrobbles.User;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.PrePersist;
import com.google.code.morphia.annotations.Reference;

@Embedded
public class ScrobblerBridge {

	boolean groupStation = false;

	@Indexed
	private Set<ObjectId> activeScrobblersUserIds = new HashSet<ObjectId>();

	@Embedded
	private Group group;

	@Reference(lazy = true)
	private User user;

	protected ScrobblerBridge() {
	}

	public ScrobblerBridge(Group group) {
		this.group = group;
		groupStation = true;
	}

	public ScrobblerBridge(User user) {
		activeScrobblersUserIds.add(user.getId());
		this.user = user;
		groupStation = false;
	}

	/*
	 * It should be called only after Group/User has been saved, otherwise it
	 * would have null ID's.
	 */
	@PrePersist
	public void loadActiveScrobblersUserIds() {
		activeScrobblersUserIds.clear();

		if (groupStation) {
			for (GroupMember groupMember : group.getGroupMembers()) {
				if (groupMember.getEndDate() == null) {
					ObjectId userId = groupMember.getUser().getId();
					if (userId != null) {
						activeScrobblersUserIds.add(userId);
					}
				}
			}
		} else {
			// individual station
			if (user.getId() != null) {
				activeScrobblersUserIds.add(user.getId());
			}
		}
	}

	public Set<ObjectId> getActiveScrobblersUserIds() {
		loadActiveScrobblersUserIds();
		return activeScrobblersUserIds;
	}

	public boolean isGroupStation() {
		return groupStation;
	}

	public boolean isIndividualStation() {
		return !groupStation;
	}

	public Group getGroup() {
		return group;
	}

	public User getUser() {
		return user;
	}

	public void setGroup(Group group) {
		if (isGroupStation()) {
			this.group = group;
		} else {
			throw new IllegalStateException(
					"Cannot call setGroup() for an Individual Station");
		}

	}

	public void setUser(User user) {
		if (isIndividualStation()) {
			this.user = user;
		} else {
			throw new IllegalStateException(
					"Cannot call setGroup() for a Individual Station");
		}
	}

	@Override
	public String toString() {
		return "ScrobblerBridge [groupStation=" + groupStation
				+ ", activeScrobblersUserIds=" + activeScrobblersUserIds
				+ ", group=" + group + ", user=" + user + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + (groupStation ? 1231 : 1237);
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScrobblerBridge other = (ScrobblerBridge) obj;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		if (groupStation != other.groupStation)
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

}
