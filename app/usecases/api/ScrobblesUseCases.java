package usecases.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Scrobble;

import org.bson.types.ObjectId;

import play.Logger;

import usecases.api.util.RequestContext;
import usecases.api.util.UseCase;
import controllers.api.util.SongwichAPIException;
import daos.api.ScrobbleDAO;
import daos.api.ScrobbleDAOMongo;
import dtos.api.ScrobblesDTO_V0_4;

public class ScrobblesUseCases extends UseCase {

	public ScrobblesUseCases(RequestContext context) {
		super(context);
	}

	public void postScrobbles(ScrobblesDTO_V0_4 scrobbleDTO) {
		Scrobble scrobble = new Scrobble(getContext().getUser().getId(),
				scrobbleDTO.getTrackTitle(), scrobbleDTO.getArtistName(),
				new Date(Long.parseLong(scrobbleDTO.getTimestamp())),
				Boolean.parseBoolean(scrobbleDTO.getChosenByUser()),
				getContext().getApp(), getContext().getAppDeveloper()
						.getEmailAddress());

		ScrobbleDAO<ObjectId> scrobbleDAO = new ScrobbleDAOMongo();
		scrobbleDAO.save(scrobble);
	}

	public List<ScrobblesDTO_V0_4> getScrobbles(ObjectId userId) {
		List<Scrobble> scrobbles = new ScrobbleDAOMongo().find().asList();
		List<ScrobblesDTO_V0_4> scrobbleDTOs = new ArrayList<ScrobblesDTO_V0_4>(scrobbles.size());
		ScrobblesDTO_V0_4 scrobbleDTO;
		for (Scrobble scrobble : scrobbles) {
			scrobbleDTO = new ScrobblesDTO_V0_4();
			try {
				scrobbleDTO.setTrackTitle(scrobble.getSongTitle());
				scrobbleDTO.setArtistName(scrobble.getArtistsNames().toString());
				scrobbleDTO.setTimestamp(Long.toString(scrobble.getTimestamp().getTime()));
			} catch (SongwichAPIException e) {
				// shouldn't happen
				Logger.error(e.toString());
			}
			scrobbleDTO.setChosenByUser(Boolean.toString(scrobble.isChoosenByUser()));
			scrobbleDTO.setService(scrobble.getService().getName());
			
			scrobbleDTOs.add(scrobbleDTO);
		}
		return scrobbleDTOs;
	}
}
