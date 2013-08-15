package usecases.api;

import java.util.UUID;

import models.api.App;
import models.api.AppDeveloper;
import play.Logger;
import usecases.api.util.RequestContext;
import usecases.api.util.UseCase;
import views.api.AppDevelopersDTO;
import database.api.AppDAOMongo;

public class AppDevelopersUseCases extends UseCase {

	public AppDevelopersUseCases() {
		// this is not an API resource
		super(new RequestContext(null, null, null));
	}

	public AppDeveloper saveNewAppDeveloper(AppDevelopersDTO appDevelopersDTO) {
		// search the app in the database
		AppDAOMongo appDao = new AppDAOMongo();
		App app = appDao.findByName(appDevelopersDTO.getAppName());
		if (app == null) {
			// app was not in the database
			app = new App(appDevelopersDTO.getAppName(),
					appDevelopersDTO.getDevEmail());
		} else {
			// check that AppDeveloper is not already in database
			AppDeveloper appDevDatabase = app.getAppDeveloper(appDevelopersDTO
					.getDevEmail());
			if (appDevDatabase != null) {
				// AppDeveloper already in database
				Logger.info(String
						.format("Tried to create AppDeveloper \"%s\" but it was already in database with authToken=%s",
								appDevelopersDTO.getDevEmail(),
								appDevDatabase.getDevAuthToken()));
				return appDevDatabase;
			}
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
