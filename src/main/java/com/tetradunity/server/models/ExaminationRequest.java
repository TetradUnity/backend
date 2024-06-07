package com.tetradunity.server.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ExaminationRequest {
    private long subjectId;

    private String email, first_name, last_name;

    public ExaminationRequest(long subjectId, String email, String first_name, String last_name) {
        this.subjectId = subjectId;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public boolean isNull() {
        return email == null || first_name == null || last_name == null || subjectId == 0;
    }

    @Override
    public String toString() {
        return email + " " + subjectId;
    }
}
