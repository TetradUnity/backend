package com.tetradunity.server.projections;

public interface GradeProjection {
    long getId();

    double getValue();

    long getDate();

    String getReason();
}