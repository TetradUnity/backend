package com.tetradunity.server.controllers;

import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.models.CalendarFilter;
import com.tetradunity.server.models.Event;
import com.tetradunity.server.models.Grade;
import com.tetradunity.server.repositories.EducationMaterialRepository;
import com.tetradunity.server.repositories.GradeRepository;
import com.tetradunity.server.repositories.StudentSubjectRepository;
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

    @PostMapping("get-month")
    public ResponseEntity<Object> getMonth(HttpServletRequest req,
                                           @RequestParam int year, @RequestParam int month,
                                           @RequestBody CalendarFilter filter) {
        UserEntity user = AuthUtil.authorizedUser(req);

        if (user == null) {
            return ResponseService.unauthorized();
        }

        long student_id = user.getId();
        long[] subject_id = filter.getSubjects_id();

        for (long temp : subject_id) {
            if (studentSubjectRepository.findByStudentIdAndSubjectId(student_id, temp).orElse(null) == null) {
                return ResponseService.failed();
            }
        }

        Calendar calendar = new GregorianCalendar(year, month, 1);

        long from = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, 1);
        long till = calendar.getTimeInMillis();

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        if (filter.isWithGrade()) {
            response.put("grades", gradeRepository.findForMonth(student_id, subject_id, from, till)
                    .stream()
                    .map(Grade::new)
                    .collect(Collectors.toList()));
        } else {
            response.put("events", educationMaterialRepository.findForMonth(subject_id, from, till)
                    .stream()
                    .map(Event::new)
                    .collect(Collectors.toList()));
        }
        return ResponseEntity.ok().body(response);
    }
}