package views.api.stations;

import java.util.List;

import views.api.DataTransferObject;
import views.api.scrobbles.UserOutputDTO_V0_4;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;

//@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonInclude(Include.NON_EMPTY)
@JsonTypeName("station")
public class RadioStationOutputDTO_V0_4 extends DataTransferObject {

	// from input DTO
	private String stationName;
	private String groupName;
	private List<String> scrobblerIds;
	private String imageUrl;
	private String description;

	// additional elements
	private String verified;

	private String stationId;
	private List<UserOutputDTO_V0_4> activeScrobblers;
	private Long numberSubscribers;
	private String active;
	private String stationReadiness;
	private TrackDTO_V0_4 nowPlaying;
	private TrackDTO_V0_4 lookAhead;

	public RadioStationOutputDTO_V0_4() {
	}

	public RadioStationOutputDTO_V0_4(RadioStationInputDTO_V0_4 inputDTO) {
		setStationName(inputDTO.getStationName());
		setGroupName(inputDTO.getGroupName());
		setScrobblerIds(inputDTO.getScrobblerIds());
		setImageUrl(inputDTO.getImageUrl());
		setDescription(inputDTO.getDescription());
	}

	public RadioStationOutputDTO_V0_4(RadioStationUpdateInputDTO_V0_4 inputDTO) {
		setStationId(inputDTO.getStationId());
		setStationName(inputDTO.getStationName());
		setGroupName(inputDTO.getGroupName());
		setScrobblerIds(inputDTO.getScrobblerIds());
		setImageUrl(inputDTO.getImageUrl());
		setDescription(inputDTO.getDescription());
	}

	public String getVerified() {
		return verified;
	}

	public void setVerified(String verified) {
		this.verified = verified;
	}

	public Long getNumberSubscribers() {
		return numberSubscribers;
	}

	public void setNumberSubscribers(Long numberSubscribers) {
		this.numberSubscribers = numberSubscribers;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getStationReadiness() {
		return stationReadiness;
	}

	public void setStationReadiness(String stationReadiness) {
		this.stationReadiness = stationReadiness;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public TrackDTO_V0_4 getNowPlaying() {
		return nowPlaying;
	}

	public void setNowPlaying(TrackDTO_V0_4 nowPlaying) {
		this.nowPlaying = nowPlaying;
	}

	public TrackDTO_V0_4 getLookAhead() {
		return lookAhead;
	}

	public void setLookAhead(TrackDTO_V0_4 lookAhead) {
		this.lookAhead = lookAhead;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public List<String> getScrobblerIds() {
		return scrobblerIds;
	}

	public void setScrobblerIds(List<String> scrobblerIds) {
		this.scrobblerIds = scrobblerIds;
	}

	public List<UserOutputDTO_V0_4> getActiveScrobblers() {
		return activeScrobblers;
	}

	public void setActiveScrobblers(List<UserOutputDTO_V0_4> activeScrobblersDTO) {
		this.activeScrobblers = activeScrobblersDTO;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Override
	public String toString() {
		return "RadioStationOutputDTO_V0_4 [stationName=" + stationName
				+ ", groupName=" + groupName + ", scrobblerIds=" + scrobblerIds
				+ ", imageUrl=" + imageUrl + ", description=" + description
				+ ", verified=" + verified + ", stationId=" + stationId
				+ ", activeScrobblers=" + activeScrobblers
				+ ", numberSubscribers=" + numberSubscribers + ", active="
				+ active + ", stationReadiness=" + stationReadiness
				+ ", nowPlaying=" + nowPlaying + ", lookAhead=" + lookAhead
				+ "]";
	}
}