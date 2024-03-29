package behavior.api.usecases.stations;

import java.util.ArrayList;
import java.util.List;

import models.api.scrobbles.Song;
import models.api.stations.SongFeedback;
import models.api.stations.SongFeedback.FeedbackType;
import models.api.stations.StationHistoryEntry;

import org.bson.types.ObjectId;

import util.api.SongwichAPIException;
import views.api.APIStatus_V0_4;
import views.api.stations.IsSongStarredDTO_V0_4;
import views.api.stations.SongDTO_V0_4;
import views.api.stations.SongFeedbackDTO_V0_4;
import views.api.stations.StarredSongSetDTO_V0_4;
import views.api.stations.TrackDTO_V0_4;
import behavior.api.usecases.RequestContext;
import behavior.api.usecases.UseCase;

public class SongFeedbackUseCases extends UseCase {

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
		FeedbackType feedbackType;
		if (songFeedbackDTO.getFeedbackType().equals("thumbs-up")) {
			feedbackType = FeedbackType.THUMBS_UP;
		} else if (songFeedbackDTO.getFeedbackType().equals("thumbs-down")) {
			feedbackType = FeedbackType.THUMBS_DOWN;
		} else if (songFeedbackDTO.getFeedbackType().equals("star")) {
			feedbackType = FeedbackType.STAR;
		} else {
			throw new SongwichAPIException("Invalid feedbackType",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

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

	public StarredSongSetDTO_V0_4 getStarredSongs(String userId)
			throws SongwichAPIException {

		authorizeGetStarredSongs(userId);

		List<StationHistoryEntry> stationHistoryEntries = getStationHistoryDAO()
				.findStarredByUserId(new ObjectId(userId));

		return createDTOForGetStarredSongs(stationHistoryEntries, userId);
	}

	public IsSongStarredDTO_V0_4 getIsSongStarred(String userId,
			String songTitle, String artistsNames, String albumTitle)
			throws SongwichAPIException {

		authorizeIsSongStarred(userId, songTitle, artistsNames);

		Song song = new Song(songTitle, albumTitle, splitArtistsNames(artistsNames));
		StationHistoryEntry stationHistoryEntry = getStationHistoryDAO()
				.isSongStarredByUser(new ObjectId(userId), song);

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
		FeedbackType feedbackTypeEnum;

		switch (feedbackType) {
		case "thumbs-up":
			feedbackTypeEnum = FeedbackType.THUMBS_UP;
			break;
		case "thumbs-down":
			feedbackTypeEnum = FeedbackType.THUMBS_DOWN;
			break;
		case "star":
			feedbackTypeEnum = FeedbackType.STAR;
			break;
		default:
			throw new SongwichAPIException("Invalid feedbackType",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		return new SongFeedback(feedbackTypeEnum, getContext().getUser()
				.getId());
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

	private void authorizeGetStarredSongs(String userId)
			throws SongwichAPIException {
		if (!ObjectId.isValid(userId)) {
			throw new SongwichAPIException("Invalid userId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}
	}

	private void authorizeIsSongStarred(String userId, String songTitle,
			String artistsNames) throws SongwichAPIException {

		if (!ObjectId.isValid(userId)) {
			throw new SongwichAPIException("Invalid userId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		if (songTitle.isEmpty()) {
			throw new SongwichAPIException("Empty songTitle",
					APIStatus_V0_4.INVALID_PARAMETER);
		} else if (artistsNames.isEmpty()) {
			throw new SongwichAPIException("Empty artistsNames",
					APIStatus_V0_4.INVALID_PARAMETER);
		}
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
			StationHistoryEntry stationHistoryEntry, String userId,
			Song song) {
		
		SongDTO_V0_4 songDTO = new SongDTO_V0_4();
		songDTO.setTrackTitle(song.getSongTitle());
		songDTO.setArtistsNames(song.getArtistsNames());
		songDTO.setAlbumTitle(song.getAlbumTitle());

		IsSongStarredDTO_V0_4 isSongStarredDTO = new IsSongStarredDTO_V0_4();
		isSongStarredDTO.setUserId(userId);
		isSongStarredDTO.setSong(songDTO);
		if (stationHistoryEntry != null) {
			isSongStarredDTO.setIsStarred(String.valueOf(true));
			isSongStarredDTO.setIdForFeedback(stationHistoryEntry.getId().toString());
		} else {
			isSongStarredDTO.setIsStarred(String.valueOf(false));
		}
		
		return isSongStarredDTO;
	}
}
