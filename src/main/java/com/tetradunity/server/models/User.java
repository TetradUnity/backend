package com.tetradunity.server.models;

import com.tetradunity.server.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private long id;

    private String email, first_name, last_name;

    private Role role;

    public User(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.email = userEntity.getEmail();
        this.first_name = userEntity.getFirst_name();
        this.last_name = userEntity.getLast_name();
        this.role = userEntity.getRole();
    }
}
