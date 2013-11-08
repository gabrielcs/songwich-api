package util.api;
import behavior.api.algorithms.NaiveStationStrategy;
import behavior.api.algorithms.StationStrategy;

import com.google.inject.AbstractModule;

public class DevelopmentDependencyInjectionModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(StationStrategy.class).to(NaiveStationStrategy.class);
	}
}
