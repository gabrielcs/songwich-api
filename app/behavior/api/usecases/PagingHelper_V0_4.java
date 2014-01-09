package behavior.api.usecases;

import java.util.List;

import models.api.Entity;

import org.bson.types.ObjectId;

import views.api.PagingNotAvailableException;
import views.api.PagingUrlManager;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

public class PagingHelper_V0_4 {

	@JsonIgnoreType
	public static enum Mode {
		SINCE, UNTIL, OPEN
	}

	public static PagingUrlManager getPagingUrlManager(
			String hostUrl, String apiMethodUrl, String requestObjectId,
			List<? extends Entity<ObjectId>> entities, int maxResults, Mode mode)
			throws PagingNotAvailableException {

		PagingUrlManager pagingUrls = new PagingUrlManager(String.format("http://%s/v0.4/%s",
				hostUrl, apiMethodUrl));
		pagingUrls.addUrlParamToBothPages("results",
				Integer.toString(maxResults));

		String oldest = null;
		String newest = null;
		int actualResults = 0;
		if (entities != null && !entities.isEmpty()) {
			oldest = entities.get(entities.size() - 1).getId().toString();
			newest = entities.get(0).getId().toString();
			actualResults = entities.size();
		}

		if (entities.size() == maxResults) {
			pagingUrls.addNextPageUrlParam("until", oldest);
			pagingUrls.addPreviousPageUrlParam("since", newest);
		} else if (actualResults > 0) {
			switch (mode) {
			case SINCE:
				pagingUrls.addNextPageUrlParam("until", oldest);
				pagingUrls.addPreviousPageUrlParam("since", newest);
				break;
			case UNTIL:
				pagingUrls.setNextPageBaseUrl(null);
				pagingUrls.addPreviousPageUrlParam("since", newest);
				break;
			case OPEN:
				pagingUrls.setNextPageBaseUrl(null);
				pagingUrls.addPreviousPageUrlParam("since", newest);
				break;
			}
		} else {
			// actualResults == 0
			switch (mode) {
			case SINCE:
				pagingUrls
						.addNextPageUrlParam("untilInclusive", requestObjectId);
				pagingUrls.addPreviousPageUrlParam("since", requestObjectId);
				break;
			case UNTIL:
				pagingUrls.setNextPageBaseUrl(null);
				pagingUrls.addPreviousPageUrlParam("sinceInclusive",
						requestObjectId);
				break;
			case OPEN:
				throw new PagingNotAvailableException();
			}
		}
		return pagingUrls;
	}
}
