package behavior.api.usecases.scrobbles;

import java.util.ArrayList;
import java.util.List;

import models.api.scrobbles.Scrobble;
import models.api.scrobbles.Song;
import models.api.scrobbles.User;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.types.ObjectId;

import play.api.Play;
import util.api.MyLogger;
import util.api.SongwichAPIException;
import views.api.APIStatus_V0_4;
import views.api.PagingNotAvailableException;
import views.api.scrobbles.ScrobblesDTO_V0_4;
import views.api.scrobbles.ScrobblesPagingDTO_V0_4;
import views.api.scrobbles.ScrobblesUpdateDTO_V0_4;
import behavior.api.usecases.RequestContext;
import behavior.api.usecases.UseCase;

public class ScrobblesUseCases extends UseCase {

	private static final int GET_SCROBBLES_MAX_RESULTS = (Integer) Play
			.current().configuration().getInt("get.scrobbles.max").get();

	public ScrobblesUseCases(RequestContext context) {
		super(context);
	}

	public void postScrobbles(ScrobblesDTO_V0_4 scrobbleDTO)
			throws SongwichAPIException {

		if (getContext().getUser() == null) {
			throw new SongwichAPIException("Missing X-Songwich.userAuthToken",
					APIStatus_V0_4.INVALID_USER_AUTH_TOKEN);
		}

		Song song = new Song(scrobbleDTO.getTrackTitle(),
				scrobbleDTO.getAlbumTitle(), scrobbleDTO.getArtistsNames());
		Scrobble scrobble = new Scrobble(getContext().getUser().getId(), song,
				Long.parseLong(scrobbleDTO.getTimestamp()),
				Boolean.parseBoolean(scrobbleDTO.getChosenByUser()),
				scrobbleDTO.getPlayer());

		getScrobbleDAO().save(scrobble,
				getContext().getAppDeveloper().getEmailAddress());

		// updates scrobbleDTO
		scrobbleDTO.setUserId(getContext().getUser().getId().toString());
		scrobbleDTO.setScrobbleId(scrobble.getId().toString());
	}

	public Pair<List<ScrobblesDTO_V0_4>, ScrobblesPagingDTO_V0_4> getScrobbles(
			String hostUrl, String userId, int maxResults,
			boolean chosenByUserOnly) throws SongwichAPIException {

		ObjectId userIdObject = authorizeUserGetScrobbles(maxResults, userId);
		List<Scrobble> scrobbles = getScrobbleDAO()
				.findLatestScrobblesByUserId(userIdObject, maxResults,
						chosenByUserOnly);

		// try to set paging
		ScrobblesPagingDTO_V0_4 paginationDTO = null;
		try {
			paginationDTO = new ScrobblesPagingDTO_V0_4(hostUrl, userId, null,
					scrobbles, maxResults, ScrobblesPagingDTO_V0_4.MODE.OPEN,
					chosenByUserOnly);
		} catch (PagingNotAvailableException exception) {
			// normal behavior in case there were no results
		}

		Pair<List<ScrobblesDTO_V0_4>, ScrobblesPagingDTO_V0_4> resultPair = new ImmutablePair<List<ScrobblesDTO_V0_4>, ScrobblesPagingDTO_V0_4>(
				createGetScrobblesResponse(scrobbles), paginationDTO);
		return resultPair;
	}

	public Pair<List<ScrobblesDTO_V0_4>, ScrobblesPagingDTO_V0_4> getScrobblesSince(
			String hostUrl, String userId, String sinceObjectIdString,
			boolean inclusive, int maxResults, boolean chosenByUserOnly)
			throws SongwichAPIException {

		Pair<ObjectId, Scrobble> userIdScrobblePair = authorizeUserGetScrobbles(
				maxResults, userId, sinceObjectIdString);
		Scrobble scrobble = userIdScrobblePair.getRight();
		List<Scrobble> scrobbles = getScrobbleDAO().findScrobblesByUserIdSince(
				userIdScrobblePair.getLeft(), scrobble.getTimestamp(),
				scrobble.getId(), inclusive, maxResults, chosenByUserOnly);

		// try to set paging
		ScrobblesPagingDTO_V0_4 paginationDTO = null;
		try {
			paginationDTO = new ScrobblesPagingDTO_V0_4(hostUrl, userId,
					sinceObjectIdString, scrobbles, maxResults,
					ScrobblesPagingDTO_V0_4.MODE.SINCE, chosenByUserOnly);
		} catch (PagingNotAvailableException exception) {
			// shouldn't reach here
			MyLogger.warn(exception.toString());
		}

		Pair<List<ScrobblesDTO_V0_4>, ScrobblesPagingDTO_V0_4> resultPair = new ImmutablePair<List<ScrobblesDTO_V0_4>, ScrobblesPagingDTO_V0_4>(
				createGetScrobblesResponse(scrobbles), paginationDTO);

		return resultPair;
	}

