package com.tetradunity.server.entities;

import com.tetradunity.server.models.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String avatar = "";

    private String email, password, first_name, last_name;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    public UserEntity() {
    }

    public UserEntity(String email, String password, String first_name, String last_name, Role role, String avatar) {
        this.email = email;
        this.password = password;
        this.first_name = first_name;
        this.last_name = last_name;
        this.role = role;
        this.avatar = avatar;
    }

    public UserEntity(String email, String password, String first_name, String last_name, Role role) {
        this.email = email;
        this.password = password;
        this.first_name = first_name;
        this.last_name = last_name;
        this.role = role;
    }
}
