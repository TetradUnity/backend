package com.tetradunity.server.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubjectFilter {
    private List<String> tags;
    private Boolean has_exam;
    private String first_name_teacher;
    private String last_name_teacher;
    private String title;
    private boolean ascent = true;
}
