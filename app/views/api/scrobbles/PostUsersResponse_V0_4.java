package views.api.scrobbles;

import org.codehaus.jackson.annotate.JsonProperty;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

public class PostUsersResponse_V0_4 extends APIResponse_V0_4 {
	@JsonProperty("user")
	private UserOutputDTO_V0_4 userDTO;

	public PostUsersResponse_V0_4(APIStatus status, String message,
			UserOutputDTO_V0_4 userOutputDTO) {
		super(status, message);
		this.userDTO = userOutputDTO;
	}

	/**
	 * @return the userDTO
	 */
	public UserOutputDTO_V0_4 getUserDTO() {
		return userDTO;
	}

	/**
	 * @param userDTO
	 *            the userDTO to set
	 */
	public void setUserDTO(UserOutputDTO_V0_4 userDTO) {
		this.userDTO = userDTO;
	}
}
