package models.api.subscriptions;

import models.api.MongoEntity;
import models.api.MongoModelImpl;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;

@Entity
public class Subscription extends MongoModelImpl implements MongoEntity {
	@Id
	private ObjectId id;

	@Indexed
	private ObjectId userId;

	@Indexed
	private ObjectId stationId;

	private Long startDate;

	private Long endDate;

	protected Subscription() {
		super();
	}

	public Subscription(ObjectId userId, ObjectId stationId) {
		this.userId = userId;
		this.stationId = stationId;
		startDate = System.currentTimeMillis();
	}

	public void endSubscription() {
		endDate = System.currentTimeMillis();
		fireModelUpdated();
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
		fireModelUpdated();
	}

	public ObjectId getStationId() {
		return stationId;
	}

	public void setStation(ObjectId stationId) {
		this.stationId = stationId;
		fireModelUpdated();
	}

	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(Long startDate) {
		this.startDate = startDate;
		fireModelUpdated();
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(Long endDate) {
		this.endDate = endDate;
		fireModelUpdated();
	}

	@Override
	public ObjectId getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Subscription [id=" + id + ", userId=" + userId + ", stationId="
				+ stationId + ", startDate=" + startDate + ", endDate="
				+ endDate + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result
				+ ((stationId == null) ? 0 : stationId.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		Subscription other = (Subscription) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (stationId == null) {
			if (other.stationId != null)
				return false;
		} else if (!stationId.equals(other.stationId))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
}
