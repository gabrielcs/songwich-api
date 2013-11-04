package behavior.api.algorithms;

import models.api.stations.RadioStation;

public interface StationReadinessCalculator {
	
	public Float getStationReadiness(RadioStation station);

}
