package com.example.server.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "subjects")
public class SubjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private long teacherId;

    public SubjectEntity(){}

    public SubjectEntity(String title, long teacherId){
        this.title = title;
        this.teacherId = teacherId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTeacher() {
        return teacherId;
    }

    public void setTeacher(long teacherId) {
        this.teacherId = this.teacherId;
    }


}