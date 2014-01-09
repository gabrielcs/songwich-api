package views.api.stations;

import java.util.List;

import models.api.Entity;

import org.bson.types.ObjectId;

import views.api.PagingDTO;
import views.api.PagingNotAvailableException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;

//@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonInclude(Include.NON_EMPTY)
@JsonTypeName("paging")
public class StarredSongsPagingDTO_V0_4 extends PagingDTO {

	public StarredSongsPagingDTO_V0_4(String hostUrl, String userId,
			String requestObjectId, List<? extends Entity<ObjectId>> entities,
			int maxResults, PagingDTO.MODE mode) throws PagingNotAvailableException {

		super(String.format("http://%s/v0.4/starredSongs/%s", hostUrl, userId));
		addUrlParamToBothPages("results", Integer.toString(maxResults));

		String oldest = null;
		String newest = null;
		int actualResults = 0;
		if (entities != null && !entities.isEmpty()) {
			oldest = entities.get(entities.size() - 1).getId().toString();
			newest = entities.get(0).getId().toString();
			actualResults = entities.size();
		}

		if (entities.size() == maxResults) {
			addNextPageUrlParam("until", oldest);
			addPreviousPageUrlParam("since", newest);
		} else if (actualResults > 0) {
			switch (mode) {
			case SINCE:
				addNextPageUrlParam("until", oldest);
				addPreviousPageUrlParam("since", newest);
				break;
			case UNTIL:
				setNextPageUrl(null);
				addPreviousPageUrlParam("since", newest);
				break;
			case OPEN:
				setNextPageUrl(null);
				addPreviousPageUrlParam("since", newest);
				break;
			}
		} else {
			// actualResults == 0
			switch (mode) {
			case SINCE:
				addNextPageUrlParam("untilInclusive", requestObjectId);
				addPreviousPageUrlParam("since", requestObjectId);
				break;
			case UNTIL:
				setNextPageUrl(null);
				addPreviousPageUrlParam("sinceInclusive", requestObjectId);
				break;
			case OPEN:
				throw new PagingNotAvailableException();
			}
		}
	}

	public String getOlder() {
		return getNextPageUrl();
	}

	public String getNewer() {
		return getPreviousPageUrl();
	}

	@Override
	public String toString() {
		return "StarredSongsPagingDTO_V0_4 [getNewer()=" + getNewer()
				+ ", getOlder()=" + getOlder() + "]";
	}
}
