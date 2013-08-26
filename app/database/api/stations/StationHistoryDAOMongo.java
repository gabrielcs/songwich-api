package database.api.stations;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import models.api.scrobbles.Song;
import models.api.stations.StationHistoryEntry;

import org.bson.types.ObjectId;

import com.google.code.morphia.query.Query;

import database.api.BasicDAOMongo;

public class StationHistoryDAOMongo extends BasicDAOMongo<StationHistoryEntry>
		implements StationHistoryDAO<ObjectId> {

	public StationHistoryDAOMongo() {
	}

	@Override
	public StationHistoryEntry findById(ObjectId id) {
		return ds.find(StationHistoryEntry.class).filter("id", id).get();
	}

	@Override
	public List<StationHistoryEntry> findByStationId(ObjectId stationId) {
		Query<StationHistoryEntry> query = queryByStationId(stationId); 
		return order(query).asList();
	}

	// TODO: test
	@Override
	public long countByStationId(ObjectId stationId) {
		return queryByStationId(stationId).countAll();
	}

	// TODO: test
	@Override
	public List<StationHistoryEntry> findByStationIdWithHourOffset(
			ObjectId stationId, int hourOffset) {
		Query<StationHistoryEntry> query = queryByStationId(stationId);
		filterHourOffset(query, hourOffset);
		return order(query).asList();
	}

	// TODO: test
	@Override
	public long countByStationIdAndSongWithHourOffset(ObjectId stationId,
			Song song, int hourOffset) {
		Query<StationHistoryEntry> query = queryByStationIdAndSong(stationId,
				song);
		return filterHourOffset(query, hourOffset).countAll();
	}

	// TODO: test
	@Override
	public List<StationHistoryEntry> findLastEntriesByStationId(
			ObjectId stationId, int numberOfEntries) {
		Query<StationHistoryEntry> query = queryByStationId(stationId);
		return order(query).limit(numberOfEntries).asList();
	}

	// TODO: test
	@Override
	public long countByStationIdAndArtist(ObjectId stationId, String artistName) {
		return queryByStationIdAndArtist(stationId, artistName).countAll();
	}

	// TODO: test
	@Override
	public List<StationHistoryEntry> findByStationIdAndArtist(
			ObjectId stationId, String artistName) {
		Query<StationHistoryEntry> query = queryByStationIdAndArtist(stationId,
				artistName);
		return order(query).asList();
	}

	// TODO: test
	@Override
	public long countByStationIdAndArtistWithHourOffset(ObjectId stationId,
			String artistName, int hourOffset) {
		return queryByStationIdAndArtistWithHourOffset(stationId, artistName,
				hourOffset).countAll();
	}

	// TODO: test
	@Override
	public List<StationHistoryEntry> findByStationIdAndArtistWithHourOffset(
			ObjectId stationId, String artistName, int hourOffset) {
		Query<StationHistoryEntry> query = queryByStationIdAndArtistWithHourOffset(
				stationId, artistName, hourOffset);
		return order(query).asList();
	}

	private Query<StationHistoryEntry> order(Query<StationHistoryEntry> query) {
		return query.order("-timestamp");
	}

	private Query<StationHistoryEntry> queryByStationId(ObjectId stationId) {
		return ds.find(StationHistoryEntry.class)
				.filter("stationId", stationId);
	}

	private Query<StationHistoryEntry> queryByStationIdAndArtist(
			ObjectId stationId, String artistName) {
		return queryByStationId(stationId).filter("song.artistNames",
				artistName);
	}

	private Query<StationHistoryEntry> queryByStationIdAndSong(
			ObjectId stationId, Song song) {
		return queryByStationId(stationId).filter("song", song);
	}

	private Query<StationHistoryEntry> queryByStationIdAndArtistWithHourOffset(
			ObjectId stationId, String artistName, int hourOffset) {
		Query<StationHistoryEntry> query = queryByStationIdAndArtist(stationId,
				artistName);
		return filterHourOffset(query, hourOffset);
	}

	private <T> Query<T> filterHourOffset(Query<T> query, int hourOffset) {
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.HOUR, -hourOffset);
		long hourOffsetMillis = calendar.getTimeInMillis();

		return query.filter("timestamp", System.currentTimeMillis()
				- hourOffsetMillis);
	}
}
