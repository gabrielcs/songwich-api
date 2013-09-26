package behavior.api.algorithms;

import models.api.scrobbles.Song;
import models.api.stations.RadioStation;

public interface StationStrategy {

	public Song next(RadioStation radioStation);

}
