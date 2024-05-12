package com.tetradunity.server.entities;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "grades")
public class GradesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int value;
    private long studentId;
    private long subjectId;
    long date = System.currentTimeMillis;

    public GradesEntity() {}

    public GradesEntity(int value, long studentId, long subjectId) {
        this.value = value;
        this.studentId = studentId;
        this.subjectId = subjectId;

    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public Date getDate() {
        return date;
    }
}
