package com.example.server.entities;

import com.example.server.models.Subject;
import jakarta.persistence.*;

@Entity
@Table(name = "subjects")
public class SubjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;


    private String description;

    private long teacherId;

    public SubjectEntity(){}

    public SubjectEntity(String title, long teacherId, String description){
        this.title = title;
        this.teacherId = teacherId;
        this.description = description;
    }

    public SubjectEntity(Subject subject){
        this.title = subject.getTitle();
        this.teacherId = subject.getTeacherId();
        this.description = subject.getDescription();
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

    public long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(long teacherId) {
        this.teacherId = teacherId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
