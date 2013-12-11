package controllers.api.subscriptions;

import java.util.List;

import play.data.Form;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import util.api.MyLogger;
import util.api.SongwichAPIException;
import views.api.APIResponse_V0_4;
import views.api.APIStatus_V0_4;
import views.api.DTOValidator;
import views.api.subscriptions.GetSubscriptionsResponse_V0_4;
import views.api.subscriptions.PostSubscriptionsResponse_V0_4;
import views.api.subscriptions.PutSubscriptionsResponse_V0_4;
import views.api.subscriptions.SubscriptionDTO_V0_4;
import views.api.subscriptions.SubscriptionInputDTO_V0_4;
import behavior.api.usecases.subscriptions.SubscriptionsUseCases;
import controllers.api.APIController;
import controllers.api.annotation.AppDeveloperAuthenticated;
import controllers.api.annotation.Logged;
import controllers.api.annotation.UserAuthenticated;

public class SubscriptionsController_V0_4 extends APIController {

	// TODO: this should be accessible only from the Songwich Radio app
	@AppDeveloperAuthenticated
	@UserAuthenticated
	@Logged
	public static Result postSubscriptions() {
		Form<SubscriptionInputDTO_V0_4> subscriptionForm = Form.form(
				SubscriptionInputDTO_V0_4.class).bindFromRequest();
		if (subscriptionForm.hasErrors()) {
			APIResponse_V0_4 apiResponse = new APIResponse_V0_4(
					APIStatus_V0_4.INVALID_PARAMETER,
					DTOValidator.errorsAsString(subscriptionForm.errors()));
			return badRequest(Json.toJson(apiResponse));
		} else {
			SubscriptionInputDTO_V0_4 subscriptionInputDTO = subscriptionForm.get();

			// process the request
			SubscriptionsUseCases subscriptionsUseCases = new SubscriptionsUseCases(
					getContext());
			SubscriptionDTO_V0_4 subscriptionDTO;
			try {
				subscriptionDTO = subscriptionsUseCases.postSubscriptions(subscriptionInputDTO);
			} catch (SongwichAPIException exception) {
				MyLogger.warn(String.format("%s [%s]: %s", exception
						.getStatus().toString(), exception.getMessage(),
						Http.Context.current().request()));
				APIResponse_V0_4 response = new APIResponse_V0_4(
						exception.getStatus(), exception.getMessage());
				if (exception.getStatus().equals(APIStatus_V0_4.UNAUTHORIZED)) {
					return Results.unauthorized(Json.toJson(response));
				} else {
					return Results.badRequest(Json.toJson(response));
				}
			}

			// return the response
			PostSubscriptionsResponse_V0_4 response = new PostSubscriptionsResponse_V0_4(
					APIStatus_V0_4.SUCCESS, "Success", subscriptionDTO);
			return ok(Json.toJson(response));
		}
	}

	@AppDeveloperAuthenticated
	@Logged
	public static Result getSubscriptions(String subscriptionId) {
		// process the request
		SubscriptionsUseCases subscriptionsUseCases = new SubscriptionsUseCases(
				getContext());
		List<SubscriptionDTO_V0_4> subscriptionsDTO;
		try {
			subscriptionsDTO = subscriptionsUseCases
					.getSubscriptions(subscriptionId);
		} catch (SongwichAPIException exception) {
			MyLogger.warn(String.format("%s [%s]: %s", exception.getStatus()
					.toString(), exception.getMessage(), Http.Context.current()
					.request()));
			APIResponse_V0_4 response = new APIResponse_V0_4(
					exception.getStatus(), exception.getMessage());
			return Results.badRequest(Json.toJson(response));
		}

		// return the response
		GetSubscriptionsResponse_V0_4 response = new GetSubscriptionsResponse_V0_4(
				APIStatus_V0_4.SUCCESS, "Success", subscriptionsDTO);
		return ok(Json.toJson(response));
	}

	// TODO: this should be accessible only from the Songwich Radio app
	@AppDeveloperAuthenticated
	@UserAuthenticated
	@Logged
	public static Result putEndSubscription(String subscriptionId) {
		// process the request
		SubscriptionsUseCases subscriptionsUseCases = new SubscriptionsUseCases(
				getContext());
		SubscriptionDTO_V0_4 subscriptionDTO;
		try {
			subscriptionDTO = subscriptionsUseCases
					.putEndSubscription(subscriptionId);
		} catch (SongwichAPIException exception) {
			MyLogger.warn(String.format("%s [%s]: %s", exception.getStatus()
					.toString(), exception.getMessage(), Http.Context.current()
					.request()));
			APIResponse_V0_4 response = new APIResponse_V0_4(
					exception.getStatus(), exception.getMessage());
			if (exception.getStatus().equals(APIStatus_V0_4.UNAUTHORIZED)) {
				return Results.unauthorized(Json.toJson(response));
			} else {
				return Results.badRequest(Json.toJson(response));
			}
		}

		// return the response
		PutSubscriptionsResponse_V0_4 response = new PutSubscriptionsResponse_V0_4(
				APIStatus_V0_4.SUCCESS, "Success", subscriptionDTO);
		return ok(Json.toJson(response));
	}

}
