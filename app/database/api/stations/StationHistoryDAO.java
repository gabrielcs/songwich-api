package database.api.stations;

import java.util.List;

import models.api.scrobbles.Song;
import models.api.stations.StationHistoryEntry;

import org.bson.types.ObjectId;

import database.api.SongwichDAO;

public interface StationHistoryDAO<I> extends
		SongwichDAO<StationHistoryEntry, I> {

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

	public List<StationHistoryEntry> findStarredByUserId(I userId, int results);

	public List<StationHistoryEntry> findStarredByUserIdSince(I userId,
			I sinceStationHistoryEntry, boolean inclusive, int results);

	public List<StationHistoryEntry> findStarredByUserIdUntil(I userId,
			I untilStationHistoryEntry, boolean inclusive, int results);

	public StationHistoryEntry isSongStarredByUser(ObjectId objectId, Song song);
}
