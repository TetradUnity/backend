package com.tetradunity.server.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectCreate{
    private long exam_end;
    private long start;

    private String title;
    private String description;

    private String short_description;
    private long duration;
    private String timetable;

    private String teacher_email;

    private String[] tags;

    private String exam;

    public SubjectCreate(long exam_end, long start, String title, String description,
                         String teacher_email, String exam, String short_description,
                         long duration, String timetable, String[] tags){
        this.exam_end = exam_end;
        this.start = start;
        this.title = title;
        this.description = description;
        this.teacher_email = teacher_email;
        this.exam = exam;
        this.short_description = short_description;
        this.duration = duration;
        this.timetable = timetable;
        this.tags = tags;
    }
}