package views.api.scrobbles;

import java.util.List;

import models.api.scrobbles.Scrobble;
import views.api.PagingDTO;
import views.api.PagingNotAvailableException;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;

//@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonInclude(Include.NON_EMPTY)
@JsonTypeName("paging")
public class ScrobblesPagingDTO_V0_4 extends PagingDTO {

	@JsonIgnoreType
	public static enum MODE {
		SINCE, UNTIL, OPEN
	}

	public ScrobblesPagingDTO_V0_4(String hostUrl, String userId,
			String requestObjectId, List<Scrobble> scrobbles, int maxResults,
			MODE mode, boolean chosenByUserOnly)
			throws PagingNotAvailableException {

		super(String.format("http://%s/v0.4/scrobbles/%s", hostUrl, userId));
		addUrlParamToBothPages("chosenByUserOnly",
				Boolean.toString(chosenByUserOnly));
		addUrlParamToBothPages("results", Integer.toString(maxResults));

		String oldest = null;
		String newest = null;
		int actualResults = 0;
		if (scrobbles != null && !scrobbles.isEmpty()) {
			oldest = scrobbles.get(scrobbles.size() - 1).getId().toString();
			newest = scrobbles.get(0).getId().toString();
			actualResults = scrobbles.size();
		}

		if (scrobbles.size() == maxResults) {
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

	public String getOlderScrobbles() {
		return getNextPageUrl();
	}

	public String getNewerScrobbles() {
		return getPreviousPageUrl();
	}

	@Override
	public String toString() {
		return "ScrobblesPagingDTO_V0_4 [getOlderScrobblesPage()="
				+ getOlderScrobbles() + ", getNewerScrobblesPage()="
				+ getNewerScrobbles() + "]";
	}
}
