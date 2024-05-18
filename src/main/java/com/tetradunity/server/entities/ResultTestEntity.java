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

    private long parent_шd;
    private String email;
    private String answers;
    private int results;
    private long finishedTime;
    private boolean isExam;
    private String uid;
}