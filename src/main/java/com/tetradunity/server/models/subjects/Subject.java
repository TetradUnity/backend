package com.tetradunity.server.models.subjects;

import com.tetradunity.server.entities.SubjectEntity;
import com.tetradunity.server.entities.UserEntity;

import java.util.List;

class Teacher{
    private long id;
    private String first_name;
    private String last_name;
    private String email;

    Teacher(UserEntity user){
        this.id = user.getId();
        this.first_name = user.getFirst_name();
        this.last_name = user.getLast_name();
        this.email = user.getEmail();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

class Student{
    private long id;
    private String first_name;
    private String last_name;

    Student(UserEntity user){
        this.id = user.getId();
        this.first_name = user.getFirst_name();
        this.last_name = user.getLast_name(); 
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
}

public class Subject {
    private String title;
    private Teacher teacher;

    private String description;
    private Student[] students;

    public Subject(){}

    public Subject(SubjectEntity subject, UserEntity teacher, List<UserEntity> students){
        this.title = subject.getTitle();
        this.description = subject.getDescription();
        this.teacher = new Teacher(teacher);
        this.students = new Student[students.size()];
        for(int i = 0; i < this.students.length; i++){
            this.students[i] = new Student(students.get(i));
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Student[] getStudents() {
        return students;
    }

    public void setStudents(Student[] students) {
        this.students = students;
    }
}
