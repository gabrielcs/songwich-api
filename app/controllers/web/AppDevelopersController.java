package controllers.web;

import java.io.Console;

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
			Logger.info("errors : "+errors);
			// TODO, Caon
			return badRequest(errors);
		} else {
			AppDevelopersUseCases appDevelopersUseCases = new AppDevelopersUseCases();
			AppDeveloper appDeveloper = appDevelopersUseCases
					.saveNewAppDeveloper(appDevelopersForm.get());
			// appDeveloper saved successfully
			 Logger.info(" appDeveloper saved successfully ");
			// TODO, Caon
			return ok(appDeveloper.getDevAuthToken());
		}
	}

}
