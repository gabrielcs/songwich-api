package controllers.api;

import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import usecases.api.ScrobbleUseCase;
import usecases.api.util.RequestContext;
import controllers.api.annotation.AppDeveloperAuthenticated;
import controllers.api.annotation.UserAuthenticated;
import dtos.api.ScrobbleDTO_V0_4;
import dtos.api.util.APIStatus_V0_4;
import dtos.api.util.ScrobblesResponse_V0_4;

public class ScrobblerController_V0_4 extends Controller {

	@AppDeveloperAuthenticated
	@UserAuthenticated
	public static Result scrobble() {
		Form<ScrobbleDTO_V0_4> form = Form.form(ScrobbleDTO_V0_4.class)
				.bindFromRequest();
		if (form.hasErrors()) {
			return badRequest(form.errorsAsJson());
		} else {
			ScrobbleDTO_V0_4 scrobbleDTO = form.get();

			// process the request
			RequestContext ctx = new RequestContext(
					AppDeveloperAuthenticationController.getApp(),
					AppDeveloperAuthenticationController.getAppDeveloper(),
					UserAuthenticationController.getUser());
			ScrobbleUseCase scrobbleUseCase = new ScrobbleUseCase(ctx);
			scrobbleUseCase.scrobble(scrobbleDTO);
			
			// return the response
			ScrobblesResponse_V0_4 response = new ScrobblesResponse_V0_4(
					APIStatus_V0_4.SUCCESS, "Success", scrobbleDTO);
			return ok(Json.toJson(response));
		}
	}
}
