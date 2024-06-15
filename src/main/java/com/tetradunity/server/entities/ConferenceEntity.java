package com.tetradunity.server.entities;

import com.tetradunity.server.models.events.ConferenceCreate;
import jakarta.persistence.*;
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

    public ConferenceEntity(ConferenceCreate info){
        this(info.getSubject_id(), info.getDate(), info.getLink());
    }

    public ConferenceEntity(long subject_id, long date, String link){
        this.subject_id = subject_id;
        this.date = date;
        this.link = link;
    }
}