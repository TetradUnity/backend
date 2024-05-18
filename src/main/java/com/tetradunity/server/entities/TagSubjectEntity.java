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

    private long subject;
    private String tag;

    public TagSubjectEntity(long subject, String tag){
        this.subject = subject;
        this.tag = tag;
    }
}
