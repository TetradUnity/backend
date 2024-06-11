package com.tetradunity.server.controllers;

import com.tetradunity.server.entities.*;
import com.tetradunity.server.models.events.InfoEducationMaterial;
import com.tetradunity.server.models.general.Role;
import com.tetradunity.server.models.events.ShortInfoHomework;
import com.tetradunity.server.models.general.StringModel;
import com.tetradunity.server.models.grades.Grade;
import com.tetradunity.server.models.grades.TypeGrade;
import com.tetradunity.server.projections.ShortInfoHomeworkProjection;
import com.tetradunity.server.repositories.EducationMaterialRepository;
import com.tetradunity.server.repositories.GradeRepository;
import com.tetradunity.server.repositories.StudentSubjectRepository;
import com.tetradunity.server.repositories.SubjectRepository;
import com.tetradunity.server.services.JSONService;
import com.tetradunity.server.services.ResponseService;
import com.tetradunity.server.services.StorageService;
import com.tetradunity.server.utils.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/education")
public class EducationController {

    @Autowired
    private EducationMaterialRepository educationMaterialRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private StudentSubjectRepository studentSubjectRepository;
    @Autowired
    private StorageService storageService;
    @Autowired
    private GradeRepository gradeRepository;

    @PostMapping("create-education-material")
    public ResponseEntity<Object> createEducationMaterial(HttpServletRequest req, @RequestBody EducationMaterialEntity educationMaterial) {
        UserEntity user = AuthUtil.authorizedUser(req);

        if (user == null) {
            return ResponseService.unauthorized();
        }

        SubjectEntity subject;
        long subject_id;

        if (educationMaterial == null ||
                (subject = subjectRepository.findById(subject_id = educationMaterial.getSubject_id()).orElse(null)) == null
        ) {
            return ResponseService.failed();
        }

        if (!subject.educationProcess()) {
            return ResponseService.failed();
        }

        if (user.getId() != subject.getTeacher_id()) {
            return ResponseService.failed("no_permission");
        }

        boolean is_test = educationMaterial.is_test();
        long deadline = educationMaterial.getDeadline();

        String content = educationMaterial.getContent();
        String title;
        educationMaterial.setTitle(title = educationMaterial.getTitle().trim());

        if (title.length() < 3) {
            return ResponseService.failed("test_very_short");
        }

        if (!is_test && (deadline == 0 || deadline - 1_800_000 > System.currentTimeMillis())) {
            content = content.trim();
            if (content.length() < 15) {
                return ResponseService.failed("content_very_short");
            }
            educationMaterial.setContent(storageService.uploadFile(storageService.convertStringToMultipartFile(content, ".txt"),
                    "education_materials"));
        } else if (is_test) {
            if (deadline + 1_800_000 < System.currentTimeMillis()) {
                return ResponseService.failed("incorrect_time");
            }
            try {
                educationMaterial.setContent(JSONService.checkTest(content));
            } catch (RuntimeException ex) {
                return ResponseService.failed();
            }
        } else {
            return ResponseService.failed();
        }

        educationMaterial = educationMaterialRepository.save(educationMaterial);

        if(deadline != 0){
            for(StudentSubjectEntity student : studentSubjectRepository.findBySubject_id(subject_id)){
                gradeRepository.save(new GradeEntity(student.getStudent_id(), subject_id, educationMaterial.getId(), is_test ? 0 : deadline,
                        is_test ? TypeGrade.TEST : TypeGrade.EDUCATION_MATERIAL));
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("education_material", educationMaterial);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("get-education-materials")
    public ResponseEntity<Object> getEducationMaterials(HttpServletRequest req, @RequestParam long subject_id) {
        UserEntity user = AuthUtil.authorizedUser(req);

        if (user == null) {
            return ResponseService.unauthorized();
        }

        SubjectEntity subject = subjectRepository.findById(subject_id).orElse(null);

        if (user.getRole() != Role.CHIEF_TEACHER &&
                user.getId() != subject.getTeacher_id() &&
                studentSubjectRepository.findByStudent_idAndSubject_id(user.getId(), subject_id).orElse(null) == null) {
            return ResponseService.failed("no_permission");
        }

        if (subject == null || !subject.educationProcess()) {
            return ResponseService.failed();
        }

        List<InfoEducationMaterial> educationMaterials = educationMaterialRepository
                .findBySubjectId(subject_id)
                .stream()
                .map(InfoEducationMaterial::new)
                .collect(Collectors.toList());
        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("education_materials", educationMaterials);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("open-education-material")
    public ResponseEntity<Object> openEducationMaterial(HttpServletRequest req, @RequestParam long education_id) {
        UserEntity user = AuthUtil.authorizedUser(req);

        if (user == null) {
            return ResponseService.unauthorized();
        }

        EducationMaterialEntity educationMaterial = educationMaterialRepository.findById(education_id).orElse(null);

        if (educationMaterial == null) {
            return ResponseService.notFound();
        }

        long subject_id = educationMaterial.getSubject_id();

        long user_id = user.getId();

        SubjectEntity subject = subjectRepository.findById(subject_id).orElse(null);

        if (subject == null) {
            return ResponseService.failed();
        }

        if (user_id != subject.getTeacher_id() &&
                studentSubjectRepository.findByStudent_idAndSubject_id(user_id, subject_id).orElse(null) == null) {
            return ResponseService.failed("no_permission");
        }

        if (!subject.educationProcess()) {
            return ResponseService.failed();
        }

        String content = educationMaterial.getContent();
        Map<String, Object> response = new HashMap<>();

        if (educationMaterial.is_test()) {
            response.put("ok", true);
            if (user.getRole() == Role.STUDENT) {
                GradeEntity grade = gradeRepository.findByStudentAndParent(user.getId(), education_id, "education_material").orElse(null);

                if (grade == null) {
                    return ResponseService.failed();
                }
                response.put("your_attempts", grade.getAttempt());
                response.put("available_attempt", JSONService.getCount_attempt(content));
                response.put("amount_questions", JSONService.getAmountQuestion(content));
                response.put("duration", JSONService.getTime(content));
            } else {
                response.put("test", content);
            }
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.ok().contentType(MediaType.valueOf(storageService.determineFileType(content)))
                    .body(storageService.downloadFile(content));
        }
    }

    @PostMapping("start-test")
    public ResponseEntity<Object> startTest(HttpServletRequest req, @RequestParam long education_id){
        UserEntity user = AuthUtil.authorizedUser(req);

        if (user == null) {
            return ResponseService.unauthorized();
        }

        EducationMaterialEntity educationMaterial = educationMaterialRepository.findById(education_id).orElse(null);

        if (educationMaterial == null) {
            return ResponseService.notFound();
        }

        if (user.getRole() != Role.STUDENT) {
            return ResponseService.failed();
        }

        if(!educationMaterial.is_test()){
            return ResponseService.notFound();
        }

        GradeEntity grade = gradeRepository.findByStudentAndParent(user.getId(), education_id, "education_material").orElse(null);

        if (grade == null) {
            return ResponseService.failed();
        }

        long deadline = educationMaterial.getDeadline();
        long current_time = System.currentTimeMillis();
        String content = educationMaterial.getContent();

        Map<String, Object> response = new HashMap<>();

        if (deadline < current_time + 600_000) {
            response.put("ok", true);
            if (JSONService.isViewing_correct_answers(content)) {
                response.put("test", JSONService.getQuestionsWithYourAnswersRight(content, grade.getContent()));
            } else {
                response.put("test", JSONService.getQuestionsWithYourAnswers(content, grade.getContent()));
            }
            return ResponseEntity.ok().body(response);
        }

        int duration = JSONService.getTime(content);

        int attempt = grade.getAttempt();

        if (current_time > grade.getTime_edited_end()) {
            if (attempt >= JSONService.getCount_attempt(content)) {
                response.put("ok", true);
                response.put("test", JSONService.getQuestionsWithYourAnswers(content, grade.getContent()));
                return ResponseEntity.ok().body(response);
            }
            long time_end = current_time + duration;
            if(time_end > deadline){
                time_end = deadline;
            }
            grade.setTime_edited_end(time_end);
            grade.setDate(time_end);
            grade.setContent("");
            gradeRepository.save(grade);
            response.put("ok", true);
            response.put("test", JSONService.getQuestions(content, true));
            return ResponseEntity.ok().body(response);
        }
        response.put("ok", true);
        response.put("test", JSONService.getQuestions(content, true));
        response.put("saved_answer", grade.getContent());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("update-answers-test")
    public ResponseEntity<Object> updateAnswers(HttpServletRequest req, @RequestParam long education_id,
                                                        @RequestBody StringModel requestModel) {
        UserEntity user = AuthUtil.authorizedUser(req);

        if (user == null) {
            return ResponseService.unauthorized();
        }

        EducationMaterialEntity educationMaterial = educationMaterialRepository.findById(education_id).orElse(null);

        if (educationMaterial == null) {
            return ResponseService.notFound();
        }

        if (user.getRole() != Role.STUDENT) {
            return ResponseService.failed();
        }

        if(!educationMaterial.is_test()){
            return ResponseService.notFound();
        }

        GradeEntity grade = gradeRepository.findByStudentAndParent(user.getId(), education_id, "test").orElse(null);

        if(grade == null){
            return ResponseService.notFound();
        }

        if (grade.getTime_edited_end() < System.currentTimeMillis()) {
            return ResponseService.failed("late");
        }

        String homework = requestModel.getModel();
        double result;

        try {
            result = JSONService.checkAnswers(educationMaterial.getContent(), homework);
        } catch (RuntimeException ex) {
            return ResponseService.failed();
        }
        grade.setContent(homework);
        grade.setValue(result);

        gradeRepository.save(grade);

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("send-homework")
    public ResponseEntity<Object> openEducationMaterial(HttpServletRequest req, @RequestParam long education_id,
                                                        @RequestBody StringModel requestModel) {
        UserEntity user = AuthUtil.authorizedUser(req);

        if (user == null) {
            return ResponseService.unauthorized();
        }

        EducationMaterialEntity educationMaterial = educationMaterialRepository.findById(education_id).orElse(null);

        if (educationMaterial == null) {
            return ResponseService.notFound();
        }

        if (user.getRole() != Role.STUDENT) {
            return ResponseService.failed();
        }

        long subject_id = educationMaterial.getSubject_id();
        long user_id = user.getId();

        SubjectEntity subject = subjectRepository.findById(subject_id).orElse(null);

        if (subject == null) {
            return ResponseService.failed();
        }

        if (!subject.educationProcess()) {
            return ResponseService.failed();
        }

        if (studentSubjectRepository.findByStudent_idAndSubject_id(user_id, subject_id).orElse(null) == null) {
            return ResponseService.failed("no_permission");
        }
        String homework = requestModel.getModel();

        GradeEntity grade = gradeRepository.findByStudentAndParent(user_id, education_id, "education_material").orElse(null);

        if (grade == null) {
            return ResponseService.failed();
        }

        long current_time = System.currentTimeMillis();


        Map<String, Object> response = new HashMap<>();
        if (educationMaterial.is_test()) {
            if (grade.getTime_edited_end() < current_time) {
                return ResponseService.failed("late");
            }

            double result;

            try {
                result = JSONService.checkAnswers(educationMaterial.getContent(), homework);
            } catch (RuntimeException ex) {
                return ResponseService.failed();
            }
            grade.setContent(homework);
            grade.setValue(result);
            grade.setTime_edited_end(current_time);
            grade.setDate(current_time);
            grade.incrementAttempt();
            gradeRepository.save(grade);
            response.put("ok", true);
            response.put("result", result);
        } else {
            if(current_time > educationMaterial.getDeadline()){
                return ResponseService.failed("late");
            }

            if(educationMaterial.getDeadline() == 0){
                return ResponseService.notFound();
            }

            if (JSONService.checkFiles(homework)) {
                grade.setDate(current_time);
                grade.setContent(homework);
            }
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("view-homeworks")
    public ResponseEntity<Object> viewHomeworks(HttpServletRequest req, @RequestParam long education_id) {
        UserEntity user = AuthUtil.authorizedUser(req);

        if (user == null) {
            return ResponseService.unauthorized();
        }

        EducationMaterialEntity educationMaterial = educationMaterialRepository.findById(education_id).orElse(null);

        if (educationMaterial == null) {
            return ResponseService.failed();
        }

        SubjectEntity subject = subjectRepository.findById(educationMaterial.getSubject_id()).orElse(null);

        if (subject == null) {
            return ResponseService.failed();
        }

        if (subject.getTeacher_id() != user.getId()) {
            return ResponseService.failed("no_permission");
        }

        List<ShortInfoHomeworkProjection> results = gradeRepository.findByParent(education_id);

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("homeworks", results
                .stream()
                .map(ShortInfoHomework::new)
                .collect(Collectors.toList()));
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("view-homework")
    public ResponseEntity<Object> viewHomework(HttpServletRequest req, @RequestParam long grade_id) {
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

        EducationMaterialEntity educationMaterial = educationMaterialRepository.findById(grade.getParent_id()).orElse(null);

        String content = grade.getContent();

        Map<String, Object> response = new HashMap<>();

        response.put("student_info", gradeRepository.findShortInfoById(grade_id));

        if (educationMaterial.is_test()) {
            response.put("test", JSONService.getQuestionsWithYourAnswersRight(educationMaterial.getContent(), content));
        } else {
            response.put("files", content);
        }
        return ResponseEntity.ok().body(response);
    }
}