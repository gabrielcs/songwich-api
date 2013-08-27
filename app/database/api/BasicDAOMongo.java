package database.api;

import java.util.HashSet;
import java.util.Set;

import models.api.MongoEntity;
import models.api.MongoModel;

import org.bson.types.ObjectId;

import util.api.DatabaseContext;
import util.api.MyLogger;

import com.google.code.morphia.Key;
import com.google.code.morphia.dao.BasicDAO;
import com.mongodb.WriteConcern;

public abstract class BasicDAOMongo<T extends MongoEntity> extends
		BasicDAO<T, ObjectId> {

	protected BasicDAOMongo() {
		super(DatabaseContext.getDatastore());
	}

	/**
	 * Use save(T entity, String devEmail) instead.
	 */
	@Override
	@Deprecated
	public Key<T> save(T entity) {
		return super.save(entity);
	}

	/**
	 * It sets createdBy, createdAt, lastUpdatedBy and lastUpdatedAt
	 * accordingly.
	 * 
	 * @param entity
	 * @param devEmail
	 * @return
	 */
	public Key<T> save(T entity, String devEmail) {
		// set createdBy, createdAt, lastUpdatedBy and lastUpdatedAt fields
		Set<MongoModel> entitySet = new HashSet<MongoModel>(1);
		entitySet.add(entity);
		checkModelsForUpdate(entitySet, devEmail);

		// save
		Key<T> key = super.save(entity);

		// check models as persisted
		checkModelsAsPersisted(entitySet);
		return key;
	}

	/**
	 * Use save(T entity, WriteConcern wc, String devEmail) instead.
	 */
	@Override
	@Deprecated
	public Key<T> save(T entity, WriteConcern wc) {
		return super.save(entity, wc);
	}

	/**
	 * It sets createdBy, createdAt, lastUpdatedBy and lastUpdatedAt
	 * accordingly.
	 * 
	 * @param entity
	 * @param wc
	 * @param devEmail
	 * @return
	 */
	public Key<T> save(T entity, WriteConcern wc, String devEmail) {
		// set createdBy, createdAt, lastUpdatedBy and lastUpdatedAt fields
		Set<MongoModel> entitySet = new HashSet<MongoModel>();
		entitySet.add(entity);
		checkModelsForUpdate(entitySet, devEmail);

		// save
		Key<T> key = super.save(entity, wc);

		// check models as persisted
		checkModelsAsPersisted(entitySet);
		return key;
	}

	private void checkModelsForUpdate(Set<MongoModel> models, String devEmail) {
		// protect against null Sets instead of empty ones
		if (models == null) return;

		for (MongoModel model : models) {
			try {
				if (model.isModelPersisted()) {
					if (model.isModelUpdated()) {
						// this is an update
						model.setLastModifiedBy(devEmail);
						model.setLastModifiedAt(System.currentTimeMillis());
						checkModelsForUpdate(model.getEmbeddedModels(),
								devEmail);
					} else {
						// maybe some embedded model was updated
						checkModelsForUpdate(model.getEmbeddedModels(),
								devEmail);
					}
				} else {
					// this is an insert
					Set<MongoModel> modelSet = new HashSet<MongoModel>(1);
					modelSet.add(model);
					checkModelsForInsert(modelSet, devEmail);
				}
			} catch (Exception e) {
				// TODO: further test
				MyLogger.error(e.toString());
			}
		}
	}

	private void checkModelsForInsert(Set<MongoModel> models, String devEmail) {
		// protect against null Sets instead of empty ones
		if (models == null) return;

		for (MongoModel model : models) {
			model.setCreatedBy(devEmail);
			model.setCreatedAt(System.currentTimeMillis());
			try {
				checkModelsForInsert(model.getEmbeddedModels(), devEmail);
			} catch (Exception e) {
				// TODO: further test
				MyLogger.error(e.toString());
			}
		}
	}

	private void checkModelsAsPersisted(Set<MongoModel> models) {
		// protect against null Sets instead of empty ones
		if (models == null) return;

		for (MongoModel model : models) {
			model.setModelPersisted(true);
			try {
				checkModelsAsPersisted(model.getEmbeddedModels());
			} catch (Exception e) {
				// TODO: further test
				MyLogger.error(e.toString());
			}
		}
	}
}
