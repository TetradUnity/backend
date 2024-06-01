package com.tetradunity.server.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class AnnounceSubject {
    private long id;

    private long time_exam_end;
    private long time_start;

    private String title;
    private String short_description;
    private String[] tags;

    private String teacher_first_name;
    private String teacher_last_name;

    public AnnounceSubject() {
    }

    public AnnounceSubject(SubjectAnnounceDB subject, String teacher_first_name, String teacher_last_name,
                           String[] tags) {
        this(
                subject.getTitle(), teacher_first_name, teacher_last_name, subject.getShort_description(),
                subject.getTime_exam_end(), subject.getTime_start(), tags
        );
    }

    public AnnounceSubject(String title, String teacher_first_name, String teacher_last_name, String short_description,
                           long time_examEnd, long time_start, String[] tags) {
        this.title = title;

        this.teacher_first_name = teacher_first_name;
        this.teacher_last_name = teacher_last_name;
        this.short_description = short_description;
        this.time_exam_end = time_examEnd;
        this.time_start = time_start;
        this.tags = tags;
    }
}