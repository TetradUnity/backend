package com.tetradunity.server.projections;

public interface ShortInfoStudentSubjectProjection {
    long getId();
    String getBanner();
    String getTitle();
    long getInfo();
    int getType();
    String getTeacher_first_name();
    String getTeacher_last_name();
}
