package com.tetradunity.server.controllers;

import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.models.calendars.CalendarFilter;
import com.tetradunity.server.models.events.Event;
import com.tetradunity.server.models.general.Role;
import com.tetradunity.server.models.grades.Grade;
import com.tetradunity.server.repositories.EducationMaterialRepository;
import com.tetradunity.server.repositories.GradeRepository;
import com.tetradunity.server.repositories.StudentSubjectRepository;
import com.tetradunity.server.repositories.SubjectRepository;
import com.tetradunity.server.services.ResponseService;
import com.tetradunity.server.utils.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("calendar")
public class CalendarController {

    @Autowired
    private StudentSubjectRepository studentSubjectRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private EducationMaterialRepository educationMaterialRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @PostMapping("get-month")
    public ResponseEntity<Object> getMonth(HttpServletRequest req,
                                           @RequestParam int year, @RequestParam int month,
                                           @RequestBody CalendarFilter filter) {
        UserEntity user = AuthUtil.authorizedUser(req);

        if (user == null) {
            return ResponseService.unauthorized();
        }

        long[] subject_id = filter.getSubjects_id();

        if(subject_id == null){
            return ResponseService.failed();
        }

        long user_id = user.getId();
        Calendar calendar = new GregorianCalendar(year, month, 1);
        long from = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, 1);
        long till = calendar.getTimeInMillis();

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);

        if(user.getRole() == Role.TEACHER){
            for(long temp : subject_id){
                if(!subjectRepository.existsSubjectByTeacherAndSubject(user_id, temp)){
                    return ResponseService.failed("no_access");
                }
            }
            response.put("events", educationMaterialRepository.findForMonth(subject_id, from, till)
                    .stream()
                    .map(Event::new)
                    .collect(Collectors.toList()));
        }
        else if(user.getRole() == Role.STUDENT){
            for (long temp : subject_id) {
                if (studentSubjectRepository.findByStudent_idAndSubject_id(user_id, temp).orElse(null) == null) {
                    return ResponseService.failed();
                }
            }
            if (filter.isWithGrade()) {
                response.put("grades", gradeRepository.findForMonth(user_id, subject_id, from, till)
                    .stream()
                    .map(Grade::new)
                    .collect(Collectors.toList()));
            } else {
                response.put("events", educationMaterialRepository.findForMonth(subject_id, from, till)
                    .stream()
                    .map(Event::new)
                    .collect(Collectors.toList()));
            }
        }
        else{
            return ResponseService.notFound();
        }
        return ResponseEntity.ok().body(response);
    }
}