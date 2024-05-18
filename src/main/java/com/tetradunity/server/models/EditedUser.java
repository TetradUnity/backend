package com.tetradunity.server.models;

import com.tetradunity.server.entities.UserEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditedUser {
	private String email;
	private String password;
	private String first_name;
	private String last_name;
	private String oldPassword;

	public EditedUser(){}

	public EditedUser(UserEntity newUserInfo, String oldPassword) {
		this.email = newUserInfo.getEmail();
		this.first_name = newUserInfo.getFirst_name();
		this.last_name = newUserInfo.getLast_name();
		this.password = newUserInfo.getPassword();
		this.oldPassword = oldPassword;
	}
}