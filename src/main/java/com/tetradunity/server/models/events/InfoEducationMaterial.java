package com.tetradunity.server.models.events;

import com.tetradunity.server.projections.InfoEducationMaterialProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InfoEducationMaterial {
    private long id;
    private String title;
    private boolean is_test;
    private long deadline;
    private long time_created;

    public InfoEducationMaterial(InfoEducationMaterialProjection projection) {
        this(projection.getId(), projection.getTitle(), projection.isTest(), projection.getDeadline(), projection.getTime_created());
    }
}
