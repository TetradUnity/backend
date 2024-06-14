package com.tetradunity.server.controllers;

import com.tetradunity.server.entities.ConferenceEntity;
import com.tetradunity.server.entities.GradeEntity;
import com.tetradunity.server.entities.SubjectEntity;
import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.models.events.ConferenceCreate;
import com.tetradunity.server.models.grades.ConferenceGrade;
import com.tetradunity.server.models.grades.TypeGrade;
import com.tetradunity.server.repositories.*;
import com.tetradunity.server.services.ResponseService;
import com.tetradunity.server.utils.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("conference")
public class ConferenceController {

    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private ConferenceRepository conferenceRepository;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudentSubjectRepository studentSubjectRepository;

    private final Pattern pattern = Pattern.compile("^(https?|ftp)://[\\w.-]+(?:\\.[\\w\\\\.-]+)+[/\\w\\-._~:/?#[\\\\]@!$&'()*+,;=.]*$");

    @PostMapping("create")
    public ResponseEntity<Object> create(HttpServletRequest req, @RequestBody ConferenceCreate info){
        UserEntity user = AuthUtil.authorizedUser(req);

        if(user == null){
            return ResponseService.unauthorized();
        }

        SubjectEntity subject = subjectRepository.findById(info.getSubject_id()).orElse(null);

        if(subject == null){
            return ResponseService.failed();
        }

        if(subject.getTeacher_id() != user.getId()){
            return ResponseService.failed("no_permission");
        }

        if (!subject.educationProcess()) {
            return ResponseService.failed();
        }

        long date = info.getDate();

        if(System.currentTimeMillis() >= date){
            return ResponseService.failed("incorrect_time");
        }

        String link = info.getLink();
        if(link == null){
            return ResponseService.failed();
        }

        if(!pattern.matcher(link).matches()){
            return ResponseService.failed("incorrect_link");
        }

        ConferenceEntity conference = conferenceRepository.save(new ConferenceEntity(info));

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("id", conference.getId());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("rate-conference")
    public ResponseEntity<Object> rateConference(HttpServletRequest req, @RequestBody ConferenceGrade conferenceGrade){
        UserEntity user = AuthUtil.authorizedUser(req);

        if(user == null){
            return ResponseService.unauthorized();
        }

        long conference_id = conferenceGrade.getConference_id();

        ConferenceEntity conference = conferenceRepository.findById(conference_id).orElse(null);

        if(conference == null){
            return ResponseService.failed();
        }

        long subject_id = conference.getSubject_id();

        SubjectEntity subject = subjectRepository.findById(subject_id).orElse(null);

        if(subject == null){
            return ResponseService.failed();
        }

        if(user.getId() != subject.getTeacher_id()){
            return ResponseService.failed("no_permission");
        }

        if (!subject.educationProcess()) {
            return ResponseService.failed();
        }

        long current_time = System.currentTimeMillis();
        long date = conference.getDate();

        if (date + 604_800_000 < current_time || date > current_time) {
            return ResponseService.failed("no_permission");
        }

        long student_id = conferenceGrade.getStudent_id();

        if(studentSubjectRepository.findByStudent_idAndSubject_id(student_id, subject_id).orElse(null) == null){
            return ResponseService.failed();
        }

        GradeEntity grade = gradeRepository.findByStudentAndParent(student_id, conference_id, TypeGrade.CONFERENCE.name()).orElse(null);

        double result = conferenceGrade.getResult();

        if(result < 0 || result > 100){
            return ResponseService.failed("incorrect_data");
        }

        if(grade == null){
            gradeRepository.save(new GradeEntity(student_id, subject_id, conference_id, 0, result));
        }
        else{
            System.out.println(grade);
            grade.setValue(result);
            grade.setDate(current_time);
            gradeRepository.save(grade);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        return ResponseEntity.ok().body(response);
    }
}