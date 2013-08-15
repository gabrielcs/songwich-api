package usecases.api;

import java.util.ArrayList;
import java.util.List;

import models.api.Scrobble;
import models.api.User;

import org.bson.types.ObjectId;

import controllers.api.util.APIStatus_V0_4;

import usecases.api.util.RequestContext;
import usecases.api.util.SongwichAPIException;
import usecases.api.util.UseCase;
import views.api.ScrobblesDTO_V0_4;
import database.api.ScrobbleDAO;
import database.api.ScrobbleDAOMongo;
import database.api.UserDAO;
import database.api.UserDAOMongo;

public class ScrobblesUseCases extends UseCase {

	public ScrobblesUseCases(RequestContext context) {
		super(context);
	}

	public void postScrobbles(ScrobblesDTO_V0_4 scrobbleDTO) {
		Scrobble scrobble = new Scrobble(getContext().getUser().getId(),
				scrobbleDTO.getTrackTitle(), scrobbleDTO.getArtistsNames(),
				Long.parseLong(scrobbleDTO.getTimestamp()),
				Boolean.parseBoolean(scrobbleDTO.getChosenByUser()),
				scrobbleDTO.getPlayer(), getContext().getAppDeveloper()
						.getEmailAddress());

		ScrobbleDAO<ObjectId> scrobbleDAO = new ScrobbleDAOMongo();
		scrobbleDAO.save(scrobble);
	}

	public List<ScrobblesDTO_V0_4> getScrobbles(ObjectId userId)
			throws SongwichAPIException {
		authorizeUserGetScrobbles(userId);
		List<Scrobble> scrobbles = new ScrobbleDAOMongo().findByUserId(userId);
		return createGetScrobblesResponse(scrobbles);
	}

	public List<ScrobblesDTO_V0_4> getScrobbles(ObjectId userId, int results)
			throws SongwichAPIException {
		authorizeUserGetScrobbles(userId);
		List<Scrobble> scrobbles = new ScrobbleDAOMongo()
				.findLastScrobblesByUserId(userId, results);
		return createGetScrobblesResponse(scrobbles);
	}

	public List<ScrobblesDTO_V0_4> getScrobblesDaysOffset(ObjectId userId,
			int daysOffset) throws SongwichAPIException {
		authorizeUserGetScrobbles(userId);
		List<Scrobble> scrobbles = new ScrobbleDAOMongo()
				.findByUserIdWithDaysOffset(userId, daysOffset);
		return createGetScrobblesResponse(scrobbles);
	}

	public List<ScrobblesDTO_V0_4> getScrobblesDaysOffset(ObjectId userId,
			int daysOffset, int results) throws SongwichAPIException {
		authorizeUserGetScrobbles(userId);
		List<Scrobble> scrobbles = new ScrobbleDAOMongo()
				.findLastScrobblesByUserIdWithDaysOffset(userId, daysOffset,
						results);
		return createGetScrobblesResponse(scrobbles);
	}

	private List<ScrobblesDTO_V0_4> createGetScrobblesResponse(
			List<Scrobble> scrobbles) {
		List<ScrobblesDTO_V0_4> scrobbleDTOs = new ArrayList<ScrobblesDTO_V0_4>(
				scrobbles.size());
		ScrobblesDTO_V0_4 scrobblesDTO;
		for (Scrobble scrobble : scrobbles) {
			scrobblesDTO = new ScrobblesDTO_V0_4();
			scrobblesDTO.setTrackTitle(scrobble.getSongTitle());
			scrobblesDTO.setArtistsNames(scrobble.getArtistsNames());
			scrobblesDTO.setTimestamp(Long.toString(scrobble.getTimestamp()));
			scrobblesDTO.setChosenByUser(Boolean.toString(scrobble
					.isChoosenByUser()));
			scrobblesDTO.setPlayer(scrobble.getPlayer());

			scrobbleDTOs.add(scrobblesDTO);
		}
		return scrobbleDTOs;
	}

	private void authorizeUserGetScrobbles(ObjectId userId)
			throws SongwichAPIException {
		// check if the User the scrobbles were asked for is the same as the
		// authenticated one
		UserDAO<ObjectId> userDAO = new UserDAOMongo();
		User databaseUser = userDAO.findById(userId);

		if (databaseUser == null) {
			throw new SongwichAPIException("Invalid userId: "
					+ userId.toString(), APIStatus_V0_4.INVALID_PARAMETER);
		}

		if (!databaseUser.equals(getContext().getUser())) {
			throw new SongwichAPIException(
					APIStatus_V0_4.UNAUTHORIZED.toString(),
					APIStatus_V0_4.UNAUTHORIZED);
		}
	}
}
