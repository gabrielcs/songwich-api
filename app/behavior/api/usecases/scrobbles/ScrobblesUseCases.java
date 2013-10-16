package behavior.api.usecases.scrobbles;

import java.util.ArrayList;
import java.util.List;

import models.api.scrobbles.Scrobble;
import models.api.scrobbles.Song;
import models.api.scrobbles.User;

import org.bson.types.ObjectId;

import util.api.SongwichAPIException;
import views.api.APIStatus_V0_4;
import views.api.scrobbles.ScrobblesDTO_V0_4;
import behavior.api.usecases.RequestContext;
import behavior.api.usecases.UseCase;
import database.api.scrobbles.ScrobbleDAO;
import database.api.scrobbles.ScrobbleDAOMongo;
import database.api.scrobbles.UserDAO;
import database.api.scrobbles.UserDAOMongo;

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

		ScrobbleDAO<ObjectId> scrobbleDAO = new ScrobbleDAOMongo();
		scrobbleDAO.save(scrobble, getContext().getAppDeveloper()
				.getEmailAddress());

		// updates scrobbleDTO
		scrobbleDTO.setUserId(getContext().getUser().getId().toString());
		scrobbleDTO.setScrobbleId(scrobble.getId().toString());
	}

	public List<ScrobblesDTO_V0_4> getScrobbles(String userId)
			throws SongwichAPIException {
		ObjectId userIdObject = authorizeUserGetScrobbles(userId);
		List<Scrobble> scrobbles = new ScrobbleDAOMongo().findByUserId(
				userIdObject, false);
		return createGetScrobblesResponse(scrobbles);
	}

	public List<ScrobblesDTO_V0_4> getScrobbles(String userId, int results)
			throws SongwichAPIException {
		ObjectId userIdObject = authorizeUserGetScrobbles(userId);
		List<Scrobble> scrobbles = new ScrobbleDAOMongo()
				.findLastScrobblesByUserId(userIdObject, results, false);
		return createGetScrobblesResponse(scrobbles);
	}

	public List<ScrobblesDTO_V0_4> getScrobblesDaysOffset(String userId,
			int daysOffset) throws SongwichAPIException {
		ObjectId userIdObject = authorizeUserGetScrobbles(userId);
		List<Scrobble> scrobbles = new ScrobbleDAOMongo()
				.findByUserIdWithDaysOffset(userIdObject, daysOffset, false);
		return createGetScrobblesResponse(scrobbles);
	}

	public List<ScrobblesDTO_V0_4> getScrobblesDaysOffset(String userId,
			int daysOffset, int results) throws SongwichAPIException {
		ObjectId userIdObject = authorizeUserGetScrobbles(userId);
		List<Scrobble> scrobbles = new ScrobbleDAOMongo()
				.findLastScrobblesByUserIdWithDaysOffset(userIdObject,
						daysOffset, results, false);
		return createGetScrobblesResponse(scrobbles);
	}

	public void deleteScrobbles(String scrobbleId) throws SongwichAPIException {
		ObjectId scrobbleIdObject = authorizeDeleteScrobbles(scrobbleId);
		ScrobbleDAO<ObjectId> scrobbleDAO = new ScrobbleDAOMongo();
		scrobbleDAO.deleteById(scrobbleIdObject);
	}

	private List<ScrobblesDTO_V0_4> createGetScrobblesResponse(
			List<Scrobble> scrobbles) {
		List<ScrobblesDTO_V0_4> scrobbleDTOs = new ArrayList<ScrobblesDTO_V0_4>(
				scrobbles.size());
		ScrobblesDTO_V0_4 scrobblesDTO;
		for (Scrobble scrobble : scrobbles) {
			scrobblesDTO = new ScrobblesDTO_V0_4();
			scrobblesDTO.setScrobbleId(scrobble.getId().toString());
			scrobblesDTO.setTrackTitle(scrobble.getSong().getSongTitle());
			scrobblesDTO.setArtistsNames(scrobble.getSong().getArtistsNames());
			scrobblesDTO.setTimestamp(Long.toString(scrobble.getTimestamp()));
			scrobblesDTO.setChosenByUser(Boolean.toString(scrobble
					.isChosenByUser()));
			scrobblesDTO.setPlayer(scrobble.getPlayer());

			scrobbleDTOs.add(scrobblesDTO);
		}
		return scrobbleDTOs;
	}

	private ObjectId authorizeUserGetScrobbles(String userId)
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
		ObjectId userIdObject = new ObjectId(userId);

		// check if the User the scrobbles were asked for is the same as the
		// authenticated one
		UserDAO<ObjectId> userDAO = new UserDAOMongo();
		User databaseUser = userDAO.findById(userIdObject);

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

		ScrobbleDAO<ObjectId> scrobbleDAO = new ScrobbleDAOMongo();
		Scrobble scrobble = scrobbleDAO.findById(scrobbleIdObject);
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
}
