package com.tetradunity.server.models.subjects;

import com.tetradunity.server.entities.SubjectEntity;
import com.tetradunity.server.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Subject {
    private String title;
    private String banner;
    private long teacher_id;
    private String teacher_first_name;
    private String teacher_last_name;

    public Subject(SubjectEntity subject, UserEntity teacher){
        this(
                subject.getTitle(), subject.getBanner(), teacher.getId(), teacher.getFirst_name(), teacher.getLast_name()
        );
    }
}
