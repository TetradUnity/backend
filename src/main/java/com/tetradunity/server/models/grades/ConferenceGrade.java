package com.tetradunity.server.models.grades;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConferenceGrade {
    private long student_id;
    private long conference_id;
    private double result;
}