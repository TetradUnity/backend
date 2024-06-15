package com.tetradunity.server.models.users;

import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.models.general.Role;
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
    private String avatar;

    public User(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.email = userEntity.getEmail();
        this.first_name = userEntity.getFirst_name();
        this.last_name = userEntity.getLast_name();
        this.role = userEntity.getRole();
        this.avatar = userEntity.getAvatar();
    }
}
