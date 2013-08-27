package models.api;

import java.util.GregorianCalendar;

public abstract class BasicModelImpl implements MongoModel {
	private Long createdAt;
	private String createdBy;

	private Long lastModifiedAt;
	private String lastModifiedBy;

	protected BasicModelImpl() {
		super();
	}

	@Override
	public long getCreatedAt() {
		return createdAt;
	}

	@Override
	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public void setCreatedAt(GregorianCalendar createdAt) {
		this.createdAt = createdAt.getTimeInMillis();
	}

	@Override
	public String getCreatedBy() {
		return createdBy;
	}

	@Override
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public long getLastModifiedAt() {
		return lastModifiedAt;
	}

	@Override
	public void setLastModifiedAt(long lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}

	@Override
	public void setLastModifiedAt(GregorianCalendar lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt.getTimeInMillis();
	}

	@Override
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	/**
	 * It also calls setLastModifiedAt(). If getId() returns null it doesn't do
	 * anything (the object is not yet in the database).
	 * 
	 * @param lastModifiedBy
	 */
	@Override
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
		setLastModifiedAt(System.currentTimeMillis());
	}

	@Override
	public String toString() {
		return "Model [createdAt=" + createdAt + ", createdBy=" + createdBy
				+ ", lastModifiedAt=" + lastModifiedAt + ", lastModifiedBy="
				+ lastModifiedBy + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((createdAt == null) ? 0 : createdAt.hashCode());
		result = prime * result
				+ ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result
				+ ((lastModifiedAt == null) ? 0 : lastModifiedAt.hashCode());
		result = prime * result
				+ ((lastModifiedBy == null) ? 0 : lastModifiedBy.hashCode());
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
		BasicModelImpl other = (BasicModelImpl) obj;
		if (createdAt == null) {
			if (other.createdAt != null)
				return false;
		} else if (!createdAt.equals(other.createdAt))
			return false;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (lastModifiedAt == null) {
			if (other.lastModifiedAt != null)
				return false;
		} else if (!lastModifiedAt.equals(other.lastModifiedAt))
			return false;
		if (lastModifiedBy == null) {
			if (other.lastModifiedBy != null)
				return false;
		} else if (!lastModifiedBy.equals(other.lastModifiedBy))
			return false;
		return true;
	}
}
