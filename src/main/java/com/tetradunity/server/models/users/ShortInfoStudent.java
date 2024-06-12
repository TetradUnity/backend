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
public class ShortInfoStudent {
    private long id;

    private String first_name, last_name;
    private String avatar;

    public ShortInfoStudent(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.first_name = userEntity.getFirst_name();
        this.last_name = userEntity.getLast_name();
        this.avatar = userEntity.getAvatar();
    }
}
