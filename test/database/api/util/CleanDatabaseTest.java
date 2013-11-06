package database.api.util;

import models.api.scrobbles.AppDeveloper;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import behavior.api.usecases.RequestContext;
import util.api.DatabaseContext;

public class CleanDatabaseTest {
	private static final String DB_NAME = "songwich-api-test";
	protected static final AppDeveloper DEV = new AppDeveloper(
			"gabriel@tests.com", "Test Dev", null);
	protected static final RequestContext REQUEST_CONTEXT = new RequestContext(
			null, DEV, null);

	@Before
	public void setUp() throws Exception {
		DatabaseContext.createDatastore(DB_NAME);
	}

	@After
	public void tearDown() throws Exception {
		DatabaseContext.dropDatabase();
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
}
