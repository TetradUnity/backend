package com.tetradunity.server.models.users;

import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.projections.ShortInfoStudentProjection;
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

    private double average_grade;

    public ShortInfoStudent(ShortInfoStudentProjection projection) {
        this.id = projection.getId();
        this.first_name = projection.getFirst_name();
        this.last_name = projection.getLast_name();
        this.avatar = projection.getAvatar();
        this.average_grade = projection.getAverage_grade();
    }

    public ShortInfoStudent(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.first_name = userEntity.getFirst_name();
        this.last_name = userEntity.getLast_name();
        this.avatar = userEntity.getAvatar();
    }
}
