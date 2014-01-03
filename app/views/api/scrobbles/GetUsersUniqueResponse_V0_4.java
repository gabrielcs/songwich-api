package views.api.scrobbles;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//there was a mysterious "userDTO" property duplicating the output
@JsonIgnoreProperties({"userDTO"})
public class GetUsersUniqueResponse_V0_4 extends APIResponse_V0_4 {

	@JsonProperty("user")
	private UserOutputDTO_V0_4 userStationDTO;

	public GetUsersUniqueResponse_V0_4(APIStatus status, String message,
			UserOutputDTO_V0_4 userStationDTO) {
		super(status, message);
		setUserDTO(userStationDTO);
	}

	public UserOutputDTO_V0_4 getUserDTO() {
		return userStationDTO;
	}

	public void setUserDTO(UserOutputDTO_V0_4 userDTO) {
		this.userStationDTO = userDTO;
	}

}
