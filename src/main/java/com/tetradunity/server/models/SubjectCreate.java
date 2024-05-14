package com.tetradunity.server.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectCreate{
    private long examEnd;
    private long start;

    private String title;
    private String description;

    private String teacherEmail;

    private String exam;

    public SubjectCreate(long examEnd, long start, String title, String description, String teacherEmail, String exam){
        this.examEnd = examEnd;
        this.start = start;
        this.title = title;
        this.description = description;
        this.teacherEmail = teacherEmail;
        this.exam = exam;
    }
}