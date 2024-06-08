package com.tetradunity.server.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectCreate {
    private long time_exam_end;
    private long time_start;
    private String title;
    private String description;
    private String short_description;
    private long duration;
    private String timetable;
    private String teacher_email;
    private String[] tags;
    private String exam;
    private String banner;
}