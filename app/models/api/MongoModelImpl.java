package models.api;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.code.morphia.annotations.NotSaved;
import com.google.code.morphia.annotations.PostLoad;

public abstract class MongoModelImpl extends BasicModelImpl implements MongoModel {
	@NotSaved
	private boolean modelPersisted = false;
	@NotSaved
	private boolean modelUpdated = false;

	protected MongoModelImpl() {
		super();
	}

	protected void fireModelUpdated() {
		modelUpdated = true;
	}

	@Override
	public Set<MongoModel> getEmbeddedModels() throws IllegalArgumentException,
			IllegalAccessException {
		// HashSet with initial capacity=16 and loadFactor=0.75
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
}
