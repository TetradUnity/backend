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

    private long time_exam_end;
    private long time_start;
    private long time_end;

    private String title;
    private String description;

    private String short_description;
    private int duration;
    private String timetable;

    private long teacher_id;
    private boolean is_end = false;
    private boolean is_start = false;

    private String exam;

    public SubjectEntity() {
    }

    public SubjectEntity(String title, long teacher_id, String description, long time_exam_end, long time_start,
                         String exam, String short_description, int duration, String timetable) {
        this.title = title;
        this.teacher_id = teacher_id;
        this.description = description;
        this.time_exam_end = time_exam_end;
        this.time_start = time_start;
        this.exam = exam;
        this.short_description = short_description;
        this.duration = duration;
        this.timetable = timetable;
    }

    public SubjectEntity(SubjectCreate subject, long teacher_id) {
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

    public boolean educationProcess() {
        return is_start && !is_end && System.currentTimeMillis() > time_start;
    }
}
