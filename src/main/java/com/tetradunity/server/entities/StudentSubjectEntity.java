package com.tetradunity.server.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "student_subjects")
public class StudentSubjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long studentId;
    private long subjectId;

    public StudentSubjectEntity() {
    }

    public StudentSubjectEntity(long studentId, long subjectId) {
        this.studentId = studentId;
        this.subjectId = subjectId;
    }
}
