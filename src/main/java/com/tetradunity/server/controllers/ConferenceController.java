package com.tetradunity.server.controllers;

import com.tetradunity.server.entities.ConferenceEntity;
import com.tetradunity.server.entities.GradeEntity;
import com.tetradunity.server.entities.SubjectEntity;
import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.models.events.ConferenceCreate;
import com.tetradunity.server.repositories.ConferenceRepository;
import com.tetradunity.server.repositories.GradeRepository;
import com.tetradunity.server.repositories.SubjectRepository;
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
    SubjectRepository subjectRepository;
    @Autowired
    ConferenceRepository conferenceRepository;
    @Autowired
    GradeRepository gradeRepository;

    private final Pattern pattern = Pattern.compile("^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?(\\?.*)?$\n");

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

        String title = info.getTitle();
        if(title == null || title.trim().isEmpty()){
            return ResponseService.failed();
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
    public ResponseEntity<Object> rateConference(HttpServletRequest req, @RequestParam double result, @RequestParam long conference_id){
        UserEntity user = AuthUtil.authorizedUser(req);

        if(user == null){
            return ResponseService.unauthorized();
        }

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

        GradeEntity grade = gradeRepository.findByStudentAndParent(user.getId(), conference_id, "conference").orElse(null);

        if(grade == null){
            gradeRepository.save(new GradeEntity(user.getId(), subject_id, conference_id, 0, result));
        }
        else{
            grade.setValue(result);
            grade.setDate(current_time);
            gradeRepository.save(grade);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        return ResponseEntity.ok().body(response);
    }
}