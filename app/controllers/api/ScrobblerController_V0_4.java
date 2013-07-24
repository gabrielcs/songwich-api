package controllers.api;

import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.api.ScrobbleDTO_V0_4;
import views.api.util.APIStatus_V0_4;
import views.api.util.ScrobbleResponse_V0_4;
import controllers.api.annotation.AppDeveloperAuthenticated;
import controllers.api.annotation.UserAuthenticated;

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
			// TODO: process the request
			ScrobbleResponse_V0_4 response = new ScrobbleResponse_V0_4(
					APIStatus_V0_4.SUCCESS, "Success", scrobbleDTO);
			return ok(Json.toJson(response));
		}
	}
}
