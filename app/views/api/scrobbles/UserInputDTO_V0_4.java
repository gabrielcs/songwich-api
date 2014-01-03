package views.api.scrobbles;

import play.data.validation.ValidationError;
import views.api.DTOValidator;
import views.api.DataTransferObject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;

//@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(Include.NON_NULL)
@JsonTypeName("user")
public class UserInputDTO_V0_4 extends DataTransferObject {

	private String userEmail;

	private String name;

	private String imageUrl;

	private String shortBio;

	public UserInputDTO_V0_4() {
		setValidator(this.new UserDTOValidator());
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getShortBio() {
		return shortBio;
	}

	public void setShortBio(String shortBio) {
		this.shortBio = shortBio;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the userEmail
	 */
	public String getUserEmail() {
		return userEmail;
	}

	/**
	 * @param userEmail
	 *            the userEmail to set
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	@Override
	public String toString() {
		return "UserInputDTO_V0_4 [userEmail=" + userEmail + ", name=" + name
				+ ", imageUrl=" + imageUrl + ", shortBio=" + shortBio + "]";
	}

	public class UserDTOValidator extends DTOValidator {
		@Override
		public void addValidation() {
			addValidation(validateUserEmail(), validateImageUrl());
		}

		private ValidationError validateUserEmail() {
			return validateRequiredEmailAddress("userEmail", userEmail);
		}

		private ValidationError validateImageUrl() {
			return validateImageUrl("imageUrl", imageUrl);
		}
	}
}
