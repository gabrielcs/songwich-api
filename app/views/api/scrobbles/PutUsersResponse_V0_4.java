package views.api.scrobbles;

import org.codehaus.jackson.annotate.JsonProperty;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

public class PutUsersResponse_V0_4 extends APIResponse_V0_4 {
	@JsonProperty("user")
	private UserOutputDTO_V0_4 userUpdateDTO;

	public PutUsersResponse_V0_4(APIStatus status, String message,
			UserOutputDTO_V0_4 userOutputDTO) {
		super(status, message);
		this.userUpdateDTO = userOutputDTO;
	}

	/**
	 * @return the userUpdateDTO
	 */
	public UserOutputDTO_V0_4 getUserUpdateDTO() {
		return userUpdateDTO;
	}

	/**
	 * @param userUpdateDTO
	 *            the userUpdateDTO to set
	 */
	public void setUserUpdateDTO(UserOutputDTO_V0_4 userUpdateDTO) {
		this.userUpdateDTO = userUpdateDTO;
	}
}
