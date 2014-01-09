package views.api.stations;

import views.api.PagingDTO;
import views.api.PagingUrlManager;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;

//@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonInclude(Include.NON_EMPTY)
@JsonTypeName("paging")
public class StarredSongsPagingDTO_V0_4 extends PagingDTO {

	public StarredSongsPagingDTO_V0_4(PagingUrlManager pagingUrls) {
		super(pagingUrls);
	}

	public String getOlder() {
		return getPagingUrlManager().getNextPageUrl();
	}

	public String getNewer() {
		return getPagingUrlManager().getPreviousPageUrl();
	}

	@Override
	public String toString() {
		return "StarredSongsPagingDTO_V0_4 [getOlder()=" + getOlder()
				+ ", getNewer()=" + getNewer() + "]";
	}
}
