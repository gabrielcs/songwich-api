package usecases.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.Scrobble;
import models.User;

import org.bson.types.ObjectId;

import play.Logger;
import usecases.api.util.RequestContext;
import usecases.api.util.UseCase;
import controllers.api.util.SongwichAPIException;
import daos.api.ScrobbleDAO;
import daos.api.ScrobbleDAOMongo;
import daos.api.UserDAO;
import daos.api.UserDAOMongo;
import dtos.api.ScrobblesDTO_V0_4;
import dtos.api.util.APIStatus_V0_4;

public class ScrobblesUseCases extends UseCase {

	public ScrobblesUseCases(RequestContext context) {
		super(context);
	}

	public void postScrobbles(ScrobblesDTO_V0_4 scrobbleDTO) {
		Scrobble scrobble = new Scrobble(getContext().getUser().getId(),
				scrobbleDTO.getTrackTitle(), Arrays.asList(scrobbleDTO.getArtistsNames()),
				Long.parseLong(scrobbleDTO.getTimestamp()),
				Boolean.parseBoolean(scrobbleDTO.getChosenByUser()),
				scrobbleDTO.getPlayer(), getContext().getAppDeveloper()
						.getEmailAddress());

		ScrobbleDAO<ObjectId> scrobbleDAO = new ScrobbleDAOMongo();
		scrobbleDAO.save(scrobble);
	}

	public List<ScrobblesDTO_V0_4> getScrobbles(ObjectId userId)
			throws SongwichAPIException {
		// check if the User the scrobbles were asked for is the same as the
		// authenticated one
		UserDAO<ObjectId> userDAO = new UserDAOMongo();
		User databaseUser = userDAO.findById(userId);
		
		if (databaseUser == null) {
			throw new SongwichAPIException(
					"Invalid userId: " + userId.toString(),
					APIStatus_V0_4.INVALID_PARAMETER);
		}
		
		if (!databaseUser.equals(getContext().getUser())) {
			throw new SongwichAPIException(
					APIStatus_V0_4.UNAUTHORIZED.toString(),
					APIStatus_V0_4.UNAUTHORIZED);
		}

		// user is authorized
		List<Scrobble> scrobbles = new ScrobbleDAOMongo().findByUserId(userId);
		List<ScrobblesDTO_V0_4> scrobbleDTOs = new ArrayList<ScrobblesDTO_V0_4>(
				scrobbles.size());
		ScrobblesDTO_V0_4 scrobbleDTO;
		for (Scrobble scrobble : scrobbles) {
			scrobbleDTO = new ScrobblesDTO_V0_4();
			try {
				scrobbleDTO.setTrackTitle(scrobble.getSongTitle());
				String[] artistNames = (String[]) scrobble.getArtistsNames().toArray();
				scrobbleDTO.setArtistsNames(artistNames);
				scrobbleDTO.setTimestamp(Long.toString(scrobble.getTimestamp()));
			} catch (SongwichAPIException e) {
				// shouldn't happen
				Logger.error(e.toString());
			}
			scrobbleDTO.setChosenByUser(Boolean.toString(scrobble
					.isChoosenByUser()));
			scrobbleDTO.setPlayer(scrobble.getPlayer());

			scrobbleDTOs.add(scrobbleDTO);
		}
		return scrobbleDTOs;
	}
}
