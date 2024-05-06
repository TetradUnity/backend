package com.example.server.models;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Subject {
    private String title;
    private long teacherId;

    private String description;
    private Set<Long> studentsId;

    public Subject(){}

    public Subject(String title, long teacherId, Set<Long> studentsId){
        this.title = title;
        this.teacherId = teacherId;
        this.studentsId = studentsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(long teacherId) {
        this.teacherId = teacherId;
    }

    public Set<Long> getStudentsId() {
        return studentsId == null ? new TreeSet<Long>(): studentsId;
    }

    public void setStudentsId(Set<Long> studentsId) {
        this.studentsId = studentsId;
    }

    public String getDescription() {
        return description == null ? "without description" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
