package com.tetradunity.server.models;

import com.tetradunity.server.entities.SubjectEntity;
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

    public String getTeacherEmail() {
        return teacherEmail;
    }
}
