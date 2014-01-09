package views.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class PagingUrlManager {

	@JsonIgnore
	private String previousPageBaseUrl;
	@JsonIgnore
	private StringBuilder previousPageUrlParams = new StringBuilder();
	@JsonIgnore
	private int previousPageParamCount = 0;

	@JsonIgnore
	private String nextPageBaseUrl;
	@JsonIgnore
	private StringBuilder nextPageUrlParams = new StringBuilder();
	@JsonIgnore
	private int nextPageParamCount = 0;

	public PagingUrlManager() {
	}

	public PagingUrlManager(String bothPagesBaseUrl) {
		setBothPagesBaseUrl(bothPagesBaseUrl);
	}

	public PagingUrlManager(String previousPageBaseUrl, String nextPageBaseUrl) {
		setPreviousPageBaseUrl(previousPageBaseUrl);
		setNextPageBaseUrl(nextPageBaseUrl);
	}

	public void setBothPagesBaseUrl(String bothPagesBaseUrl) {
		setPreviousPageBaseUrl(bothPagesBaseUrl);
		setNextPageBaseUrl(bothPagesBaseUrl);
	}

	public void setPreviousPageBaseUrl(String previousPageBaseUrl) {
		this.previousPageBaseUrl = previousPageBaseUrl;
	}

	public void setNextPageBaseUrl(String nextPageBaseUrl) {
		this.nextPageBaseUrl = nextPageBaseUrl;
	}

	public void addUrlParamToBothPages(String paramKey, String paramValue) {
		addPreviousPageUrlParam(paramKey, paramValue);
		addNextPageUrlParam(paramKey, paramValue);
	}

	public void addPreviousPageUrlParam(String paramKey, String paramValue) {
		if (previousPageParamCount == 0) {
			previousPageUrlParams.append("?");
		} else {
			previousPageUrlParams.append("&");
		}

		previousPageUrlParams.append(paramKey).append("=").append(paramValue);
		previousPageParamCount++;
	}

	public void addNextPageUrlParam(String paramKey, String paramValue) {
		if (nextPageParamCount == 0) {
			nextPageUrlParams.append("?");
		} else {
			nextPageUrlParams.append("&");
		}

		nextPageUrlParams.append(paramKey).append("=").append(paramValue);
		nextPageParamCount++;
	}

	@JsonIgnore
	public String getNextPageUrl() {
		if (nextPageBaseUrl == null) {
			return null;
		}

		return nextPageUrlParams.length() == 0 ? nextPageBaseUrl
				: nextPageBaseUrl.concat(nextPageUrlParams.toString());
	}

	@JsonIgnore
	public String getPreviousPageUrl() {
		if (previousPageBaseUrl == null) {
			return null;
		}

		return previousPageUrlParams.length() == 0 ? previousPageBaseUrl
				: previousPageBaseUrl.concat(previousPageUrlParams.toString());
	}
}
