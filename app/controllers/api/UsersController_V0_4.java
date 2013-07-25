package controllers.api;

import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import usecases.api.NewUserUseCase;
import controllers.api.annotation.AppDeveloperAuthenticated;
import controllers.api.util.SongwichController;
import dtos.api.UserDTO_V0_4;
import dtos.api.util.APIStatus_V0_4;
import dtos.api.util.UsersResponse_V0_4;

public class UsersController_V0_4 extends SongwichController {

	@AppDeveloperAuthenticated
	public static Result newUser() {
		Form<UserDTO_V0_4> form = Form.form(UserDTO_V0_4.class)
				.bindFromRequest();
		if (form.hasErrors()) {
			return badRequest(form.errorsAsJson());
		} else {
			UserDTO_V0_4 userDTO = form.get();

			// process the request
			NewUserUseCase newUserUseCase = new NewUserUseCase(getContext());
			newUserUseCase.newUser(userDTO);

			// return the response
			UsersResponse_V0_4 response = new UsersResponse_V0_4(
					APIStatus_V0_4.SUCCESS, "Success", userDTO);
			return ok(Json.toJson(response));
		}
	}
}
