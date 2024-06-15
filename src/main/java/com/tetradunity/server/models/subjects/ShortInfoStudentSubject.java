package com.tetradunity.server.models.subjects;

import com.tetradunity.server.projections.ShortInfoStudentSubjectProjection;
import com.tetradunity.server.projections.ShortInfoTeacherSubjectProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShortInfoStudentSubject {
    private long id;
    private String banner;
    private String teacher_first_name;
    private String teacher_last_name;
    private String title;
    private long info;
    private TypeSubject type;

    public ShortInfoStudentSubject(long id, String banner, String teacher_first_name, String teacher_last_name,
                                   String title, long info, int type) {
        this.id = id;
        this.banner = banner;
        this.teacher_first_name = teacher_first_name;
        this.teacher_last_name = teacher_last_name;
        this.title = title;
        this.info = info;
        this.type = TypeSubject.getTypeSubject(type);
    }

    public ShortInfoStudentSubject(ShortInfoStudentSubjectProjection projection){
        this(
                projection.getId(), projection.getBanner(), projection.getTeacher_first_name(),
                projection.getTeacher_last_name(), projection.getTitle(),
                projection.getInfo(), projection.getType()
        );
    }
}