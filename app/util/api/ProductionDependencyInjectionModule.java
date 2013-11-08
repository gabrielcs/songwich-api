package util.api;
import behavior.api.algorithms.PseudoDMCAStationStrategy;
import behavior.api.algorithms.StationStrategy;

import com.google.inject.AbstractModule;

public class ProductionDependencyInjectionModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(StationStrategy.class).to(PseudoDMCAStationStrategy.class);
	}
}
