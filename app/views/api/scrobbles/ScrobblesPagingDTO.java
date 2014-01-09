package views.api.scrobbles;

import views.api.PagingDTO;
import views.api.PagingUrlManager;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;

//@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonInclude(Include.NON_EMPTY)
@JsonTypeName("paging")
public class ScrobblesPagingDTO extends PagingDTO {

	public ScrobblesPagingDTO(PagingUrlManager pagingUrls, boolean chosenByUserOnly) {
		super(pagingUrls);
		getPagingUrlManager().addUrlParamToBothPages("chosenByUserOnly",
				Boolean.toString(chosenByUserOnly));
	}

	public String getOlder() {
		return getPagingUrlManager().getNextPageUrl();
	}

	public String getNewer() {
		return getPagingUrlManager().getPreviousPageUrl();
	}

	@Override
	public String toString() {
		return "ScrobblesPagingDTO_V0_4 [getOlder()=" + getOlder()
				+ ", getNewer()=" + getNewer() + "]";
	}
}
