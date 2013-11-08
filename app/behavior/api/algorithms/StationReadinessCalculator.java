package behavior.api.algorithms;

public interface StationReadinessCalculator {
	/**
	 * @throws IllegalStateException if setStation() hasn't been called first
	 */
	public Float getStationReadiness() throws IllegalStateException;;
	
	/**
	 * @throws IllegalStateException if setStation() hasn't been called first
	 */
	public Boolean isStationReady() throws IllegalStateException;;
}
