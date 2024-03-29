package util.api;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import util.api.DatabaseContext;

public class WithCleanDatabase {
	private static final String DB_NAME = "songwich-api-test";

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
