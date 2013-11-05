package behavior.api.algorithms;

public abstract class AbstractStationReadinessCalculator implements
		StationReadinessCalculator {

	private Float stationReadiness;

	protected AbstractStationReadinessCalculator() {
	}

	/**
	 * @return a float between 0 and 1 representing the percentage of the
	 *         station's readiness
	 */
	protected abstract Float calculateStationReadiness();

	@Override
	public Float getStationReadiness() {
		if (stationReadiness != null) {
			stationReadiness = calculateStationReadiness();
		}
		return stationReadiness;
	}

	@Override
	public Boolean isStationReady() {
		if (stationReadiness == null) {
			stationReadiness = calculateStationReadiness();
		}
		return (stationReadiness < 1) ? false : true;
	}

	@Override
	public String toString() {
		return "AbstractStationReadinessCalculator [stationReadiness="
				+ stationReadiness + "]";
	}
}
