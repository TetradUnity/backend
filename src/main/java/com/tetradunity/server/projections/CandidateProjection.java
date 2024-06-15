package com.tetradunity.server.projections;

public interface CandidateProjection {
    long getId();
    String getEmail();
    String getFirst_name();
    String getLast_name();
    double getResult();
    int getDuration();
}
