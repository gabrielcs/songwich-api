package behavior.api.usecases.scrobbles;

import java.util.ArrayList;
import java.util.List;

import models.api.scrobbles.Scrobble;
import models.api.scrobbles.Song;
import models.api.scrobbles.User;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.types.ObjectId;

import util.api.SongwichAPIException;
import views.api.APIStatus_V0_4;
import views.api.scrobbles.ScrobblesDTO_V0_4;
import views.api.scrobbles.ScrobblesPagingDTO_V0_4;
import views.api.scrobbles.ScrobblesUpdateDTO_V0_4;
import behavior.api.usecases.RequestContext;
import behavior.api.usecases.UseCase;

public class ScrobblesUseCases extends UseCase {

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
			String hostUrl, String userId, Integer results,
			Boolean chosenByUserOnly, Long requestTimestamp)
			throws SongwichAPIException {

		ObjectId userIdObject = authorizeUserGetScrobbles(results, userId);
		List<Scrobble> scrobbles = getScrobbleDAO()
				.findLatestScrobblesByUserId(userIdObject, results,
						chosenByUserOnly);
		Long since, until;
		if (scrobbles != null && !scrobbles.isEmpty()) {
			since = scrobbles.get(scrobbles.size() - 1).getTimestamp();
			until = scrobbles.get(0).getTimestamp();
		} else {
			since = until = requestTimestamp;
		}

		ScrobblesPagingDTO_V0_4 paginationDTO = new ScrobblesPagingDTO_V0_4(
				hostUrl, userId, since, until, results, chosenByUserOnly);
		Pair<List<ScrobblesDTO_V0_4>, ScrobblesPagingDTO_V0_4> resultPair = new ImmutablePair<List<ScrobblesDTO_V0_4>, ScrobblesPagingDTO_V0_4>(
				createGetScrobblesResponse(scrobbles), paginationDTO);
		return resultPair;
	}

	public Pair<List<ScrobblesDTO_V0_4>, ScrobblesPagingDTO_V0_4> getScrobblesSince(
			String hostUrl, String userId, Long since, Integer results,
			boolean chosenByUserOnly) throws SongwichAPIException {

		ObjectId userIdObject = authorizeUserGetScrobbles(results, userId);
		List<Scrobble> scrobbles = getScrobbleDAO().findScrobblesByUserIdSince(
				userIdObject, since, results, chosenByUserOnly);

		Long until;
		if (scrobbles != null && !scrobbles.isEmpty()) {
			since = scrobbles.get(scrobbles.size() - 1).getTimestamp();
			until = scrobbles.get(0).getTimestamp();
		} else {
			until = since;
			// make sure we include the last scrobble in an older scrobbles page
			since++;
		}

		ScrobblesPagingDTO_V0_4 paginationDTO = new ScrobblesPagingDTO_V0_4(
				hostUrl, userId, since, until, results, chosenByUserOnly);
		Pair<List<ScrobblesDTO_V0_4>, ScrobblesPagingDTO_V0_4> resultPair = new ImmutablePair<List<ScrobblesDTO_V0_4>, ScrobblesPagingDTO_V0_4>(
				createGetScrobblesResponse(scrobbles), paginationDTO);

		return resultPair;
	}

	public Pair<List<ScrobblesDTO_V0_4>, ScrobblesPagingDTO_V0_4> getScrobblesUntil(
			String hostUrl, String userId, Long until, Integer results,
			boolean chosenByUserOnly) throws SongwichAPIException {

		ObjectId userIdObject = authorizeUserGetScrobbles(results, userId);
		List<Scrobble> scrobbles = getScrobbleDAO().findScrobblesByUserIdUntil(
				userIdObject, until, results, chosenByUserOnly);

		Long since;
		if (scrobbles != null && !scrobbles.isEmpty()) {
			since = scrobbles.get(scrobbles.size() - 1).getTimestamp();
			until = scrobbles.get(0).getTimestamp();
		} else {
			since = until;
			// make sure we include the last scrobble in a newer scrobbles page
			until--;
		}

		ScrobblesPagingDTO_V0_4 paginationDTO = new ScrobblesPagingDTO_V0_4(
				hostUrl, userId, since, until, results, chosenByUserOnly);
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

		// TODO: get this from a configuration file
		final int GET_SCROBBLES_MAX_RESULTS = 100;

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
