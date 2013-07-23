package daos.api;

import java.util.UUID;

import models.App;
import models.AppDeveloper;

import com.google.code.morphia.dao.DAO;

public interface AppDAO<I> extends DAO<App, I> {
	
	public App findById(I id);

	public App findByName(String name);
	
	public App findByDevAuthToken(UUID devAuthToken);
	
	public AppDeveloper findAppDevByAuthToken(UUID devAuthToken);

	public App findByDevEmail(String devEmailAdress);
	
	public AppDeveloper findAppDevByEmail(String devEmailAdress);
}
