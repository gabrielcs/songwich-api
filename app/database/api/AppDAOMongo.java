package database.api;

import java.util.ArrayList;
import java.util.List;

import models.api.App;
import models.api.AppDeveloper;

import org.bson.types.ObjectId;

import com.google.code.morphia.Key;

import database.api.util.BasicDAOMongo;
import database.api.util.CascadeSaveDAO;

public class AppDAOMongo extends BasicDAOMongo<App> implements
		AppDAO<ObjectId>, CascadeSaveDAO<App, ObjectId> {

	public AppDAOMongo() {
	}

	@Override
	public Key<App> cascadeSave(App t) {
		// nothing to cascade
		return save(t);
	}

	@Override
	public App findByName(String name) {
		return ds.find(App.class).filter("name", name).get();
	}

	@Override
	public App findById(ObjectId id) {
		return ds.find(App.class).filter("id", id).get();
	}

	@Override
	public App findByDevAuthToken(String devAuthToken) {
		App app = ds
				.find(App.class)
				.filter("appDevelopers.statefulDevAuthToken.token",
						devAuthToken).get();
		if (app != null) {
			return app;
		}

		// search for it in the deprecated field
		return ds.find(App.class)
				.filter("appDevelopers.devAuthToken", devAuthToken).get();

	}

	@Override
	public List<App> findByDevEmail(String devEmailAdress) {
		return ds.find(App.class)
				.filter("appDevelopers.emailAddress", devEmailAdress).asList();
	}

	/*
	 * This might be a bit inefficient since it finds in the database and later
	 * in memory.
	 */
	@Override
	public AppDeveloper findAppDevByAuthToken(String devAuthToken) {
		App app = findByDevAuthToken(devAuthToken);
		if (app == null) {
			// the devAuthToken is not in the database
			return null;
		}

		for (AppDeveloper appDeveloper : app.getAppDevelopers()) {
			if (appDeveloper.getDevAuthToken().getToken().equals(devAuthToken)) {
				return appDeveloper;
			}
		}
		// shouldn't reach this point
		return null;
	}

	/*
	 * This might be a bit inefficient since it finds in the database and later
	 * in memory.
	 */
	@Override
	public List<AppDeveloper> findAppDevByEmail(String devEmailAdress) {
		List<App> appList = findByDevEmail(devEmailAdress);

		List<AppDeveloper> appDevelopers = new ArrayList<AppDeveloper>();
		for (App app : appList) {
			for (AppDeveloper appDeveloper : app.getAppDevelopers()) {
				if (appDeveloper.getEmailAddress().equals(devEmailAdress)) {
					appDevelopers.add(appDeveloper);
				}
			}
		}

		return appDevelopers;
	}

}
