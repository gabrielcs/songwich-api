package controllers.api.util;

import models.api.App;
import models.api.AppDeveloper;
import models.api.User;
import controllers.api.AppDeveloperAuthController;
import controllers.api.UserAuthController;
import play.mvc.Controller;
import play.mvc.Http;
import usecases.api.util.RequestContext;

public class APIController extends Controller {
	
	protected static RequestContext getContext() {
		return new RequestContext(
				(App) Http.Context.current().args
						.get(AppDeveloperAuthController.APP),
				(AppDeveloper) Http.Context.current().args
						.get(AppDeveloperAuthController.DEV),
				(User) Http.Context.current().args.get(UserAuthController.USER));
	}
}
