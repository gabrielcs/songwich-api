package views.api.scrobbles;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

//there was a mysterious "userDTO" property duplicating the output
@JsonIgnoreProperties({"userDTO"})
public class GetUsersUniqueResponse_V0_4 extends APIResponse_V0_4 {

	@JsonProperty("user")
	private UserDTO_V0_4 userStationDTO;

	public GetUsersUniqueResponse_V0_4(APIStatus status, String message,
			UserDTO_V0_4 userStationDTO) {
		super(status, message);
		setUserDTO(userStationDTO);
	}

	public UserDTO_V0_4 getUserDTO() {
		return userStationDTO;
	}

	public void setUserDTO(UserDTO_V0_4 userDTO) {
		this.userStationDTO = userDTO;
	}

}
