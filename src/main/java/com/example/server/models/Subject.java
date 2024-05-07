package com.example.server.models;

import com.example.server.entities.SubjectEntity;

import java.util.Set;

public class Subject {
    private String title;
    private String teacherFirst_name;
    private String teacherLast_name;

    private String description;
    private long[] studentsId;

    public Subject(){}

    public Subject(SubjectEntity subject, String teacherFirst_name, String teacherLast_name, long[] studentsId){
        this.title = subject.getTitle();
        this.description = subject.getDescription();
        this.teacherFirst_name = teacherFirst_name;
        this.teacherLast_name = teacherLast_name;
        this.studentsId = studentsId;
    }

    public Subject(String title, String description, String teacherFirst_name, String teacherLast_name, long[] studentsId) {
        this.title = title;
        this.description = description;
        this.teacherFirst_name = teacherFirst_name;
        this.teacherLast_name = teacherLast_name;
        this.studentsId = studentsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTeacherFirst_name() {
        return teacherFirst_name;
    }

    public void setTeacherFirst_name(String teacherFirst_name) {
        this.teacherFirst_name = teacherFirst_name;
    }

    public String getTeacherLast_name() {
        return teacherLast_name;
    }

    public void setTeacherLast_name(String teacherLast_name) {
        this.teacherLast_name = teacherLast_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long[] getStudentsId() {
        return studentsId;
    }

    public void setStudentsId(long[] studentsId) {
        this.studentsId = studentsId;
    }
}
