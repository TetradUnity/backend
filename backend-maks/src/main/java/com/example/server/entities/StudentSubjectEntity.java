package com.example.server.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "student_subjects")
public class StudentSubjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long studentId;
    private long subjectId;

    public StudentSubjectEntity() {}

    public StudentSubjectEntity(long studentId, long subjectId) {
        this.studentId = studentId;
        this.subjectId = subjectId;
    }


    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
