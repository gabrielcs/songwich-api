package views.api.scrobbles;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import views.api.APIResponse_V0_4;
import views.api.APIStatus;

public class GetScrobblesResponse_V0_4 extends APIResponse_V0_4 {
	@JsonProperty("scrobbles")
	private List<ScrobblesDTO_V0_4> scrobblesDTO;
	@JsonProperty("paging")
	private ScrobblesPagingDTO_V0_4 pagingDTO;
	
	/*
	public GetScrobblesResponse_V0_4(APIStatus status, String message) {
		super(status, message);
		scrobblesDTO = new ArrayList<ScrobblesDTO_V0_4>();
	}
	*/

	public GetScrobblesResponse_V0_4(APIStatus status, String message,
			List<ScrobblesDTO_V0_4> scrobblesDTO, ScrobblesPagingDTO_V0_4 paginationDTO) {
		super(status, message);
		setScrobblesDTO(scrobblesDTO);
		setPagingDTO(paginationDTO);
	}

	/**
	 * @return the scrobbleDTO
	 */
	public List<ScrobblesDTO_V0_4> getScrobblesDTO() {
		return scrobblesDTO;
	}

	/**
	 * @param scrobbleDTO
	 *            the scrobbleDTO to set
	 */
	public void setScrobblesDTO(List<ScrobblesDTO_V0_4> scrobblesDTO) {
		this.scrobblesDTO = scrobblesDTO;
	}
	
	public void addScrobbleDTO(ScrobblesDTO_V0_4 scrobbleDTO) {
		scrobblesDTO.add(scrobbleDTO);
	}
	
	public ScrobblesPagingDTO_V0_4 getPagingDTO() {
		return pagingDTO;
	}

	public void setPagingDTO(ScrobblesPagingDTO_V0_4 pagingDTO) {
		this.pagingDTO = pagingDTO;
	}
}
