package controllers.api;

import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import usecases.api.UsersUseCases;
import controllers.api.annotation.AppDeveloperAuthenticated;
import controllers.api.util.SongwichController;
import dtos.api.PostUsersDTO_V0_4;
import dtos.api.util.APIStatus_V0_4;
import dtos.api.util.PostUsersResponse_V0_4;

public class UsersController_V0_4 extends SongwichController {

	@AppDeveloperAuthenticated
	public static Result postUsers() {
		Form<PostUsersDTO_V0_4> form = Form.form(PostUsersDTO_V0_4.class)
				.bindFromRequest();
		if (form.hasErrors()) {
			return badRequest(form.errorsAsJson());
		} else {
			PostUsersDTO_V0_4 userDTO = form.get();

			// process the request
			UsersUseCases usersUseCases = new UsersUseCases(getContext());
			usersUseCases.postUsers(userDTO);

			// return the response
			PostUsersResponse_V0_4 response = new PostUsersResponse_V0_4(
					APIStatus_V0_4.SUCCESS, "Success", userDTO);
			return ok(Json.toJson(response));
		}
	}
}
