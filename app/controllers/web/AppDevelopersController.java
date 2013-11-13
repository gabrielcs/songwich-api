package controllers.web;

import models.api.scrobbles.AppDeveloper;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import util.api.MyLogger;
import views.api.DTOValidator;
import views.api.scrobbles.AppDevelopersDTO_V0_4;
import behavior.api.usecases.scrobbles.AppDevelopersUseCases;
import controllers.api.annotation.Logged;

public class AppDevelopersController extends Controller {

	@Logged
	public static Result postAppDevelopers() {

		Form<AppDevelopersDTO_V0_4> appDevelopersForm = Form.form(
				AppDevelopersDTO_V0_4.class).bindFromRequest();
		if (appDevelopersForm.hasErrors()) {
			String errors = DTOValidator.errorsAsString(appDevelopersForm
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
