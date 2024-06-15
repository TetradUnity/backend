package com.tetradunity.server.models.users;

import com.tetradunity.server.entities.UserEntity;

public class UserWithTokens {
	private UserEntity userEntity;
	private String accessToken;
	private String refreshToken;

	public UserWithTokens(UserEntity userEntity, String accessToken, String refreshToken){
		this.userEntity = userEntity;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public UserEntity getUserEntity() {
		return userEntity;
	}

	public void setUserEntity(UserEntity userEntity) {
		this.userEntity = userEntity;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
