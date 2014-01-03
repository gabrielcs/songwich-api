package views.api.stations;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetIsSongStarredResponse_V0_4 extends APIResponse_V0_4 {

	@JsonProperty("isSongStarredResult")
	private IsSongStarredDTO_V0_4 isSongStarredDTO;

	public GetIsSongStarredResponse_V0_4(APIStatus status, String message,
			IsSongStarredDTO_V0_4 isSongStarredDTO) {
		super(status, message);
		setIsSongStarredDTO(isSongStarredDTO);
	}

	public IsSongStarredDTO_V0_4 getIsSongStarredDTO() {
		return isSongStarredDTO;
	}

	public void setIsSongStarredDTO(IsSongStarredDTO_V0_4 isSongStarredDTO) {
		this.isSongStarredDTO = isSongStarredDTO;
	}
}
