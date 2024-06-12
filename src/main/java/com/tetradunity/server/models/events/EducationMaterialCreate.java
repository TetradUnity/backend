package com.tetradunity.server.models.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EducationMaterialCreate {
    private String title;
    private long subject_id;
    private Boolean is_test;
    private String content;
    private long deadline;
}