package com.tetradunity.server.models;

import com.tetradunity.server.projections.GradeProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Grade {
    private long id;
    private double value;
    private long date;
    private String reason;

    public Grade(GradeProjection projection) {
        this(
                projection.getId(), projection.getValue(),
                projection.getDate(), projection.getReason()
        );
    }
}
