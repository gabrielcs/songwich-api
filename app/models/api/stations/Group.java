package models.api.stations;

import java.util.HashSet;
import java.util.Set;

import models.api.MongoModelImpl;
import models.api.scrobbles.User;

import com.google.code.morphia.annotations.Embedded;

@Embedded
public class Group extends MongoModelImpl {
	
	private String name;

	@Embedded
	private Set<GroupMember> groupMembers = new HashSet<GroupMember>();
	
	protected Group() {
		super();
	}

	public Group(String name, Set<GroupMember> groupMembers) {
		this.name = name;
		this.groupMembers = groupMembers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	public boolean addGroupMember(User user) {
		boolean result = this.groupMembers.add(new GroupMember(user, System.currentTimeMillis()));
		fireModelUpdated();
		return result;
	}
	
	/**
	 * 
	 * @param groupMember
	 * @return <tt>true</tt> (as specified by {@link java.util.Collection#remove})
	 */
	public boolean deactivateGroupMember(User user) {
		for (GroupMember groupMember : groupMembers) {
			if (groupMember.getUser().getId().equals(user.getId())) {
				groupMember.setEndDate(System.currentTimeMillis());
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "Group [name=" + name + ", groupMembers=" + groupMembers + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
