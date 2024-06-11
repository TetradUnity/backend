package com.tetradunity.server.projections;

public interface InfoEducationMaterialProjection {
    long getId();

    String getTitle();

    boolean isTest();

    long getDeadline();

    long getTime_created();
}
