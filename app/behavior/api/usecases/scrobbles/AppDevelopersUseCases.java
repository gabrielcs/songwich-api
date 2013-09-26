package behavior.api.usecases.scrobbles;

import behavior.api.usecases.RequestContext;
import behavior.api.usecases.UseCase;
import models.api.scrobbles.App;
import models.api.scrobbles.AppDeveloper;
import models.api.scrobbles.AuthToken;
import util.api.MyLogger;
import views.api.scrobbles.AppDevelopersDTO_V0_4;
import database.api.scrobbles.AppDAOMongo;

public class AppDevelopersUseCases extends UseCase {

	public AppDevelopersUseCases() {
		// this is not an API resource
		super(new RequestContext(null, null, null));
	}

	public AppDeveloper saveNewAppDeveloper(AppDevelopersDTO_V0_4 appDevelopersDTO) {
		// search the app in the database
		AppDAOMongo appDao = new AppDAOMongo();
		App app = appDao.findByName(appDevelopersDTO.getAppName());
		if (app == null) {
			// app was not in the database
			app = new App(appDevelopersDTO.getAppName());
		} else {
			// check that AppDeveloper is not already in database
			AppDeveloper appDevDatabase = app.getAppDeveloper(appDevelopersDTO
					.getDevEmail());
			if (appDevDatabase != null) {
				// AppDeveloper already in database
				MyLogger.info(String
						.format("Tried to create AppDeveloper \"%s\" but it was already in database with authToken=%s",
								appDevelopersDTO.getDevEmail(), appDevDatabase
										.getDevAuthToken().getToken()));
				return appDevDatabase;
			}
		}

		// creates the AppDeveloper
		AppDeveloper appDeveloper = new AppDeveloper(
				appDevelopersDTO.getDevEmail(), appDevelopersDTO.getName(),
				AuthToken.createDevAuthToken());
		app.addAppDeveloper(appDeveloper);
		appDao.cascadeSave(app, appDevelopersDTO.getDevEmail());

		MyLogger.info(String.format(
				"Created '%s' working at '%s' with devAuthToken=%s",
				appDeveloper.getEmailAddress(), appDevelopersDTO.getAppName(),
				appDeveloper.getDevAuthToken().getToken()));
		return appDeveloper;
	}
}
