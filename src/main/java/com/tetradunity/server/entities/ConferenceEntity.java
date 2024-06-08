package com.tetradunity.server.entities;

import com.tetradunity.server.models.ConferenceCreate;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "conferences")
public class ConferenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long subject_id;
    private long date;
    private String link;
    private String title;

    public ConferenceEntity(ConferenceCreate info){
        this(info.getSubject_id(), info.getDate(),
                info.getLink(), info.getTitle());
    }

    public ConferenceEntity(long subject_id, long date, String link, String title){
        this.subject_id = subject_id;
        this.date = date;
        this.link = link;
        this.title = title;
    }
}