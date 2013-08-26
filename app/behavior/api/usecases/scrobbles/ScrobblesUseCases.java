package behavior.api.usecases.scrobbles;

import java.util.ArrayList;
import java.util.List;

import models.api.scrobbles.Scrobble;
import models.api.scrobbles.Song;
import models.api.scrobbles.User;

import org.bson.types.ObjectId;

import behavior.api.usecases.RequestContext;
import behavior.api.usecases.UseCase;

import util.api.SongwichAPIException;
import views.api.APIStatus_V0_4;
import views.api.scrobbles.ScrobblesDTO_V0_4;
import database.api.scrobbles.ScrobbleDAO;
import database.api.scrobbles.ScrobbleDAOMongo;
import database.api.scrobbles.UserDAO;
import database.api.scrobbles.UserDAOMongo;

public class ScrobblesUseCases extends UseCase {

	public ScrobblesUseCases(RequestContext context) {
		super(context);
	}

	public void postScrobbles(ScrobblesDTO_V0_4 scrobbleDTO) {
		Song song = new Song(scrobbleDTO.getTrackTitle(),
				scrobbleDTO.getArtistsNames());
		Scrobble scrobble = new Scrobble(getContext().getUser().getId(), song,
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
		List<Scrobble> scrobbles = new ScrobbleDAOMongo().findByUserId(userId,
				false);
		return createGetScrobblesResponse(scrobbles);
	}

	public List<ScrobblesDTO_V0_4> getScrobbles(ObjectId userId, int results)
			throws SongwichAPIException {
		authorizeUserGetScrobbles(userId);
		List<Scrobble> scrobbles = new ScrobbleDAOMongo()
				.findLastScrobblesByUserId(userId, results, false);
		return createGetScrobblesResponse(scrobbles);
	}

	public List<ScrobblesDTO_V0_4> getScrobblesDaysOffset(ObjectId userId,
			int daysOffset) throws SongwichAPIException {
		authorizeUserGetScrobbles(userId);
		List<Scrobble> scrobbles = new ScrobbleDAOMongo()
				.findByUserIdWithDaysOffset(userId, daysOffset, false);
		return createGetScrobblesResponse(scrobbles);
	}

	public List<ScrobblesDTO_V0_4> getScrobblesDaysOffset(ObjectId userId,
			int daysOffset, int results) throws SongwichAPIException {
		authorizeUserGetScrobbles(userId);
		List<Scrobble> scrobbles = new ScrobbleDAOMongo()
				.findLastScrobblesByUserIdWithDaysOffset(userId, daysOffset,
						results, false);
		return createGetScrobblesResponse(scrobbles);
	}

	private List<ScrobblesDTO_V0_4> createGetScrobblesResponse(
			List<Scrobble> scrobbles) {
		List<ScrobblesDTO_V0_4> scrobbleDTOs = new ArrayList<ScrobblesDTO_V0_4>(
				scrobbles.size());
		ScrobblesDTO_V0_4 scrobblesDTO;
		for (Scrobble scrobble : scrobbles) {
			scrobblesDTO = new ScrobblesDTO_V0_4();
			scrobblesDTO.setTrackTitle(scrobble.getSong().getSongTitle());
			scrobblesDTO.setArtistsNames(scrobble.getSong().getArtistsNames());
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
