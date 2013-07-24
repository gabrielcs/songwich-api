package usecases.api;

import java.util.Date;

import models.Scrobble;

import org.bson.types.ObjectId;

import usecases.api.util.DatabaseContext;
import usecases.api.util.RequestContext;
import usecases.api.util.UseCase;
import daos.api.ScrobbleDAO;
import daos.api.ScrobbleDAOMongo;
import dtos.api.ScrobbleDTO_V0_4;

public class ScrobbleUseCase extends UseCase {

	public ScrobbleUseCase(RequestContext context) {
		super(context);
	}

	public void scrobble(ScrobbleDTO_V0_4 scrobbleDTO) {
		Scrobble scrobble = new Scrobble(
				getContext().getUser().getId(),
				scrobbleDTO.getTrackTitle(), 
				scrobbleDTO.getArtistName(),
				new Date(Long.parseLong(scrobbleDTO.getTimestamp())),
				Boolean.parseBoolean(scrobbleDTO.getChosenByUser()),
				getContext().getApp(), 
				getContext().getAppDeveloper().getEmailAddress()
				);
		
		ScrobbleDAO<ObjectId> scrobbleDAO = new ScrobbleDAOMongo(
				DatabaseContext.getDatastore());
		scrobbleDAO.save(scrobble);
	}
}
