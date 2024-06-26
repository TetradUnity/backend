package com.tetradunity.server.models.subjects;

import com.tetradunity.server.projections.ShortInfoTeacherSubjectProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShortInfoTeacherSubject {
    private long id;
    private String banner;
    private String title;
    private long info;
    private TypeSubject type;

    public ShortInfoTeacherSubject(long id, String banner, String title, long info, int type) {
        this.id = id;
        this.banner = banner;
        this.title = title;
        this.info = info;
        this.type = TypeSubject.getTypeSubject(type);
    }

    public ShortInfoTeacherSubject(ShortInfoTeacherSubjectProjection projection){
        this(
                projection.getId(), projection.getBanner(), projection.getTitle(),
                projection.getInfo(), projection.getType()
        );
    }
}