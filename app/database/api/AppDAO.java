package database.api;

import java.util.List;

import models.api.App;
import models.api.AppDeveloper;

import com.google.code.morphia.dao.DAO;

public interface AppDAO<I> extends DAO<App, I> {
	
	public App findById(I id);

	public App findByName(String name);
	
	public App findByDevAuthToken(String devAuthToken);
	
	public AppDeveloper findAppDevByAuthToken(String devAuthToken);

	public List<App> findByDevEmail(String devEmailAdress);
	
	public List<AppDeveloper> findAppDevByEmail(String devEmailAdress);
}