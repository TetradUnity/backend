package com.tetradunity.server.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "results_test")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResultTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long parentId;
    private String email;
    private String answers;
    private int results;
    private long finishedTime;
    private boolean isExam;
}