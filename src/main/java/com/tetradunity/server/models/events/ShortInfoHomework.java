package com.tetradunity.server.models.events;

import com.tetradunity.server.projections.ShortInfoHomeworkProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ShortInfoHomework {
    private long id;
    private String first_name, last_name, avatar;
    private double value;
    private long dispatch_time;
    private int attempt;

    public ShortInfoHomework(ShortInfoHomeworkProjection projection) {
        this(
                projection.getId(), projection.getFirst_name(), projection.getLast_name(),
                projection.getAvatar(), projection.getValue(), projection.getDispatch_time(), projection.getAttempt()
        );
    }
}