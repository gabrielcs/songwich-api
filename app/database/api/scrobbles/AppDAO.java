package database.api.scrobbles;

import java.util.List;

import models.api.scrobbles.App;
import models.api.scrobbles.AppDeveloper;
import database.api.SongwichDAO;

public interface AppDAO<I> extends SongwichDAO<App, I> {
	
	public App findById(I id);

	public App findByName(String name);
	
	public App findByDevAuthToken(String devAuthToken);
	
	public AppDeveloper findAppDevByAuthToken(String devAuthToken);

	public List<App> findByDevEmail(String devEmailAdress);
	
	public List<AppDeveloper> findAppDevByEmail(String devEmailAdress);
}
