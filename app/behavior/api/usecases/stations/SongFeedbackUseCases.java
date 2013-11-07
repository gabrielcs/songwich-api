package behavior.api.usecases.stations;

import java.util.List;

import models.api.stations.SongFeedback;
import models.api.stations.SongFeedback.FeedbackType;
import models.api.stations.StationHistoryEntry;

import org.bson.types.ObjectId;

import util.api.MyLogger;
import util.api.SongwichAPIException;
import views.api.APIStatus_V0_4;
import views.api.stations.SongDTO_V0_4;
import views.api.stations.SongFeedbackDTO_V0_4;
import views.api.stations.StarredSongSetDTO_V0_4;
import behavior.api.usecases.RequestContext;
import behavior.api.usecases.UseCase;
import database.api.stations.StationHistoryDAO;
import database.api.stations.StationHistoryDAOMongo;

public class SongFeedbackUseCases extends UseCase {

	public SongFeedbackUseCases(RequestContext context) {
		super(context);
	}

	public void postSongFeedback(SongFeedbackDTO_V0_4 songFeedbackDTO)
			throws SongwichAPIException {

		StationHistoryDAO<ObjectId> stationHistoryDAO = new StationHistoryDAOMongo();
		StationHistoryEntry stationHistoryEntry = stationHistoryDAO
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
		stationHistoryDAO.save(stationHistoryEntry, getContext()
				.getAppDeveloper().getEmailAddress());

		// update DTO
		updateDTOForPostSongFeedback(songFeedbackDTO, stationHistoryEntry,
				getContext().getUser().getId().toString());
	}

	public StarredSongSetDTO_V0_4 getStarredSongs(String userId)
			throws SongwichAPIException {

		authorizeGetStarredSongs(userId);

		StationHistoryDAO<ObjectId> stationHistoryDAO = new StationHistoryDAOMongo();
		List<StationHistoryEntry> stationHistoryEntries = stationHistoryDAO
				.findStarredByUserId(new ObjectId(userId));
		MyLogger.debug("stationHistoryEntries: " + stationHistoryEntries);

		return createDTOForGetStarredSongs(stationHistoryEntries, userId);
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
		StationHistoryDAO<ObjectId> stationHistoryDAO = new StationHistoryDAOMongo();
		stationHistoryDAO.save(stationHistoryEntry, getContext()
				.getAppDeveloper().getEmailAddress());
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

		StationHistoryDAO<ObjectId> stationHistoryDAO = new StationHistoryDAOMongo();
		StationHistoryEntry stationHistoryEntry = stationHistoryDAO
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

		SongDTO_V0_4 songDTO;
		for (StationHistoryEntry stationHistoryEntry : stationHistoryEntries) {
			songDTO = new SongDTO_V0_4();
			songDTO.setTrackTitle(stationHistoryEntry.getSong().getSongTitle());
			songDTO.setArtistsNames(stationHistoryEntry.getSong()
					.getArtistsNames());
			starredSongList.add(songDTO);
		}
		return starredSongList;
	}
}
