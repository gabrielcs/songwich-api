package behavior.api.usecases.stations;

import java.util.ArrayList;
import java.util.List;

import models.api.scrobbles.Song;
import models.api.scrobbles.User;
import models.api.stations.SongFeedback;
import models.api.stations.SongFeedback.FeedbackType;
import models.api.stations.StationHistoryEntry;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.types.ObjectId;

import play.api.Play;
import util.api.MyLogger;
import util.api.SongwichAPIException;
import views.api.APIStatus_V0_4;
import views.api.stations.IsSongStarredDTO_V0_4;
import views.api.stations.SongDTO_V0_4;
import views.api.stations.SongFeedbackDTO_V0_4;
import views.api.stations.StarredSongSetDTO_V0_4;
import views.api.stations.StarredSongsPagingDTO_V0_4;
import views.api.stations.TrackDTO_V0_4;
import behavior.api.usecases.PagingHelper_V0_4;
import behavior.api.usecases.PagingNotAvailableException;
import behavior.api.usecases.RequestContext;
import behavior.api.usecases.UseCase;

public class SongFeedbackUseCases extends UseCase {

	// TODO: move this somewhere else?
	private static final int GET_STARRED_SONGS_MAX_RESULTS = (Integer) Play
			.current().configuration().getInt("get.starred.songs.max").get();

	public SongFeedbackUseCases(RequestContext context) {
		super(context);
	}

