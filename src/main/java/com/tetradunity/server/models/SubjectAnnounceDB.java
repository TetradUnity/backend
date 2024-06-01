package com.tetradunity.server.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubjectAnnounceDB {
    private long id;
    private String title;
    private long teacher_id;
    private String short_description;
    private long time_exam_end;
    private long time_start;
}
