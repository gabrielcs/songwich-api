package controllers.web;

import play.*;
import play.api.Routes;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {
  
    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }
  
/*
	public static Result javascriptRoutes() {
	    response().setContentType("text/javascript");
	    return ok(
	        Routes.javascriptRouter("jsRoutes",
	        	controllers.web.routes.javascript.Application.index()
	        	
	            //routes.javascript.AppDevelopersController.postAppDevelopers()
	            
	            //controllers.routes.javascript.Projects.rename(),
	            //controllers.routes.javascript.Projects.addGroup()
	        )
	    );
	}*/
}
