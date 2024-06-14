package com.tetradunity.server.models.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConferenceCreate {
    private long subject_id;
    private long date;
    private String link;
}