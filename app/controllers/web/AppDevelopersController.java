package controllers.web;

import behavior.api.usecases.scrobbles.AppDevelopersUseCases;
import models.api.scrobbles.AppDeveloper;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import util.api.MyLogger;
import views.api.DataTransferObject;
import views.api.scrobbles.AppDevelopersDTO;

public class AppDevelopersController extends Controller {

	public static Result postAppDevelopers() {

		Form<AppDevelopersDTO> appDevelopersForm = Form.form(
				AppDevelopersDTO.class).bindFromRequest();
		if (appDevelopersForm.hasErrors()) {
			String errors = DataTransferObject.errorsAsString(appDevelopersForm
					.errors());
			MyLogger.info(String.format(
					"Error(s) creating AppDeveloper [%s]: %s",
					appDevelopersForm.toString(), errors));
			return badRequest(errors);
		} else {
			AppDevelopersUseCases appDevelopersUseCases = new AppDevelopersUseCases();
			AppDeveloper appDeveloper = appDevelopersUseCases
					.saveNewAppDeveloper(appDevelopersForm.get());
			return ok(appDeveloper.getDevAuthToken().getToken());
		}
	}

}
