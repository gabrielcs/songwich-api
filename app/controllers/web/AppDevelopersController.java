package controllers.web;

import models.AppDeveloper;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import usecases.api.AppDevelopersUseCases;
import dtos.api.AppDevelopersDTO;
import dtos.api.util.DataTransferObject;

public class AppDevelopersController extends Controller {

	public static Result postAppDevelopers() {
		Form<AppDevelopersDTO> appDevelopersForm = Form.form(
				AppDevelopersDTO.class).bindFromRequest();
		if (appDevelopersForm.hasErrors()) {
			String errors = DataTransferObject.errorsAsString(appDevelopersForm
					.errors());
			// TODO, Caon
			return badRequest();
		} else {
			AppDevelopersUseCases appDevelopersUseCases = new AppDevelopersUseCases();
			AppDeveloper appDeveloper = appDevelopersUseCases
					.saveNewAppDeveloper(appDevelopersForm.get());
			// appDeveloper saved successfully
			// TODO, Caon
			return ok();
		}
	}

}
