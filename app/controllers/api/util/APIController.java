package controllers.api.util;

import behavior.api.usecases.RequestContext;
import models.api.scrobbles.App;
import models.api.scrobbles.AppDeveloper;
import models.api.scrobbles.User;
import controllers.api.AppDeveloperAuthController;
import controllers.api.UserAuthController;
import play.mvc.Controller;
import play.mvc.Http;

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
