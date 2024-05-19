package com.tetradunity.server.models;

import com.tetradunity.server.entities.SubjectEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DetailsAnnounceSubject{
    private long exam_end;
    private long start;

    private String title;
    private String description;
    private int duration_exam;
    private long duration;
    private String timetable;

    private String teacher_first_name;
    private String teacher_last_name;

    public DetailsAnnounceSubject(){}

    public DetailsAnnounceSubject(SubjectEntity subject, int duration_exam,
                                  String teacher_first_name, String teacher_last_name) {
        this(
                subject.getExam_end(), subject.getStart(), subject.getTitle(),
                subject.getDescription(), duration_exam, subject.getDuration(), subject.getTimetable(),
                teacher_first_name, teacher_last_name
        );
    }
}