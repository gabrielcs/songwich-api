package models.api.stations;

import models.api.MongoModel;
import models.api.MongoModelImpl;
import models.api.scrobbles.User;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Reference;

@Embedded
public class GroupMember extends MongoModelImpl implements MongoModel {
	
	// TODO: check what happens if we do ignoreMissing=true
	@Reference(lazy = true)
	private User user;

	private Long startDate;

	private Long endDate;

	protected GroupMember() {
		super();
	}

	public GroupMember(User user, Long startDate) {
		this.user = user;
		this.startDate = startDate;
	}

	public GroupMember(User user, Long startDate, Long endDate) {
		this.user = user;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
		fireModelUpdated();
	}

	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
		fireModelUpdated();
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
		fireModelUpdated();
	}

	@Override
	public String toString() {
		return "GroupMember [user=" + user + ", startDate=" + startDate
				+ ", endDate=" + endDate + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		GroupMember other = (GroupMember) obj;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

}
