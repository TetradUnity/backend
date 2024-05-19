package com.tetradunity.server.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SubjectCreate{
    private long exam_end;
    private long start;
    private String title;
    private String description;
    private String short_description;
    private int duration;
    private String timetable;
    private String teacher_email;
    private String[] tags;
    private String exam;
}