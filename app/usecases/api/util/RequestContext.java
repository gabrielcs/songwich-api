package usecases.api.util;

import models.api.App;
import models.api.AppDeveloper;
import models.api.User;

public class RequestContext {
	
	private App app;
	private AppDeveloper appDeveloper;
	private User user;
	
	public RequestContext(App app, AppDeveloper appDeveloper, User user) {
		super();
		this.app = app;
		this.appDeveloper = appDeveloper;
		this.user = user;
	}

	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
	}

	public AppDeveloper getAppDeveloper() {
		return appDeveloper;
	}

	public void setAppDeveloper(AppDeveloper appDeveloper) {
		this.appDeveloper = appDeveloper;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
