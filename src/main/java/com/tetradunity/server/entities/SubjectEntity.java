package com.tetradunity.server.entities;

import java.util.Date;

import com.tetradunity.server.models.SubjectCreate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "subjects")
public class SubjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long exam_end;
    private long start;

    private String title;
    private String description;

    private String short_description;
    private long duration;
    private String timetable;

    private long teacher_id;
    private boolean is_end = false;

    private String exam;

    public SubjectEntity(){}

    public SubjectEntity(String title, long teacher_id, String description, long exam_end, long start,
                         String exam, String short_description, long duration, String timetable){
        this.title = title;
        this.teacher_id = teacher_id;
        this.description = description;
        this.exam_end = exam_end;
        this.start = start;
        this.exam = exam;
        this.short_description = short_description;
        this.duration = duration;
        this.timetable = timetable;
    }

    public SubjectEntity(SubjectCreate subject, long teacher_id){
        this(subject.getTitle(),
                teacher_id,
                subject.getDescription(),
                subject.getExam_end(),
                subject.getStart(),
                subject.getExam(),
                subject.getShort_description(),
                subject.getDuration(),
                subject.getTimetable());
    }
}
