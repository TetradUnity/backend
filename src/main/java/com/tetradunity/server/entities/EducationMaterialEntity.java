package com.tetradunity.server.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "education_materials")
public class EducationMaterialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long subject_id;
    private boolean is_test;
    private String content;

    private long deadline;

    public EducationMaterialEntity(EducationMaterialEntity educationMaterial) {
        this(
                educationMaterial.subject_id, educationMaterial.is_test,
                educationMaterial.content, educationMaterial.deadline
        );
    }

    public EducationMaterialEntity(long subject_id, boolean is_test, String content, long deadline) {
        this.subject_id = subject_id;
        this.is_test = is_test;
        this.content = content;
        this.deadline = deadline;
    }
}