	public Pair<List<ScrobblesDTO_V0_4>, ScrobblesPagingDTO_V0_4> getScrobblesUntil(
			String hostUrl, String userId, String untilObjectIdString,
			boolean inclusive, int maxResults, boolean chosenByUserOnly)
			throws SongwichAPIException {

		Pair<ObjectId, Scrobble> userIdScrobblePair = authorizeUserGetScrobbles(
				maxResults, userId, untilObjectIdString);
		Scrobble scrobble = userIdScrobblePair.getRight();
		List<Scrobble> scrobbles = getScrobbleDAO().findScrobblesByUserIdUntil(
				userIdScrobblePair.getLeft(), scrobble.getTimestamp(),
				scrobble.getId(), inclusive, maxResults, chosenByUserOnly);

		// try to set paging
		ScrobblesPagingDTO_V0_4 paginationDTO = null;
		try {
			paginationDTO = new ScrobblesPagingDTO_V0_4(hostUrl, userId,
					untilObjectIdString, scrobbles, maxResults,
					ScrobblesPagingDTO_V0_4.MODE.UNTIL, chosenByUserOnly);
		} catch (PagingNotAvailableException exception) {
			// shouldn't reach here
			MyLogger.warn(exception.toString());
		}

		Pair<List<ScrobblesDTO_V0_4>, ScrobblesPagingDTO_V0_4> resultPair = new ImmutablePair<List<ScrobblesDTO_V0_4>, ScrobblesPagingDTO_V0_4>(
				createGetScrobblesResponse(scrobbles), paginationDTO);

		return resultPair;
	}

	public void deleteScrobbles(String scrobbleId) throws SongwichAPIException {
		ObjectId scrobbleIdObject = authorizeDeleteScrobbles(scrobbleId);
		getScrobbleDAO().deleteById(scrobbleIdObject);
	}

	public void putScrobbles(String scrobbleId,
			ScrobblesUpdateDTO_V0_4 scrobblesUpdateDTO)
			throws SongwichAPIException {
		Scrobble scrobble = authorizePutScrobbles(scrobbleId,
				scrobblesUpdateDTO);

		// process the request
		if (scrobblesUpdateDTO.getChosenByUser() == null
				|| (!scrobblesUpdateDTO.getChosenByUser().equalsIgnoreCase(
						"true") && !scrobblesUpdateDTO.getChosenByUser()
						.equalsIgnoreCase("false"))) {

			throw new SongwichAPIException(
					"chosenByUser should be either true or false",
					APIStatus_V0_4.INVALID_PARAMETER);
		} else {
			scrobble.setChosenByUser(new Boolean(scrobblesUpdateDTO
					.getChosenByUser()));
		}

		getScrobbleDAO().save(scrobble,
				getContext().getAppDeveloper().getEmailAddress());

		// update output
		updateDTOPutScrobbles(scrobble, scrobblesUpdateDTO);
	}

