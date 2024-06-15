package com.tetradunity.server.projections;

public interface InfoEducationMaterialProjection {
    long getId();

    String getTitle();

    Boolean getIs_test();

    long getDeadline();

    long getTime_created();
}
