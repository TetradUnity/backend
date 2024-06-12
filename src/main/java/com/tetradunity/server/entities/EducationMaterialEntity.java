package com.tetradunity.server.entities;

import com.tetradunity.server.models.events.EducationMaterialCreate;
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

    private String title;
    private long subject_id;
    private boolean is_test;
    @Column(columnDefinition = "TEXT")
    private String content;
    private long deadline;
    private long time_created = System.currentTimeMillis();

    public EducationMaterialEntity(long subject_id, String title, boolean is_test, String content, long deadline) {
        this.subject_id = subject_id;
        this.title = title;
        this.is_test = is_test;
        this.content = content;
        this.deadline = deadline;
    }

    public EducationMaterialEntity(EducationMaterialCreate material){
        this(
                material.getSubject_id(), material.getTitle(), material.is_test(),
                material.getContent(), material.getDeadline()
        );
    }
}
