package com.tetradunity.server.projections;

public interface EventProjection {
    long getId();

    String getTitle();

    long getDate();

    String getType();
}
