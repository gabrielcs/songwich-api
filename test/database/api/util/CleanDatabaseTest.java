package database.api.util;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import usecases.api.util.DatabaseContext;

public class CleanDatabaseTest {
	protected static final String DEV_EMAIL = "developers@songwich.com";
	private String dbName = "songwich-api-test";

	@Before
	public void setUp() throws Exception {
		DatabaseContext.createDatastore(dbName);
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
