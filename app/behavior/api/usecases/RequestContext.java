package behavior.api.usecases;

import models.api.scrobbles.App;
import models.api.scrobbles.AppDeveloper;
import models.api.scrobbles.User;

public class RequestContext {
	
	private App app;
	private AppDeveloper appDeveloper;
	private User user;
	private Long timestamp;

	public RequestContext(App app, AppDeveloper appDeveloper, User user, Long timestamp) {
		super();
		this.app = app;
		this.appDeveloper = appDeveloper;
		this.user = user;
		this.timestamp = timestamp;
	}
	
	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
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
