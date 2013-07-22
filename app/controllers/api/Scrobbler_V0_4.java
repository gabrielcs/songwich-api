package controllers.api;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import views.api.ScrobbleDTO_V0_4;
import views.api.util.ScrobbleResponse_V0_4;

@With(AuthenticationController.class)
public class Scrobbler_V0_4 extends Controller {

	public static Result scrobble() {
		Form<ScrobbleDTO_V0_4> form = Form.form(ScrobbleDTO_V0_4.class).bindFromRequest();
		if (form.hasErrors()) {
			return badRequest(form.errorsAsJson());
		} else {
			ScrobbleDTO_V0_4 scrobbleDTO = form.get();
			ScrobbleResponse_V0_4 response = new ScrobbleResponse_V0_4(views.api.util.Status.SUCCESS,
				"Success", scrobbleDTO);
			return ok(response.toJson());
		}
	}
}
