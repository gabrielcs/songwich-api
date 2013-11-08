package util.api;

import models.api.scrobbles.App;
import models.api.scrobbles.AppDeveloper;
import models.api.scrobbles.AuthToken;
import models.api.scrobbles.User;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;

import behavior.api.usecases.RequestContext;
import database.api.CascadeSaveDAO;
import database.api.scrobbles.AppDAOMongo;

public class WithRequestContext extends WithCleanDatabase {
	private RequestContext requestContext;

	protected RequestContext getContext() {
		return requestContext;
	}
	
	protected void setRequestContextUser(User testUser) {
		requestContext.setUser(testUser);
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		createRequestContext();
	}
	
	private void createRequestContext() {
		String testDevEmail = "developers@songwich.com";
		String testDevAuthToken = "3bde6fba-1ae5-4d7f-8000-f2aba160b71a";

		// sets devAuthToken
		AuthToken authToken = AuthToken.createDevAuthToken();
		authToken.setToken(testDevAuthToken);
		
		// creates a test AppDeveloper
		AppDeveloper testDev = new AppDeveloper(testDevEmail,
				"Songwich Developers", authToken);
		// creates a test App
		App testApp = new App("Songwich", testDev);
		CascadeSaveDAO<App, ObjectId> appDao = new AppDAOMongo();
		appDao.cascadeSave(testApp, testDevEmail);
		// request contexts
		requestContext = new RequestContext(testApp, testDev, null);
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
}