	public void postSongFeedback(SongFeedbackDTO_V0_4 songFeedbackDTO)
			throws SongwichAPIException {

		StationHistoryEntry stationHistoryEntry = getStationHistoryDAO()
				.findById(new ObjectId(songFeedbackDTO.getIdForFeedback()));

		if (stationHistoryEntry == null) {
			throw new SongwichAPIException("Invalid idForFeedback",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		// process request
		FeedbackType feedbackType = getFeedbackFromString(songFeedbackDTO
				.getFeedbackType());

		// set it and save it
		SongFeedback songFeedback = new SongFeedback(feedbackType, getContext()
				.getUser().getId());
		stationHistoryEntry.addSongFeedback(songFeedback);
		getStationHistoryDAO().save(stationHistoryEntry,
				getContext().getAppDeveloper().getEmailAddress());

		// update DTO
		updateDTOForPostSongFeedback(songFeedbackDTO, stationHistoryEntry,
				getContext().getUser().getId().toString());
	}

	public Pair<StarredSongSetDTO_V0_4, StarredSongsPagingDTO_V0_4> getStarredSongs(
			String hostUrl, String userIdString, Integer maxResults)
			throws SongwichAPIException {

		User user = authorizeGetStarredSongs(userIdString, maxResults);

		List<StationHistoryEntry> stationHistoryEntries = getStationHistoryDAO()
				.findStarredByUserId(user.getId(), maxResults);

		// try to set paging
		StarredSongsPagingDTO_V0_4 pagingDTO = null;
		try {
			String apiMethodUrl = "starredSongs/" + userIdString;
			pagingDTO = new StarredSongsPagingDTO_V0_4(
					PagingHelper_V0_4.getPagingUrlManager(hostUrl,
							apiMethodUrl, null, stationHistoryEntries,
							maxResults, PagingHelper_V0_4.Mode.OPEN));
		} catch (PagingNotAvailableException exception) {
			// normal behavior in case there were no results
		}

		Pair<StarredSongSetDTO_V0_4, StarredSongsPagingDTO_V0_4> resultPair = new ImmutablePair<StarredSongSetDTO_V0_4, StarredSongsPagingDTO_V0_4>(
				createDTOForGetStarredSongs(stationHistoryEntries, userIdString),
				pagingDTO);

		return resultPair;
	}

	public Pair<StarredSongSetDTO_V0_4, StarredSongsPagingDTO_V0_4> getStarredSongsSince(
			String hostUrl, String userIdString, String sinceObjectIdString,
			boolean inclusive, int maxResults) throws SongwichAPIException {

		Pair<User, StationHistoryEntry> userIdHistoryEntryPair = authorizeGetStarredSongs(
				userIdString, maxResults, sinceObjectIdString);
		User user = userIdHistoryEntryPair.getLeft();
		StationHistoryEntry sinceHistoryEntry = userIdHistoryEntryPair
				.getRight();

		List<StationHistoryEntry> stationHistoryEntries = getStationHistoryDAO()
				.findStarredByUserIdSince(user.getId(),
						sinceHistoryEntry.getId(), inclusive, maxResults);

		// try to set paging
		StarredSongsPagingDTO_V0_4 pagingDTO = null;
		try {
			String apiMethodUrl = "starredSongs/" + userIdString;
			pagingDTO = new StarredSongsPagingDTO_V0_4(
					PagingHelper_V0_4.getPagingUrlManager(hostUrl,
							apiMethodUrl, sinceHistoryEntry.getId().toString(),
							stationHistoryEntries, maxResults,
							PagingHelper_V0_4.Mode.SINCE));
		} catch (PagingNotAvailableException exception) {
			// shouldn't reach here
			MyLogger.warn(exception.toString());
		}

		Pair<StarredSongSetDTO_V0_4, StarredSongsPagingDTO_V0_4> resultPair = new ImmutablePair<StarredSongSetDTO_V0_4, StarredSongsPagingDTO_V0_4>(
				createDTOForGetStarredSongs(stationHistoryEntries, userIdString),
				pagingDTO);

		return resultPair;
	}

	public Pair<StarredSongSetDTO_V0_4, StarredSongsPagingDTO_V0_4> getStarredSongsUntil(
			String hostUrl, String userIdString, String untilObjectIdString,
			boolean inclusive, int maxResults) throws SongwichAPIException {

		Pair<User, StationHistoryEntry> userIdHistoryEntryPair = authorizeGetStarredSongs(
				userIdString, maxResults, untilObjectIdString);
		User user = userIdHistoryEntryPair.getLeft();
		StationHistoryEntry untilHistoryEntry = userIdHistoryEntryPair
				.getRight();

		List<StationHistoryEntry> stationHistoryEntries = getStationHistoryDAO()
				.findStarredByUserIdUntil(user.getId(),
						untilHistoryEntry.getId(), inclusive, maxResults);

		// try to set paging
		StarredSongsPagingDTO_V0_4 pagingDTO = null;
		try {
			String apiMethodUrl = "starredSongs/" + userIdString;
			pagingDTO = new StarredSongsPagingDTO_V0_4(
					PagingHelper_V0_4.getPagingUrlManager(hostUrl,
							apiMethodUrl, untilHistoryEntry.getId().toString(),
							stationHistoryEntries, maxResults,
							PagingHelper_V0_4.Mode.UNTIL));
		} catch (PagingNotAvailableException exception) {
			// shouldn't reach here
			MyLogger.warn(exception.toString());
		}

		Pair<StarredSongSetDTO_V0_4, StarredSongsPagingDTO_V0_4> resultPair = new ImmutablePair<StarredSongSetDTO_V0_4, StarredSongsPagingDTO_V0_4>(
				createDTOForGetStarredSongs(stationHistoryEntries, userIdString),
				pagingDTO);

		return resultPair;
	}

	public IsSongStarredDTO_V0_4 getIsSongStarred(String userId,
			String songTitle, String artistsNames, String albumTitle)
			throws SongwichAPIException {

		User user = authorizeIsSongStarred(userId, songTitle, artistsNames);

		Song song = new Song(songTitle, albumTitle,
				splitArtistsNames(artistsNames));
		StationHistoryEntry stationHistoryEntry = getStationHistoryDAO()
				.isSongStarredByUser(user.getId(), song);

		return createDTOForIsSongStarred(stationHistoryEntry, userId, song);
	}

	private List<String> splitArtistsNames(String artistsNames) {
		String[] artistsNamesArray = artistsNames.split(",");
		List<String> result = new ArrayList<String>(artistsNamesArray.length);
		for (String artistName : artistsNamesArray) {
			result.add(artistName.trim());
		}
		System.out.println(result);
		return result;
	}

	public void deleteSongFeedback(String idForFeedback, String feedbackType)
			throws SongwichAPIException {

		SongFeedback songFeedback = buildSongFeedback(feedbackType);
		// authorize
		StationHistoryEntry stationHistoryEntry = authorizeForDeleteSongFeedback(
				idForFeedback, songFeedback);
		// process request
		stationHistoryEntry.removeSongFeedback(songFeedback);
		// save
		getStationHistoryDAO().save(stationHistoryEntry,
				getContext().getAppDeveloper().getEmailAddress());
	}

	private SongFeedback buildSongFeedback(String feedbackType)
			throws SongwichAPIException {
		FeedbackType feedbackTypeEnum = getFeedbackFromString(feedbackType);

		return new SongFeedback(feedbackTypeEnum, getContext().getUser()
				.getId());
	}

	private FeedbackType getFeedbackFromString(String feedbackTypeString)
			throws SongwichAPIException {

		switch (feedbackTypeString) {
		case "skip":
			return FeedbackType.SKIP;
		case "thumbs-up":
			return FeedbackType.THUMBS_UP;
		case "thumbs-down":
			return FeedbackType.THUMBS_DOWN;
		case "star":
			return FeedbackType.STAR;
		default:
			throw new SongwichAPIException("Invalid feedbackType",
					APIStatus_V0_4.INVALID_PARAMETER);
		}
	}

	private StationHistoryEntry authorizeForDeleteSongFeedback(
			String idForFeedback, SongFeedback songFeedback)
			throws SongwichAPIException {

		if (!ObjectId.isValid(idForFeedback)) {
			throw new SongwichAPIException("Invalid idForFeedback",
					APIStatus_V0_4.INVALID_PARAMETER);
		}
		ObjectId idForFeedbackObject = new ObjectId(idForFeedback);

		StationHistoryEntry stationHistoryEntry = getStationHistoryDAO()
				.findById(idForFeedbackObject);
		if (stationHistoryEntry == null) {
			throw new SongwichAPIException("Non-existent idForFeedback",
					APIStatus_V0_4.INVALID_PARAMETER);
		}
		if (!stationHistoryEntry.getSongFeedback().contains(songFeedback)) {
			throw new SongwichAPIException("Non-existent song feedback",
					APIStatus_V0_4.BAD_REQUEST);
		}

		return stationHistoryEntry;
	}

	private User authorizeGetStarredSongs(String userId, int results)
			throws SongwichAPIException {

		if (getContext().getUser() == null) {
			throw new SongwichAPIException(
					APIStatus_V0_4.UNAUTHORIZED.toString(),
					APIStatus_V0_4.UNAUTHORIZED);
		}

		if (!ObjectId.isValid(userId)) {
			throw new SongwichAPIException("Invalid userId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		// check if the User the scrobbles were asked for is the same as the
		// authenticated one
		User databaseUser = getUserDAO().findById(new ObjectId(userId));

		if (databaseUser == null) {
			throw new SongwichAPIException("Invalid userId: "
					+ userId.toString(), APIStatus_V0_4.INVALID_PARAMETER);
		}

		if (!databaseUser.equals(getContext().getUser())) {
			throw new SongwichAPIException(
					APIStatus_V0_4.UNAUTHORIZED.toString(),
					APIStatus_V0_4.UNAUTHORIZED);
		}

		if (results > GET_STARRED_SONGS_MAX_RESULTS || results < 1) {
			throw new SongwichAPIException(String.format(
					"results should be between %d and %d", 1,
					GET_STARRED_SONGS_MAX_RESULTS),
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		return databaseUser;
	}

	private Pair<User, StationHistoryEntry> authorizeGetStarredSongs(
			String userId, int results, String sinceUntilObjectId)
			throws SongwichAPIException {

		if (!ObjectId.isValid(sinceUntilObjectId)) {
			throw new SongwichAPIException(String.format(
					"Invalid scrobble id [%s]", sinceUntilObjectId),
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		StationHistoryEntry historyEntry = getStationHistoryDAO().findById(
				new ObjectId(sinceUntilObjectId));

		if (historyEntry == null) {
			throw new SongwichAPIException(String.format(
					"Non-existent feedbackId [%s]", sinceUntilObjectId),
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		Pair<User, StationHistoryEntry> userIdHistoryEntryPair = new ImmutablePair<User, StationHistoryEntry>(
				authorizeGetStarredSongs(userId, results), historyEntry);

		return userIdHistoryEntryPair;
	}

	private User authorizeIsSongStarred(String userId, String songTitle,
			String artistsNames) throws SongwichAPIException {

		if (getContext().getUser() == null) {
			throw new SongwichAPIException(
					APIStatus_V0_4.UNAUTHORIZED.toString(),
					APIStatus_V0_4.UNAUTHORIZED);
		}

		if (!ObjectId.isValid(userId)) {
			throw new SongwichAPIException("Invalid userId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		// check if the User the scrobbles were asked for is the same as the
		// authenticated one
		User databaseUser = getUserDAO().findById(new ObjectId(userId));

		if (databaseUser == null) {
			throw new SongwichAPIException("Invalid userId: "
					+ userId.toString(), APIStatus_V0_4.INVALID_PARAMETER);
		}

		if (!databaseUser.equals(getContext().getUser())) {
			throw new SongwichAPIException(
					APIStatus_V0_4.UNAUTHORIZED.toString(),
					APIStatus_V0_4.UNAUTHORIZED);
		}

		if (songTitle.isEmpty()) {
			throw new SongwichAPIException("Empty songTitle",
					APIStatus_V0_4.INVALID_PARAMETER);
		} else if (artistsNames.isEmpty()) {
			throw new SongwichAPIException("Empty artistsNames",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		return databaseUser;
	}

	private static void updateDTOForPostSongFeedback(
			SongFeedbackDTO_V0_4 songFeedbackDTO,
			StationHistoryEntry stationHistoryEntry, String userId) {

		songFeedbackDTO.setUserId(userId);
		SongDTO_V0_4 songDTO = new SongDTO_V0_4();
		songDTO.setTrackTitle(stationHistoryEntry.getSong().getSongTitle());
		songDTO.setArtistsNames(stationHistoryEntry.getSong().getArtistsNames());
		songFeedbackDTO.setSong(songDTO);
	}

	private static StarredSongSetDTO_V0_4 createDTOForGetStarredSongs(
			List<StationHistoryEntry> stationHistoryEntries, String userId) {
		StarredSongSetDTO_V0_4 starredSongList = new StarredSongSetDTO_V0_4();
		starredSongList.setUserId(userId);

		TrackDTO_V0_4 songListEntryDTO;
		for (StationHistoryEntry stationHistoryEntry : stationHistoryEntries) {
			songListEntryDTO = new TrackDTO_V0_4();
			songListEntryDTO.setTrackTitle(stationHistoryEntry.getSong()
					.getSongTitle());
			songListEntryDTO.setArtistsNames(stationHistoryEntry.getSong()
					.getArtistsNames());
			songListEntryDTO.setIdForFeedback(stationHistoryEntry.getId()
					.toString());
			starredSongList.add(songListEntryDTO);
		}
		return starredSongList;
	}

	private static IsSongStarredDTO_V0_4 createDTOForIsSongStarred(
			StationHistoryEntry stationHistoryEntry, String userId, Song song) {

		SongDTO_V0_4 songDTO = new SongDTO_V0_4();
		songDTO.setTrackTitle(song.getSongTitle());
		songDTO.setArtistsNames(song.getArtistsNames());
		songDTO.setAlbumTitle(song.getAlbumTitle());

		IsSongStarredDTO_V0_4 isSongStarredDTO = new IsSongStarredDTO_V0_4();
		isSongStarredDTO.setUserId(userId);
		isSongStarredDTO.setSong(songDTO);
		if (stationHistoryEntry != null) {
			isSongStarredDTO.setIsStarred(String.valueOf(true));
			isSongStarredDTO.setIdForFeedback(stationHistoryEntry.getId()
					.toString());
		} else {
			isSongStarredDTO.setIsStarred(String.valueOf(false));
		}

		return isSongStarredDTO;
	}
}
