package daos.api;

import java.util.UUID;

import models.App;
import models.AppDeveloper;

import org.bson.types.ObjectId;

import com.google.code.morphia.Datastore;

import daos.api.util.BasicDAOMongo;
import daos.api.util.CascadeSaveDAO;

public class AppDAOMongo extends BasicDAOMongo<App> implements
		AppDAO<ObjectId>, CascadeSaveDAO<App, ObjectId> {

	public AppDAOMongo(Datastore ds) {
		super(ds);
	}

	@Override
	public void cascadeSave(App t) {
		// nothing to cascade
		save(t);
	}

	@Override
	public App findByName(String name) {
		return ds.find(App.class).filter("name", name).get();
	}

	@Override
	public App findById(ObjectId id) {
		return ds.find(App.class).filter("id", id).get();
	}

	// TODO: test
	@Override
	public App findByDevAuthToken(UUID appAuthToken) {
		return ds.find(App.class)
				.filter("appDevelopers.devAuthToken", appAuthToken).get();
	}

	// TODO: test
	@Override
	public App findByDevEmail(String devEmailAdress) {
		return ds.find(App.class)
				.filter("appDevelopers.emailAdress", devEmailAdress).get();
	}

	/*
	 * This might be a bit inefficient since it finds in the database and later 
	 * in memory.
	 * 
	 * TODO: test
	 */
	@Override
	public AppDeveloper findAppDevByAuthToken(UUID devAuthToken) {
		App app = findByDevAuthToken(devAuthToken);

		for (AppDeveloper appDeveloper : app.getAppDevelopers()) {
			if (appDeveloper.getDevAuthToken().equals(devAuthToken)) {
				return appDeveloper;
			}
		}
		// shouldn't reach this point
		return null;
	}

	/*
	 * This might be a bit inefficient since it finds in the database and later 
	 * in memory.
	 * 
	 * TODO: test
	 */
	@Override
	public AppDeveloper findAppDevByEmail(String devEmailAdress) {
		App app = findByDevEmail(devEmailAdress);

		for (AppDeveloper appDeveloper : app.getAppDevelopers()) {
			if (appDeveloper.getEmailAddress().equals(devEmailAdress)) {
				return appDeveloper;
			}
		}
		// shouldn't reach this point
		return null;
	}

}
