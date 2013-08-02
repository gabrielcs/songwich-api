package controllers.web;

import java.io.Console;

import models.AppDeveloper;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import usecases.api.AppDevelopersUseCases;
import dtos.api.AppDevelopersDTO;
import dtos.api.util.DataTransferObject;

public class AppDevelopersController extends Controller {


	public static Result postAppDevelopers() {

	    Logger.info("hello, inside postappdevelopersssssssssssssssssssssssssssssssssssssss\n");
	    
		Form<AppDevelopersDTO> appDevelopersForm = Form.form(
				AppDevelopersDTO.class).bindFromRequest();
		if (appDevelopersForm.hasErrors()) {
			String errors = DataTransferObject.errorsAsString(appDevelopersForm
					.errors());
			 Logger.info("errors : "+errors);
			// TODO, Caon
			return badRequest();
		} else {
			AppDevelopersUseCases appDevelopersUseCases = new AppDevelopersUseCases();
			AppDeveloper appDeveloper = appDevelopersUseCases
					.saveNewAppDeveloper(appDevelopersForm.get());
			// appDeveloper saved successfully
			 Logger.info(" appDeveloper saved successfully ");
			// TODO, Caon
			return ok();
		}
	}

}
