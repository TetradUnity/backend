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
    private String email_teacher;
}
