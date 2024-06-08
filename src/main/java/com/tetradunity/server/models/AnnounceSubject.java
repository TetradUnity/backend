package com.tetradunity.server.models;

import com.tetradunity.server.projections.AnnounceSubjectProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnnounceSubject {
    private long id;
    private long teacher_id;

    private long time_exam_end;
    private long time_start;

    private String title;
    private String short_description;
    private String[] tags;

    private String teacher_first_name;
    private String teacher_last_name;

    private String banner;

    public AnnounceSubject(AnnounceSubjectProjection subject, String teacher_first_name, String teacher_last_name,
                           String[] tags) {
        this(
                subject.getId(),
                subject.getTeacher_id(),
                subject.getTime_exam_end(),
                subject.getTime_start(),
                subject.getTitle(),
                subject.getShort_description(),
                tags,
                teacher_first_name,
                teacher_last_name,
                subject.getBanner()
        );
    }
}