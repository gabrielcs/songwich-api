package util.api;

import org.junit.After;
import org.junit.Before;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class WithProductionDependencyInjection extends WithRequestContext {

	private Injector injector;
	
	protected Injector getInjector() {
		return injector;
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// for dependency injection
		injector = Guice
				.createInjector(new ProductionDependencyInjectionModule());
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
}
