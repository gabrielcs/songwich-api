package database.api.stations;

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import models.api.scrobbles.Song;
import models.api.stations.SongFeedback;
import models.api.stations.SongFeedback.FeedbackType;
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

	@Override
	public long countByStationId(ObjectId stationId) {
		return queryByStationId(stationId).countAll();
	}

	@Override
	public List<StationHistoryEntry> findByStationIdWithHourOffset(
			ObjectId stationId, int hourOffset) {
		Query<StationHistoryEntry> query = queryByStationId(stationId);
		filterHourOffset(query, hourOffset);
		return order(query).asList();
	}

	@Override
	public long countByStationIdAndSongWithHourOffset(ObjectId stationId,
			Song song, int hourOffset) {
		Query<StationHistoryEntry> query = queryByStationIdAndSong(stationId,
				song);
		return filterHourOffset(query, hourOffset).countAll();
	}

	@Override
	public List<StationHistoryEntry> findLastEntriesByStationId(
			ObjectId stationId, int numberOfEntries) {
		Query<StationHistoryEntry> query = queryByStationId(stationId);
		return order(query).limit(numberOfEntries).asList();
	}

	@Override
	public long countByStationIdAndArtist(ObjectId stationId, String artistName) {
		return queryByStationIdAndArtist(stationId, artistName).countAll();
	}

	@Override
	public List<StationHistoryEntry> findByStationIdAndArtist(
			ObjectId stationId, String artistName) {
		Query<StationHistoryEntry> query = queryByStationIdAndArtist(stationId,
				artistName);
		return order(query).asList();
	}

	@Override
	public long countByStationIdAndArtistWithHourOffset(ObjectId stationId,
			String artistName, int hourOffset) {
		return queryByStationIdAndArtistWithHourOffset(stationId, artistName,
				hourOffset).countAll();
	}

	@Override
	public List<StationHistoryEntry> findByStationIdAndArtistWithHourOffset(
			ObjectId stationId, String artistName, int hourOffset) {
		Query<StationHistoryEntry> query = queryByStationIdAndArtistWithHourOffset(
				stationId, artistName, hourOffset);
		return order(query).asList();
	}

	// http://stackoverflow.com/questions/19596949/how-to-build-a-morphia-query-on-a-subset-of-the-properties-of-a-java-collection
	@Override
	public List<StationHistoryEntry> findStarredByUserId(ObjectId userId,
			int results) {
		/*
		 * Query<StationHistoryEntry> query = ds
		 * .createQuery(StationHistoryEntry.class); query.and(
		 * query.criteria("songFeedback.userId").equal(userId),
		 * query.criteria("songFeedback.feedbackType"
		 * ).equal(FeedbackType.STAR)); return query.asList();
		 */

		SongFeedback songFeedback = new SongFeedback(FeedbackType.STAR, userId);
		Query<StationHistoryEntry> query = ds.find(StationHistoryEntry.class)
		// .filter("songFeedback elem", songFeedback);
				.field("songFeedback").hasThisElement(songFeedback);
		return order(query).limit(results).asList();
	}

	@Override
	public List<StationHistoryEntry> findStarredByUserIdSince(ObjectId userId,
			ObjectId sinceObjectId, boolean inclusive, int results) {

		SongFeedback songFeedback = new SongFeedback(FeedbackType.STAR, userId);
		Query<StationHistoryEntry> query = ds.find(StationHistoryEntry.class)
		// .filter("songFeedback elem", songFeedback);
				.field("songFeedback").hasThisElement(songFeedback);

		filterSince(query, sinceObjectId, inclusive);
		// gets the oldest from the selected bunch and limit results
		query = orderReverse(query).limit(results);

		// order by newest
		List<StationHistoryEntry> result = query.asList();
		Collections.reverse(result);
		return result;
	}
	
	@Override
	public List<StationHistoryEntry> findStarredByUserIdUntil(ObjectId userId,
			ObjectId untilObjectId, boolean inclusive, int results) {

		SongFeedback songFeedback = new SongFeedback(FeedbackType.STAR, userId);
		Query<StationHistoryEntry> query = ds.find(StationHistoryEntry.class)
		// .filter("songFeedback elem", songFeedback);
				.field("songFeedback").hasThisElement(songFeedback);

		filterUntil(query, untilObjectId, inclusive);
		return order(query).limit(results).asList();
	}

	// TODO: test
	// this is case sensitive so far
	@Override
	public StationHistoryEntry isSongStarredByUser(ObjectId userId, Song song) {
		SongFeedback songFeedback = new SongFeedback(FeedbackType.STAR, userId);
		return ds.find(StationHistoryEntry.class).filter("song", song)
		// .filter("songFeedback elem", songFeedback).get();
				.field("songFeedback").hasThisElement(songFeedback).get();
	}

	private Query<StationHistoryEntry> order(Query<StationHistoryEntry> query) {
		return query.order("-id");
	}

	private Query<StationHistoryEntry> orderReverse(
			Query<StationHistoryEntry> query) {
		return query.order("id");
	}

	private Query<StationHistoryEntry> queryByStationId(ObjectId stationId) {
		return ds.find(StationHistoryEntry.class)
				.filter("stationId", stationId);
	}

	private Query<StationHistoryEntry> queryByStationIdAndArtist(
			ObjectId stationId, String artistName) {
		return queryByStationId(stationId).filter("song.artistsNames",
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

	private Query<StationHistoryEntry> filterHourOffset(
			Query<StationHistoryEntry> query, int hourOffset) {

		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.HOUR, -hourOffset);
		long hourOffsetMillis = calendar.getTimeInMillis();

		// return query.filter("timestamp >", hourOffsetMillis);
		return query.field("timestamp").greaterThan(hourOffsetMillis);
	}

	private Query<StationHistoryEntry> filterSince(
			Query<StationHistoryEntry> query, ObjectId sinceObjectId,
			boolean inclusive) {

		if (inclusive) {
			return query.field("id").greaterThanOrEq(sinceObjectId);
		} else {
			return query.field("id").greaterThan(sinceObjectId);
		}
	}

	private Query<StationHistoryEntry> filterUntil(
			Query<StationHistoryEntry> query, ObjectId untilObjectId,
			boolean inclusive) {

		if (inclusive) {
			return query.field("id").lessThanOrEq(untilObjectId);
		} else {
			return query.field("id").lessThan(untilObjectId);
		}
	}
}
