package com.tetradunity.server.models;

public class ExaminationRequest {
    private long subjectId;

    private String email, first_name, last_name;

    public ExaminationRequest(long subjectId, String email, String first_name, String last_name){
        this.subjectId = subjectId;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public boolean isNull(){
        return email == null || first_name == null || last_name == null || subjectId == 0;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString(){
        return email + " " + subjectId;
    }
}
