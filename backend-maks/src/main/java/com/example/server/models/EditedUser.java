package com.example.server.models;

import com.example.server.entities.UserEntity;

public class EditedUser {
	private UserEntity newUserInfo;
	private String oldPassword;

	public EditedUser(){}

	public EditedUser(UserEntity newUserInfo, String oldPassword) {
		this.newUserInfo = newUserInfo;
		this.oldPassword = oldPassword;
	}

	public UserEntity getNewUserInfo() {
		return newUserInfo;
	}

	public void setNewUserInfo(UserEntity newUserInfo) {
		this.newUserInfo = newUserInfo;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
}