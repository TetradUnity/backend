package com.tetradunity.server.models.tests;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ExaminationRequest {
    private long subject_id;

    private String email, first_name, last_name;

    public ExaminationRequest(long subject_id, String email, String first_name, String last_name) {
        this.subject_id = subject_id;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public boolean isNull() {
        return email == null || first_name == null || last_name == null || subject_id == 0;
    }

    @Override
    public String toString() {
        return email + " " + subject_id;
    }
}
