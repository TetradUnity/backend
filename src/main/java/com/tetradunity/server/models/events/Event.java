package com.tetradunity.server.models.events;

import com.tetradunity.server.projections.EventProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    private long id;
    private String title;
    private String type;

    public Event(EventProjection projection) {
        this(
                projection.getId(), projection.getTitle(),
                projection.getType()
        );
    }
}