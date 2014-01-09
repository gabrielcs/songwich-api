package views.api.scrobbles;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

// paging might be null
@JsonInclude(Include.NON_NULL)
public class GetScrobblesResponse_V0_4 extends APIResponse_V0_4 {
	@JsonProperty("scrobbles")
	private List<ScrobblesDTO_V0_4> scrobblesDTO;
	@JsonProperty("paging")
	private ScrobblesPagingDTO pagingDTO;
	
	/*
	public GetScrobblesResponse_V0_4(APIStatus status, String message) {
		super(status, message);
		scrobblesDTO = new ArrayList<ScrobblesDTO_V0_4>();
	}
	*/

	public GetScrobblesResponse_V0_4(APIStatus status, String message,
			List<ScrobblesDTO_V0_4> scrobblesDTO, ScrobblesPagingDTO paginationDTO) {
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
	
	public ScrobblesPagingDTO getPagingDTO() {
		return pagingDTO;
	}

	public void setPagingDTO(ScrobblesPagingDTO pagingDTO) {
		this.pagingDTO = pagingDTO;
	}
}
