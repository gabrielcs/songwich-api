package models.api;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import util.api.MyLogger;

import com.google.code.morphia.annotations.NotSaved;
import com.google.code.morphia.annotations.PostLoad;

public abstract class MongoModelImpl extends BasicModelImpl implements
		MongoModel {

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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Set<MongoModel> getEmbeddedModels() {
		// HashSet with initial capacity=16 and loadFactor=0.75
		Set<MongoModel> embeddedModels = new HashSet<MongoModel>();

		// a Set ensures there will be no duplicates
		Set<Field> fields = new HashSet<Field>();
		// non-inherited fields, including private ones
		for (Field field : this.getClass().getDeclaredFields()) {
			fields.add(field);
		}
		// includes non-private inherited fields
		for (Field field : this.getClass().getFields()) {
			fields.add(field);
		}

		Object object;
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				object = field.get(this);
				if (object instanceof MongoModel) {
					embeddedModels.add((MongoModel) object);
				} else if (object instanceof Collection) {
					// check if it's a Collection of MongoModel's
					boolean mongoModelCollection = true;
					for (Object element : (Collection) object) {
						if (!(element instanceof MongoModel)) {
							mongoModelCollection = false;
							break;
						}
					}
					if (mongoModelCollection) {
						embeddedModels
								.addAll((Collection<? extends MongoModel>) object);
					}
				} else if (field.getType().isArray()) {
					// check if it's an array of MongoModel's
					boolean mongoModelArray = true;
					for (Object element : (Object[]) object) {
						if (!(element instanceof MongoModel)) {
							mongoModelArray = false;
							break;
						}
					}
					if (mongoModelArray) {
						for (MongoModel mongoModel : (MongoModel[]) object) {
							embeddedModels.add(mongoModel);
						}
					}
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				MyLogger.warn(e.getMessage());
			}
		}
		return embeddedModels;
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
