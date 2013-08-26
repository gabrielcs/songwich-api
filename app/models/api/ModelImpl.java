package models.api;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import com.google.code.morphia.annotations.NotSaved;
import com.google.code.morphia.annotations.PostLoad;

public abstract class ModelImpl implements MongoModel {
	private Long createdAt;
	private String createdBy;

	private Long lastModifiedAt;
	private String lastModifiedBy;

	@NotSaved
	private boolean modelPersisted = false;
	@NotSaved
	private boolean modelUpdated = false;

	protected ModelImpl() {
		super();
	}

	protected void fireModelUpdated() {
		modelUpdated = true;
	}

	@Override
	public Set<MongoModel> getEmbeddedModels() throws IllegalArgumentException,
			IllegalAccessException {
		Set<MongoModel> embeddedModels = new HashSet<MongoModel>();
		Field[] fields = this.getClass().getFields();
		for (Field field : fields) {
			if (Arrays.asList(field.getClass().getInterfaces()).contains(
					MongoModel.class)) {
				embeddedModels.add((MongoModel) field.get(this));
			}
		}
		return embeddedModels;
	}

	@Override
	public boolean isModelUpdated() {
		return modelUpdated;
	}

	@Override
	public boolean isModelPersisted() {
		return modelPersisted;
	}

	@Override
	public void setModelPersisted(boolean modelPersisted) {
		this.modelPersisted = modelPersisted;
	}

	@PostLoad
	private void setModelPersisted() {
		this.modelPersisted = true;
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
		ModelImpl other = (ModelImpl) obj;
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
