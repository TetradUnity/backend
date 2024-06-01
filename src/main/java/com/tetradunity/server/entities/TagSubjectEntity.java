package com.tetradunity.server.entities;

import com.tetradunity.server.repositories.TagSubjectRepository;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tags_subject")
public class TagSubjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long subject_id;
    private String tag;

    public TagSubjectEntity(long subject_id, String tag) {
        this.subject_id = subject_id;
        this.tag = tag;
    }
}
