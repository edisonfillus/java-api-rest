package org.project.example.api.security;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserToken {
	
	@JsonProperty("access_token")
	private String accessToken;
	
	@JsonProperty("expires_in")
	private int expiresIn = 900;
	
	@JsonProperty("refresh_expires_in")
	private int refreshExpiresIn = 1800;
	
	@JsonProperty("refresh_token")
	private String refreshToken;
	
	@JsonProperty("token_type")
	private String token_type;
	
	@JsonProperty("not-before-policy")
	private int notBeforePolicy=0;
	
	@JsonProperty("session_state")
	private String sessionState;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public int getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}

	public int getRefreshExpiresIn() {
		return refreshExpiresIn;
	}

	public void setRefreshExpiresIn(int refreshExpiresIn) {
		this.refreshExpiresIn = refreshExpiresIn;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getToken_type() {
		return token_type;
	}

	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}

	public int getNotBeforePolicy() {
		return notBeforePolicy;
	}

	public void setNotBeforePolicy(int notBeforePolicy) {
		this.notBeforePolicy = notBeforePolicy;
	}

	public String getSessionState() {
		return sessionState;
	}

	public void setSessionState(String sessionState) {
		this.sessionState = sessionState;
	}
	
	
}
