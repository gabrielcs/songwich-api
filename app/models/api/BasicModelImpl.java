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

	/*
	 * No immutable properties.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return 1;
	}

	/*
	 * No immutable properties.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public boolean equals(Object obj) {
		return true;
	}
	
}
