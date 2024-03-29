package views.api.stations;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import views.api.DTOValidator;
import views.api.DataTransferObject;

//@JsonInclude(Include.NON_EMPTY)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeName("isSongStarredResult")
public class IsSongStarredDTO_V0_4 extends DataTransferObject {
	
	private String userId;

	// only for output
	private SongDTO_V0_4 song;
	
	private String isStarred;
	
	private String idForFeedback;

	public IsSongStarredDTO_V0_4() {
		setValidator(this.new IsSongStarredDTOValidator());
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public SongDTO_V0_4 getSong() {
		return song;
	}

	public void setSong(SongDTO_V0_4 song) {
		this.song = song;
	}

	public String getIsStarred() {
		return isStarred;
	}

	public void setIsStarred(String isStarred) {
		this.isStarred = isStarred;
	}

	public String getIdForFeedback() {
		return idForFeedback;
	}

	public void setIdForFeedback(String idForFeedback) {
		this.idForFeedback = idForFeedback;
	}
	
	public class IsSongStarredDTOValidator extends DTOValidator {
		@Override
		public void addValidation() {
			// nothing to validate
		}
	}

}
