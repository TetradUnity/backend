package com.tetradunity.server.models;

import com.tetradunity.server.entities.SubjectEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class AnnounceSubject{
    private long id;

    private long exam_end;
    private long start;

    private String title;
    private String short_description;
    private String[] tags;

    private String teacher_first_name;
    private String teacher_last_name;

    public AnnounceSubject(){}

    public AnnounceSubject(SubjectEntity subject, String teacher_first_name, String teacher_last_name,
                           String[] tags){
        this(
            subject.getTitle(), teacher_first_name, teacher_last_name, subject.getDescription(),
            subject.getExam_end(), subject.getStart(), tags
        );
    }

    public AnnounceSubject(String title, String teacher_first_name, String teacher_last_name, String short_description,
                           long examEnd, long start, String[] tags){
        this.title = title;

        this.teacher_first_name = teacher_first_name;
        this.teacher_last_name = teacher_last_name;
        this.short_description = short_description;
        this.exam_end = examEnd;
        this.start = start;
        this.tags = tags;
    }
}