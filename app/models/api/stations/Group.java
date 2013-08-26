package models.api.stations;

import java.util.HashSet;
import java.util.Set;

import models.api.ModelImpl;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;

@Embedded
public class Group extends ModelImpl implements Scrobbler {
	@Embedded
	private Set<GroupMember> groupMembers = new HashSet<GroupMember>();

	protected Group() {
		super();
	}

	public Group(Set<GroupMember> groupMembers) {
		this.groupMembers = groupMembers;
	}

	public Set<GroupMember> getGroupMembers() {
		return groupMembers;
	}

	public void setGroupMembers(Set<GroupMember> groupMembers) {
		this.groupMembers = groupMembers;
		fireModelUpdated();
	}

	/**
	 * 
	 * @param groupMember
	 * @return <tt>true</tt> (as specified by {@link java.util.Collection#add})
	 */
	public boolean addGroupMember(GroupMember groupMember) {
		boolean result = this.groupMembers.add(groupMember);
		fireModelUpdated();
		return result;
	}

	@Override
	public Set<ObjectId> getActiveScrobblersUserIds() {
		Set<ObjectId> userIds = new HashSet<ObjectId>();
		for (GroupMember groupMember : groupMembers) {
			userIds.add(groupMember.getUser().getId());
		}
		return userIds;
	}

	@Override
	public String toString() {
		return "Group [groupMembers=" + groupMembers + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((groupMembers == null) ? 0 : groupMembers.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Group other = (Group) obj;
		if (groupMembers == null) {
			if (other.groupMembers != null)
				return false;
		} else if (!groupMembers.equals(other.groupMembers))
			return false;
		return true;
	}
}
