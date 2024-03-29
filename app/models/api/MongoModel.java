package models.api;

import java.util.Set;

public interface MongoModel extends BasicModel {

	public Set<MongoModel> getEmbeddedModels() throws IllegalArgumentException,
			IllegalAccessException;

	public boolean isModelPersisted();

	public boolean isModelUpdated();

	public void setModelPersisted(boolean isPersisted);
	
}
