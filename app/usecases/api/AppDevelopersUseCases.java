package usecases.api;

import java.util.UUID;

import models.App;
import models.AppDeveloper;
import play.Logger;
import usecases.api.util.RequestContext;
import usecases.api.util.UseCase;
import daos.api.AppDAOMongo;
import dtos.api.AppDevelopersDTO;

public class AppDevelopersUseCases extends UseCase {
	
	public AppDevelopersUseCases() {
		// this is not an API resource
		super(new RequestContext(null, null, null));
	}

	public AppDeveloper saveNewAppDeveloper(
			AppDevelopersDTO appDevelopersDTO) {
		// search the app in the database
		AppDAOMongo appDao = new AppDAOMongo();
		App app = appDao.findByName(appDevelopersDTO.getAppName());
		if (app == null) {
			// app was not in the database
			app = new App(appDevelopersDTO.getAppName(),
					appDevelopersDTO.getDevEmail());
		}

		// creates the AppDeveloper
		AppDeveloper appDeveloper = new AppDeveloper(
				appDevelopersDTO.getDevEmail(), appDevelopersDTO.getName(),
				UUID.randomUUID(), appDevelopersDTO.getDevEmail());
		app.addAppDeveloper(appDeveloper);
		appDao.cascadeSave(app);

		Logger.info(String.format(
				"Created '%s' working at '%s' with devAuthToken=%s",
				appDeveloper.getEmailAddress(), appDevelopersDTO.getAppName(),
				appDeveloper.getDevAuthToken()));

		return appDeveloper;
	}
}
