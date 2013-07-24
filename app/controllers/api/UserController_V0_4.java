package controllers.api;

import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import usecases.api.NewUserUseCase;
import usecases.api.util.RequestContext;
import controllers.api.annotation.AppDeveloperAuthenticated;
import dtos.api.UserDTO_V0_4;
import dtos.api.util.APIStatus_V0_4;
import dtos.api.util.UsersResponse_V0_4;

public class UserController_V0_4 extends Controller {

	@AppDeveloperAuthenticated
	public static Result newUser() {
		Form<UserDTO_V0_4> form = Form.form(UserDTO_V0_4.class)
				.bindFromRequest();
		if (form.hasErrors()) {
			return badRequest(form.errorsAsJson());
		} else {
			UserDTO_V0_4 userDTO = form.get();
			
			// process the request
			RequestContext ctx = new RequestContext(
					AppDeveloperAuthenticationController.getApp(),
					AppDeveloperAuthenticationController.getAppDeveloper(),
					UserAuthenticationController.getUser());
			NewUserUseCase newUserUseCase = new NewUserUseCase(ctx);
			newUserUseCase.newUser(userDTO);

			// return the response
			UsersResponse_V0_4 response = new UsersResponse_V0_4(
					APIStatus_V0_4.SUCCESS, "Success", userDTO);
			return ok(Json.toJson(response));
		}
	}
}
