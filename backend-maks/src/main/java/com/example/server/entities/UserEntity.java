package com.example.server.entities;

import com.example.server.models.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String email, password, first_name, last_name;

	@Enumerated(EnumType.STRING)
	@Column(name = "role")
	private Role role;

	public UserEntity(){}

	public UserEntity(String email, String password, String first_name, String last_name, Role role){
		this.email = email;
		this.password = password;
		this.first_name = first_name;
		this.last_name = last_name;
		this.role = role;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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
		if(email != null){
			this.email = email;
		}
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if(password != null){
			this.password = password;
		}
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		if(first_name != null){
			this.first_name = first_name;
		}
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		if(last_name != null){
			this.last_name = last_name;
		}
	}
}
