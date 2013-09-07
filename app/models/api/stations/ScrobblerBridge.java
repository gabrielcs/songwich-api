package models.api.stations;

import java.util.HashSet;
import java.util.Set;

import models.api.scrobbles.User;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.PrePersist;

@Embedded
public class ScrobblerBridge {

	boolean groupScrobbler, userScrobbler = false;

	@Indexed
	private Set<ObjectId> activeScrobblersUserIds = new HashSet<ObjectId>();

	@Embedded
	private Group group;

	@Embedded
	private User user;

	protected ScrobblerBridge() {
	}

	public ScrobblerBridge(Group group) {
		this.group = group;
		groupScrobbler = true;
	}

	public ScrobblerBridge(User user) {
		activeScrobblersUserIds.add(user.getId());
		this.user = user;
		userScrobbler = true;
	}

	/*
	 * It should be called only after Group/User has been saved, otherwise it
	 * would have null ID's.
	 */
	@PrePersist
	private void loadActiveScrobblersUserIds() {
		activeScrobblersUserIds.clear();

		if (groupScrobbler) {
			for (GroupMember groupMember : group.getGroupMembers()) {
				if (groupMember.getEndDate() == null) {
					ObjectId userId = groupMember.getUser().getId();
					if (userId != null) {
						activeScrobblersUserIds.add(userId);
					}
				}
			}
		} else if (userScrobbler) {
			if (user.getId() != null) {
				activeScrobblersUserIds.add(user.getId());
			}
		}
	}

	public Set<ObjectId> getActiveScrobblersUserIds() {
		loadActiveScrobblersUserIds();
		return activeScrobblersUserIds;
	}

	public boolean isGroupScrobbler() {
		return groupScrobbler;
	}

	public boolean isUserScrobbler() {
		return userScrobbler;
	}

	public Group getGroup() {
		return group;
	}

	public User getUser() {
		return user;
	}

	public void setGroup(Group group) {
		if (isGroupScrobbler()) {
			this.group = group;
		} else {
			throw new IllegalStateException(
					"Cannot call setGroup() for a User Scrobbler");
		}

	}

	public void setUser(User user) {
		if (isUserScrobbler()) {
			this.user = user;
		} else {
			throw new IllegalStateException(
					"Cannot call setGroup() for a User Scrobbler");
		}
	}

	@Override
	public String toString() {
		return "ScrobblerBridge [groupScrobbler=" + groupScrobbler
				+ ", userScrobbler=" + userScrobbler
				+ ", activeScrobblersUserIds=" + activeScrobblersUserIds
				+ ", group=" + group + ", user=" + user + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + (groupScrobbler ? 1231 : 1237);
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result + (userScrobbler ? 1231 : 1237);
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
		if (groupScrobbler != other.groupScrobbler)
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (userScrobbler != other.userScrobbler)
			return false;
		return true;
	}
}
