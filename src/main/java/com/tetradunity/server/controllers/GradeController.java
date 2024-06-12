package com.tetradunity.server.controllers;

import com.tetradunity.server.entities.EducationMaterialEntity;
import com.tetradunity.server.entities.GradeEntity;
import com.tetradunity.server.entities.SubjectEntity;
import com.tetradunity.server.entities.UserEntity;
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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("grade")
public class GradeController {

    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private EducationMaterialRepository educationMaterialRepository;
    @Autowired
    private StudentSubjectRepository studentSubjectRepository;

    @PostMapping("rate")
    public ResponseEntity<Object> rate(HttpServletRequest req, @RequestParam double result, @RequestParam long grade_id) {
        UserEntity user = AuthUtil.authorizedUser(req);

        if (user == null) {
            return ResponseService.unauthorized();
        }

        GradeEntity grade = gradeRepository.findById(grade_id).orElse(null);

        if (grade == null) {
            return ResponseService.failed();
        }

        SubjectEntity subject = subjectRepository.findById(grade.getSubject_id()).orElse(null);

        if (subject == null) {
            return ResponseService.failed();
        }

        if (subject.getTeacher_id() != user.getId()) {
            return ResponseService.failed("no_permission");
        }

        if (!subject.educationProcess()) {
            return ResponseService.failed();
        }

        EducationMaterialEntity educationMaterial = educationMaterialRepository.findById(grade.getParent_id()).orElse(null);

        long current_time = System.currentTimeMillis();
        long deadline = educationMaterial.getDeadline();

        if(grade.getValue() == -1 && (deadline + 604_800_000 < current_time || deadline > current_time)){
            return ResponseService.failed("no_permission");
        }

        if(result < 0 || result > 100){
            return ResponseService.failed("incorrect_data");
        }

        grade.setDate(current_time);
        grade.setValue(result);

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("get-grades")
    public ResponseEntity<Object> getGrades(HttpServletRequest req, @RequestParam long subject_id,
                                            @RequestParam(defaultValue = "1", required = false) int page) {
        UserEntity user = AuthUtil.authorizedUser(req);

        if (user == null) {
            return ResponseService.unauthorized();
        }

        if(studentSubjectRepository.findByStudent_idAndSubject_id(user.getId(), subject_id).orElse(null) == null){
            return ResponseService.failed("no_access");
        }

        int pos = (page - 1) * 15;

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("grades", gradeRepository.findBySubject(subject_id, pos)
                .stream()
                .map(Grade::new)
                .toList());
        return ResponseEntity.ok().body(response);
    }
}