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

    private long student_id;
    private long subject_id;

    public StudentSubjectEntity() {
    }

    public StudentSubjectEntity(long student_id, long subject_id) {
        this.student_id = student_id;
        this.subject_id = subject_id;
    }
}
