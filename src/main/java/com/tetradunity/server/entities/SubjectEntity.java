package com.tetradunity.server.entities;

import com.tetradunity.server.models.subjects.SubjectCreate;
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
    @Column(columnDefinition = "TEXT")
    private String description;

    private String short_description;
    private long duration;
    private String timetable;

    private String banner;

    private long teacher_id;
    private boolean is_end = false;
    private boolean is_start = false;

    @Column(columnDefinition = "TEXT")
    private String exam;

    public SubjectEntity() {
    }

    public SubjectEntity(String title, long teacher_id, String description, long time_exam_end, long time_start,
                         String exam, String short_description, long duration, String timetable, String banner) {
        this.title = title;
        this.teacher_id = teacher_id;
        this.description = description;
        this.time_exam_end = time_exam_end;
        this.time_start = time_start;
        this.exam = exam;
        this.short_description = short_description;
        this.duration = duration;
        this.timetable = timetable;
        this.banner = banner;
    }

    public SubjectEntity(SubjectCreate subject, long teacher_id) {
        this(subject.getTitle(),
                teacher_id,
                subject.getDescription(),
                subject.getTime_exam_end(),
                subject.getTime_start(),
                subject.getExam(),
                subject.getShort_description(),
                subject.getDuration(),
                subject.getTimetable(),
                subject.getBanner());
    }

    public boolean educationProcess() {
        return is_start && !is_end && System.currentTimeMillis() > time_start;
    }

    public boolean stageAnnounce(){return !is_start || System.currentTimeMillis() < time_start;}
}
