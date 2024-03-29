package models.api.scrobbles;

import java.util.UUID;

import models.api.MongoModel;
import models.api.MongoModelImpl;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Indexed;

import database.api.scrobbles.AppDAO;
import database.api.scrobbles.AppDAOMongo;
import database.api.scrobbles.UserDAO;
import database.api.scrobbles.UserDAOMongo;

@Embedded
public class AuthToken extends MongoModelImpl implements MongoModel {
	@Indexed
	private String token;

	private AuthTokenState state;

	protected AuthToken() {
	}

	public AuthToken(String authToken) {
		this.token = authToken;
		this.state = AuthTokenState.VALID;
	}

	/*
	 * Creates a unique user auth token with a valid state
	 */
	public static AuthToken createUserAuthToken() {
		String userAuthToken = UUID.randomUUID().toString();
		// assert that the random UUID is unique (might be expensive)
		UserDAO<ObjectId> userDAO = new UserDAOMongo();
		User user = userDAO.findByUserAuthToken(userAuthToken);
		if (user == null) {
			return new AuthToken(userAuthToken);
		} else {
			return createUserAuthToken();
		}
	}

	/*
	 * Creates a unique dev auth token with a valid state
	 */
	public static AuthToken createDevAuthToken() {
		String devAuthToken = UUID.randomUUID().toString();
		// assert that the random UUID is unique (might be expensive)
		AppDAO<ObjectId> appDAO = new AppDAOMongo();
		App app = appDAO.findByDevAuthToken(devAuthToken);
		if (app == null) {
			return new AuthToken(devAuthToken);
		} else {
			return createDevAuthToken();
		}
	}

	public String getToken() {
		return token;
	}

	public void setToken(String authToken) {
		this.token = authToken;
		fireModelUpdated();
	}

	public void setToken(UUID authToken) {
		this.token = authToken.toString();
		fireModelUpdated();
	}

	public AuthTokenState getState() {
		return state;
	}

	public void setState(AuthTokenState state) {
		this.state = state;
		fireModelUpdated();
	}

	public enum AuthTokenState {
		VALID, REVOKED;
	}

	@Override
	public String toString() {
		return "AuthToken [token=" + token + ", state=" + state + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuthToken other = (AuthToken) obj;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		return true;
	}
}
