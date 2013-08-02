package controllers.web;

import models.AppDeveloper;
import play.Routes;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import usecases.api.AppDevelopersUseCases;
import views.html.index;
import dtos.api.AppDevelopersDTO;
import dtos.api.util.DataTransferObject;


public class Application extends Controller {

	public static Result index() {
		return ok(index.render("Your new application is ready."));
	}

	/*
	 * Untrail GET URLs
	 * (http://stackoverflow.com/questions/13189095/play-framework2-remove-trailing-slash-from-urls)
	 */
	public static Result untrail(String path) {
		   return movedPermanently("/" + path);
    }

	public static Result javascriptRoutes() {
		response().setContentType("text/javascript");
		return ok(Routes.javascriptRouter("jsRoutes",
				controllers.web.routes.javascript.AppDevelopersController.postAppDevelopers()));
	}

}
