package database.api;

import java.util.List;

import models.api.scrobbles.Song;
import models.api.stations.StationHistoryEntry;

import com.google.code.morphia.dao.DAO;

public interface StationHistoryDAO<I> extends DAO<StationHistoryEntry, I> {
	public StationHistoryEntry findById(I id);

	public long countByStationId(I stationId);

	public List<StationHistoryEntry> findByStationId(I stationId);

	public List<StationHistoryEntry> findByStationIdWithHourOffset(I stationId,
			int hourOffset);

	public long countByStationIdAndSongWithHourOffset(I stationId, Song song,
			int hourOffset);

	public List<StationHistoryEntry> findLastEntriesByStationId(I stationId,
			int numberOfEntries);

	public long countByStationIdAndArtist(I stationId, String artistName);

	public List<StationHistoryEntry> findByStationIdAndArtist(I stationId,
			String artistName);

	public long countByStationIdAndArtistWithHourOffset(I stationId,
			String artistName, int hourOffset);

	public List<StationHistoryEntry> findByStationIdAndArtistWithHourOffset(
			I stationId, String artistName, int hourOffset);
}
