package views.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public abstract class PagingDTO {
	
	@JsonIgnoreType
	public static enum MODE {
		SINCE, UNTIL, OPEN
	}

	@JsonIgnore
	private StringBuilder previousPageUrl;
	@JsonIgnore
	private int previousPageParamCount = 0;

	@JsonIgnore
	private StringBuilder nextPageUrl;
	@JsonIgnore
	private int nextPageParamCount = 0;

	protected PagingDTO(String bothPagesBaseUrl) {
		setPreviousPageBaseUrl(bothPagesBaseUrl);
		setNextPageBaseUrl(bothPagesBaseUrl);
	}

	protected PagingDTO(String previousPageBaseUrl, String nextPageBaseUrl) {
		setPreviousPageBaseUrl(previousPageBaseUrl);
		setNextPageBaseUrl(nextPageBaseUrl);
	}

	protected void setPreviousPageBaseUrl(String previousPageBaseUrl) {
		previousPageUrl = new StringBuilder(previousPageBaseUrl);
	}

	protected void setNextPageBaseUrl(String nextPageBaseUrl) {
		nextPageUrl = new StringBuilder(nextPageBaseUrl);
	}

	protected void addUrlParamToBothPages(String paramKey, String paramValue) {
		addPreviousPageUrlParam(paramKey, paramValue);
		addNextPageUrlParam(paramKey, paramValue);
	}

	protected void addPreviousPageUrlParam(String paramKey, String paramValue) {
		if (previousPageParamCount == 0) {
			previousPageUrl.append("?");
		} else {
			previousPageUrl.append("&");
		}

		previousPageUrl.append(paramKey).append("=").append(paramValue);
		previousPageParamCount++;
	}

	protected void addNextPageUrlParam(String paramKey, String paramValue) {
		if (nextPageParamCount == 0) {
			nextPageUrl.append("?");
		} else {
			nextPageUrl.append("&");
		}

		nextPageUrl.append(paramKey).append("=").append(paramValue);
		nextPageParamCount++;
	}

	// nextPageUrl is never null
	@JsonIgnore
	protected String getNextPageUrl() {
		return nextPageUrl.length() == 0 ? null : nextPageUrl.toString();
	}

	protected void setNextPageUrl(String nextPageUrl) {
		if (nextPageUrl == null) {
			this.nextPageUrl = new StringBuilder();
		} else {
			this.nextPageUrl = new StringBuilder(nextPageUrl);
		}
	}

	@JsonIgnore
	protected String getPreviousPageUrl() {
		return previousPageUrl.length() == 0 ? null : previousPageUrl
				.toString();
	}

	protected void setPreviousPageUrl(String previousPageUrl) {
		if (previousPageUrl == null) {
			this.previousPageUrl = new StringBuilder();
		} else {
			this.previousPageUrl = new StringBuilder(previousPageUrl);
		}
	}

}
