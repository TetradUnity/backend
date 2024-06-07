package com.tetradunity.server.projections;

public interface ShortInfoHomeworkProjection {
    long getId();

    String getFirst_name();

    String getLast_name();

    String getAvatar();

    double getValue();

    long getDispatch_time();

    int getAttempt();
}
