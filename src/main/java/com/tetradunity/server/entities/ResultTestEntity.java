package com.tetradunity.server.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "results_test")
@Getter
@Setter
@NoArgsConstructor
public class ResultTestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long parent_id;
    private String email;
    private String first_name;
    private String last_name;
    private String answers;
    private double result;
    private long end_time;
    private boolean is_exam;
    private String uid;
    private int duration;

    public ResultTestEntity(long parent_id, String email, String first_name, String last_name, String answers, int result, long end_time, boolean is_exam,
                            String uid, int duration) {
        this.parent_id = parent_id;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.answers = answers;
        this.result = result;
        this.end_time = end_time;
        this.is_exam = is_exam;
        this.uid = uid;
        this.duration = duration;
    }
}