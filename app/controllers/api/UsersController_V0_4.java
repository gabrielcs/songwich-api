package controllers.api;

import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import usecases.api.UsersUseCases;
import controllers.api.annotation.AppDeveloperAuthenticated;
import controllers.api.util.APIController;
import dtos.api.UsersDTO_V0_4;
import dtos.api.util.APIResponse_V0_4;
import dtos.api.util.APIStatus_V0_4;
import dtos.api.util.DataTransferObject;
import dtos.api.util.PostUsersResponse_V0_4;

public class UsersController_V0_4 extends APIController {

	@AppDeveloperAuthenticated
	public static Result postUsers() {
		Form<UsersDTO_V0_4> form = Form.form(UsersDTO_V0_4.class)
				.bindFromRequest();
		if (form.hasErrors()) {
			APIResponse_V0_4 apiResponse = new APIResponse_V0_4(
					APIStatus_V0_4.INVALID_PARAMETER,
					DataTransferObject.errorsAsString(form.errors()));
			return badRequest(Json.toJson(apiResponse));
		} else {
			UsersDTO_V0_4 userDTO = form.get();

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
