package com.tetradunity.server.entities;

import java.util.Date;

import com.tetradunity.server.models.SubjectCreate;
import jakarta.persistence.*;

@Entity
@Table(name = "subjects")
public class SubjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Date examenEnd;

    private String title;


    private String description;

    private long teacherId;

    public SubjectEntity(){}

    public SubjectEntity(String title, long teacherId, String description){
        this.title = title;
        this.teacherId = teacherId;
        this.description = description;
    }

    public SubjectEntity(SubjectCreate subjectCreate){
        this.title = subjectCreate.getTitle();
        this.teacherId = subjectCreate.getTeacherId();
        this.description = subjectCreate.getDescription();
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
