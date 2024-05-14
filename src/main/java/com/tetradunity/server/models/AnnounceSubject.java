package com.tetradunity.server.models.;

import com.tetradunity.server.entities.SubjectEntity;

public class AnnounceSubject{
    private long id;

    private long examEnd;
    private long start;

    private String title;
    private String description;

    private long teacherId;

    public AnnounceSubject(){}

    public AnnounceSubject(SubjectEntity subject){
        this(
            subject.getTitle(), subject.getTeacherId(), subject.getDescription(),
            subject.getExamEnd(), subject.getStart()
        );
    }

    public AnnounceSubject(String title, long teacherId, String description, long examEnd, long start){
        this.title = title;
        this.teacherId = teacherId;
        this.description = description;
        this.examEnd = examEnd;
        this.start = start;
    }
}