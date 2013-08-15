package controllers.web;

import models.api.AppDeveloper;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import usecases.api.AppDevelopersUseCases;
import views.api.AppDevelopersDTO;
import views.api.util.DataTransferObject;

public class AppDevelopersController extends Controller {

	public static Result postAppDevelopers() {

		Form<AppDevelopersDTO> appDevelopersForm = Form.form(
				AppDevelopersDTO.class).bindFromRequest();
		if (appDevelopersForm.hasErrors()) {
			String errors = DataTransferObject.errorsAsString(appDevelopersForm
					.errors());
			Logger.info(String.format(
					"Error(s) creating AppDeveloper [%s]: %s",
					appDevelopersForm.toString(), errors));
			return badRequest(errors);
		} else {
			AppDevelopersUseCases appDevelopersUseCases = new AppDevelopersUseCases();
			AppDeveloper appDeveloper = appDevelopersUseCases
					.saveNewAppDeveloper(appDevelopersForm.get());
			return ok(appDeveloper.getDevAuthToken());
		}
	}

}
