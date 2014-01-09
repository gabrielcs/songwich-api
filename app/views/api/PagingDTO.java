package views.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class PagingDTO {
	@JsonIgnore
	private PagingUrlManager pagingUrlManager;
	
	public PagingDTO(PagingUrlManager pagingUrls) {
		setPagingUrlManager(pagingUrls);
	}
	
	public PagingUrlManager getPagingUrlManager() {
		return pagingUrlManager;
	}

	public void setPagingUrlManager(PagingUrlManager pagingUrlManager) {
		this.pagingUrlManager = pagingUrlManager;
	}
}
