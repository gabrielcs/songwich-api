package views.api.scrobbles;

import views.api.PagingDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;

//@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonInclude(Include.NON_EMPTY)
@JsonTypeName("paging")
public class ScrobblesPagingDTO_V0_4 extends PagingDTO {

	public ScrobblesPagingDTO_V0_4(String hostUrl, String userId, Long currentPageSince,
			Long currentPageUntil, Integer results, Boolean chosenByUserOnly) {
		
		super(String.format("http://%s/v0.4/scrobbles/%s", hostUrl, userId));

		// currentPageSince and currentPageUntil are never null
		// older scrobbles
		addNextPageUrlParam("until", currentPageSince.toString()); 
		// newer scrobbles
		addPreviousPageUrlParam("since", currentPageUntil.toString());  

		if (results != null) {
			addUrlParamToBothPages("results", results.toString());
		}
		
		if (chosenByUserOnly != null) {
			addUrlParamToBothPages("chosenByUserOnly", chosenByUserOnly.toString());
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
