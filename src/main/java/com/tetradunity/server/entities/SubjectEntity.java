package com.tetradunity.server.entities;

import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "subjects")
public class SubjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long examEnd;
    private long start;

    private String title;
    private String description;

    private long teacherId;

    private String exam;

    public SubjectEntity(){}

    public SubjectEntity(String title, long teacherId, String description, long examEnd, long start, String exam){
        this.title = title;
        this.teacherId = teacherId;
        this.description = description;
        this.examEnd = examEnd;
        this.start = start;
        this.exam = exam;
    }

    public SubjectEntity(SubjectCreate subject, long teacherId){
        this.title = subject.getTitle();
        this.teacherId = teacherId;
        this.description = subject.getDescription();
        this.examEnd = subject.getExamEnd();
        this.start = subject.getStart();
        this.exam = subject.getExam();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(long teacherId) {
        this.teacherId = teacherId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getExamEnd() {
        return examEnd;
    }

    public void setExamEnd(long examEnd) {
        this.examEnd = examEnd;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public String getExam() {
        return exam;
    }

    public void setExam(String exam) {
        this.exam = exam;
    }

}
