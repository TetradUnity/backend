package com.tetradunity.server.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "grades")
public class GradeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int value;
    private long studentId;
    private long subjectId;
    long date = System.currentTimeMillis();

    public GradeEntity() {}

    public GradeEntity(int value, long studentId, long subjectId) {
        this.value = value;
        this.studentId = studentId;
        this.subjectId = subjectId;

    }
}
