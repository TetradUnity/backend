package com.tetradunity.server.projections;

public interface AnnounceSubjectProjection {
    long getId();

    String getTitle();

    long getTeacher_id();

    String getShort_description();

    long getTime_exam_end();

    long getTime_start();
}