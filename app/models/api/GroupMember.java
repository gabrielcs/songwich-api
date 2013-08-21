package models.api;

import models.api.util.Model;

import com.google.code.morphia.annotations.Embedded;

@Embedded
public class GroupMember extends Model {
	@Embedded
	private User user;
	
	private Long startDate;
	
	private Long endDate;
	
	protected GroupMember() {
		super();
	}
	
	public GroupMember(User user, Long startDate, String createdBy) {
		super(createdBy);
		this.user = user;
		this.startDate = startDate;
	}

	public GroupMember(User user, Long startDate, Long endDate, String createdBy) {
		super(createdBy);
		this.user = user;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user, String modifiedBy) {
		this.user = user;
		setLastModifiedBy(modifiedBy);
	}

	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate, String modifiedBy) {
		this.startDate = startDate;
		setLastModifiedBy(modifiedBy);
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate, String modifiedBy) {
		this.endDate = endDate;
		setLastModifiedBy(modifiedBy);
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
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
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
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
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