	private Scrobble authorizePutScrobbles(String scrobbleId,
			ScrobblesUpdateDTO_V0_4 scrobblesUpdateDTO)
			throws SongwichAPIException {

		if (!ObjectId.isValid(scrobbleId)) {
			throw new SongwichAPIException("Invalid scrobbleId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		Scrobble scrobble = getScrobbleDAO().findById(new ObjectId(scrobbleId));
		if (scrobble == null) {
			throw new SongwichAPIException("Non-existent scrobbleId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		authenticatePutScrobbles(scrobble, scrobblesUpdateDTO);
		return scrobble;
	}

	private void authenticatePutScrobbles(Scrobble scrobble,
			ScrobblesUpdateDTO_V0_4 scrobblesUpdateDTO)
			throws SongwichAPIException {

		if (getContext().getUser() == null) {
			throw new SongwichAPIException("Missing X-Songwich.userAuthToken",
					APIStatus_V0_4.UNAUTHORIZED);
		}

		// check if the user is the one who made the scrobble
		if (!scrobble.getUserId().equals(getContext().getUser().getId())) {
			throw new SongwichAPIException(
					APIStatus_V0_4.UNAUTHORIZED.toString(),
					APIStatus_V0_4.UNAUTHORIZED);
		}
	}

	private ObjectId authorizeUserGetScrobbles(Integer results, String userId)
			throws SongwichAPIException {

		if (results != null
				&& (results > GET_SCROBBLES_MAX_RESULTS || results < 1)) {
			throw new SongwichAPIException(String.format(
					"results should be between %d and %d", 1,
					GET_SCROBBLES_MAX_RESULTS),
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		if (getContext().getUser() == null) {
			throw new SongwichAPIException(
					APIStatus_V0_4.UNAUTHORIZED.toString(),
					APIStatus_V0_4.UNAUTHORIZED);
		}

		if (!ObjectId.isValid(userId)) {
			throw new SongwichAPIException("Invalid userId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}
		ObjectId userIdObject = new ObjectId(userId);

		// check if the User the scrobbles were asked for is the same as the
		// authenticated one
		User databaseUser = getUserDAO().findById(userIdObject);

		if (databaseUser == null) {
			throw new SongwichAPIException("Invalid userId: "
					+ userId.toString(), APIStatus_V0_4.INVALID_PARAMETER);
		}

		if (!databaseUser.equals(getContext().getUser())) {
			throw new SongwichAPIException(
					APIStatus_V0_4.UNAUTHORIZED.toString(),
					APIStatus_V0_4.UNAUTHORIZED);
		}

		// authorized
		return userIdObject;
	}

	private Pair<ObjectId, Scrobble> authorizeUserGetScrobbles(Integer results,
			String userId, String sinceUntilObjectId)
			throws SongwichAPIException {

		if (!ObjectId.isValid(sinceUntilObjectId)) {
			throw new SongwichAPIException(String.format(
					"Invalid scrobble id [%s]", sinceUntilObjectId),
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		Scrobble scrobble = getScrobbleDAO().findById(
				new ObjectId(sinceUntilObjectId));

		if (scrobble == null) {
			throw new SongwichAPIException(String.format(
					"Non-existent scrobble id [%s]", sinceUntilObjectId),
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		Pair<ObjectId, Scrobble> userIdScrobblePair = new ImmutablePair<ObjectId, Scrobble>(
				authorizeUserGetScrobbles(results, userId), scrobble);

		return userIdScrobblePair;
	}

	private ObjectId authorizeDeleteScrobbles(String scrobbleId)
			throws SongwichAPIException {

		if (!ObjectId.isValid(scrobbleId)) {
			throw new SongwichAPIException("Invalid scrobbleId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}
		ObjectId scrobbleIdObject = new ObjectId(scrobbleId);

		Scrobble scrobble = getScrobbleDAO().findById(scrobbleIdObject);
		if (scrobble == null) {
			throw new SongwichAPIException("Invalid scrobbleId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		// check if the User the scrobbles were asked for is the same as the
		// authenticated one
		if (!scrobble.getUserId().equals(getContext().getUser().getId())) {
			throw new SongwichAPIException(
					APIStatus_V0_4.UNAUTHORIZED.toString(),
					APIStatus_V0_4.UNAUTHORIZED);
		}

		return scrobbleIdObject;
	}

	private static void updateDTOPutScrobbles(Scrobble scrobble,
			ScrobblesUpdateDTO_V0_4 scrobblesUpdateDTO) {

		scrobblesUpdateDTO.setScrobbleId(scrobble.getId().toString());
		scrobblesUpdateDTO.setUserId(scrobble.getUserId().toString());
		scrobblesUpdateDTO.setTrackTitle(scrobble.getSong().getSongTitle());
		scrobblesUpdateDTO
				.setArtistsNames(scrobble.getSong().getArtistsNames());
		scrobblesUpdateDTO.setAlbumTitle(scrobble.getSong().getAlbumTitle());
		scrobblesUpdateDTO
				.setChosenByUser(scrobble.isChosenByUser().toString());
		scrobblesUpdateDTO.setPlayer(scrobble.getPlayer());
		scrobblesUpdateDTO.setTimestamp(scrobble.getTimestamp().toString());
	}

	private static List<ScrobblesDTO_V0_4> createGetScrobblesResponse(
			List<Scrobble> scrobbles) {
		List<ScrobblesDTO_V0_4> scrobbleDTOs = new ArrayList<ScrobblesDTO_V0_4>(
				scrobbles.size());
		ScrobblesDTO_V0_4 scrobblesDTO;
		for (Scrobble scrobble : scrobbles) {
			scrobblesDTO = new ScrobblesDTO_V0_4();
			scrobblesDTO.setScrobbleId(scrobble.getId().toString());
			scrobblesDTO.setTrackTitle(scrobble.getSong().getSongTitle());
			scrobblesDTO.setArtistsNames(scrobble.getSong().getArtistsNames());
			scrobblesDTO.setAlbumTitle(scrobble.getSong().getAlbumTitle());
			scrobblesDTO.setTimestamp(Long.toString(scrobble.getTimestamp()));
			scrobblesDTO.setChosenByUser(Boolean.toString(scrobble
					.isChosenByUser()));
			scrobblesDTO.setPlayer(scrobble.getPlayer());

			scrobbleDTOs.add(scrobblesDTO);
		}
		return scrobbleDTOs;
	}
}
