package com.tetradunity.server.models;

import com.tetradunity.server.entities.UserEntity;

public class User {
	private String email, first_name, last_name;

	private Role role;

	public User(){}

	public User(UserEntity userEntity){
		this.email = userEntity.getEmail();
		this.first_name = userEntity.getFirst_name();
		this.last_name = userEntity.getLast_name();
		this.role = userEntity.getRole();
	}

	public User(String email, String first_name, String last_name, Role role){
		this.email = email;
		this.first_name = first_name;
		this.last_name = last_name;
		this.role = role;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
}
