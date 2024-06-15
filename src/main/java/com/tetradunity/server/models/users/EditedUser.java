package com.tetradunity.server.models.users;

import com.tetradunity.server.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditedUser {
    private String email;
    private String password;
    private String first_name;
    private String last_name;
    private String oldPassword;
    private String avatar;

    public EditedUser(UserEntity newUserInfo, String oldPassword) {
        this(
                newUserInfo.getEmail(), newUserInfo.getFirst_name(),
                newUserInfo.getLast_name(), newUserInfo.getPassword(), oldPassword, newUserInfo.getAvatar()
        );
    }
}