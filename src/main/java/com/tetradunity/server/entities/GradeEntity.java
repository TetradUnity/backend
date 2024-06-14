package com.tetradunity.server.entities;

import com.tetradunity.server.models.grades.TypeGrade;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "grades")
@NoArgsConstructor
public class GradeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private double value = -1;
    private long student_id;
    private long subject_id;
    private long parent_id;
    @Column(columnDefinition = "TEXT")
    private String content = "";
    private long time_edited_end;
    private int attempt = 0;
    @Enumerated(EnumType.STRING)
    private TypeGrade type;
    long date = 0;

    public GradeEntity(long student_id, long subject_id, long parent_id, long time_edited_end, TypeGrade type) {
        this.student_id = student_id;
        this.subject_id = subject_id;
        this.parent_id = parent_id;
        this.time_edited_end = time_edited_end;
        this.type = type;
        if(type != TypeGrade.EDUCATION_MATERIAL){
            this.value = 0;
        }
    }

    public GradeEntity(long student_id, long subject_id, long parent_id, long time_edited_end, double value) {
        this.student_id = student_id;
        this.subject_id = subject_id;
        this.parent_id = parent_id;
        this.time_edited_end = time_edited_end;
        this.value = value;
        this.type = TypeGrade.CONFERENCE;
        this.date = System.currentTimeMillis();
    }

    public void incrementAttempt(){
        this.attempt++;
    }
}
