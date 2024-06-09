package com.tetradunity.server.models.users;

import com.tetradunity.server.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShortInfoUser {
    private long id;

    private String email, first_name, last_name;

    public ShortInfoUser(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.email = userEntity.getEmail();
        this.first_name = userEntity.getFirst_name();
        this.last_name = userEntity.getLast_name();
    }
}
