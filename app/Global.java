import play.GlobalSettings;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Http.RequestHeader;
import play.mvc.Results;
import play.mvc.SimpleResult;
import util.api.DAOProvider;
import util.api.DatabaseContext;
import util.api.DevelopmentDependencyInjectionModule;
import util.api.MongoDependencyInjectionModule;
import util.api.MyLogger;
import util.api.ProductionDependencyInjectionModule;
import views.api.APIResponse_V0_4;
import views.api.APIStatus_V0_4;

import com.google.inject.Guice;
import com.google.inject.Injector;

import controllers.api.auth.AppDeveloperAuthController;

public class Global extends GlobalSettings {

	// for dependency injection
	private static Injector INJECTOR;

	/*
	 * Checks whether it is the Web Application and not a scheduled job.
	 */
	private boolean isWebApplication(play.Application app) {
		return app.configuration().getBoolean("application.web");
	}

	@Override
	public void onStart(play.Application app) {
		if (isWebApplication(app)) {

			if (app.isProd()) {
				// connects to a MongodDB-as-a-Service database
				// it may be a production or a staging database according to the
				// environment variable
				String dbName = System.getenv("MONGOHQ_DBNAME");
				String uri = System.getenv("MONGOHQ_URL");
				DatabaseContext.createDatastore(uri, dbName);

				// dependency injection
				INJECTOR = Guice.createInjector(
						new ProductionDependencyInjectionModule(),
						new MongoDependencyInjectionModule());

				DAOProvider.setInjector(INJECTOR);
			}

			if (app.isDev()) {
				// starts with a clean local database if in development mode
				String dbName = app.configuration().getString(
						"mongo.local.dbname");
				DatabaseContext.createDatastore(dbName);
				DatabaseContext.dropDatabase();

				// and creates a test developer
				AppDeveloperAuthController.createTestAppWithDeveloper(app
						.configuration().getString("dev.auth.token"));

				// dependency injection
				INJECTOR = Guice.createInjector(
						new DevelopmentDependencyInjectionModule(),
						new MongoDependencyInjectionModule());

				DAOProvider.setInjector(INJECTOR);
			}
		}
	}

	@Override
	public <A> A getControllerInstance(Class<A> controllerClass)
			throws Exception {
		return INJECTOR.getInstance(controllerClass);
	}

	@Override
	public Promise<SimpleResult> onBadRequest(RequestHeader request,
			String error) {
		// public Result onBadRequest(RequestHeader request, String error) {
		MyLogger.warn(String.format("Bad request [%s]: %s\n", error, request));
		APIResponse_V0_4 response = new APIResponse_V0_4(
				APIStatus_V0_4.BAD_REQUEST, error);
		return Promise.<SimpleResult> pure(Results.badRequest(Json
				.toJson(response)));
		// return Results.badRequest(Json.toJson(response));
	}

	@Override
	public Promise<SimpleResult> onHandlerNotFound(RequestHeader request) {
		// public Result onHandlerNotFound(RequestHeader request) {
		// ignore GET /favicon.ico
		if (request.toString().equals("GET /favicon.ico")) {
			// return Results.badRequest();
			return Promise.<SimpleResult> pure(Results.badRequest());
		}

		MyLogger.warn("Handler not found: " + request);
		APIResponse_V0_4 response = new APIResponse_V0_4(
				APIStatus_V0_4.BAD_REQUEST, String.format(
						"API method not found: %s %s", request.method(),
						request.path()));

		return Promise.<SimpleResult> pure(Results.badRequest(Json
				.toJson(response)));
		// return Results.badRequest(Json.toJson(response));
	}

	@Override
	public Promise<SimpleResult> onError(RequestHeader request, Throwable t) {
		// public Result onError(RequestHeader request, Throwable t) {
		// get the message of the Exception wrapped inside Play's
		// ExecutionExeption
		String message = t.getCause().getClass().getSimpleName();
		if (t.getCause().getMessage() != null) {
			message = String.format("%s: %s", message, t.getCause()
					.getMessage());
		}
		// it's currently not showing POST requests parameters
		MyLogger.error(String.format("Error while processing: %s [%s]",
				request, message));

		APIResponse_V0_4 response = new APIResponse_V0_4(
				APIStatus_V0_4.UNKNOWN_ERROR, message);
		return Promise.<SimpleResult> pure(Results.badRequest(Json
				.toJson(response)));
		// return Results.badRequest(Json.toJson(response));
	}

}