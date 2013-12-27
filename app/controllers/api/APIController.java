package controllers.api;

import models.api.scrobbles.App;
import models.api.scrobbles.AppDeveloper;
import models.api.scrobbles.User;
import play.mvc.Controller;
import play.mvc.Http;
import behavior.api.usecases.RequestContext;
import controllers.api.auth.AppDeveloperAuthController;
import controllers.api.auth.TimestampController;
import controllers.api.auth.UserAuthController;

public class APIController extends Controller {

	public APIController() {
	}

	protected static RequestContext getContext() {
		return new RequestContext(
				(App) Http.Context.current().args
						.get(AppDeveloperAuthController.APP),
				(AppDeveloper) Http.Context.current().args
						.get(AppDeveloperAuthController.DEV),
				(User) Http.Context.current().args.get(UserAuthController.USER),
				(Long) Http.Context.current().args
						.get(TimestampController.TIMESTAMP));
	}
}
