package com.tetradunity.server.controllers;

import com.tetradunity.server.entities.EducationMaterialEntity;
import com.tetradunity.server.entities.GradeEntity;
import com.tetradunity.server.entities.SubjectEntity;
import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.models.InfoEducationMaterial;
import com.tetradunity.server.models.Role;
import com.tetradunity.server.models.ShortInfoHomework;
import com.tetradunity.server.models.StringModel;
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
import jakarta.validation.constraints.NotNull;
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

        if (educationMaterial == null ||
                (subject = subjectRepository.findById(educationMaterial.getSubject_id()).orElse(null)) == null
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

        if (!is_test && (deadline == 0 || deadline + 1_800_000 < System.currentTimeMillis())) {
            content = content.trim();
            if (content.length() < 15) {
                return ResponseService.failed("content_very_short");
            }
            educationMaterial.setContent(storageService.uploadFile(storageService.convertStringToMultipartFile(content, ".txt"),
                    "education_materials"));
        }

        if (is_test) {
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
                studentSubjectRepository.findByStudentIdAndSubjectId(user.getId(), subject_id).orElse(null) == null) {
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

        if (user.getRole() != Role.CHIEF_TEACHER &&
                user_id != subject.getTeacher_id() &&
                studentSubjectRepository.findByStudentIdAndSubjectId(user_id, subject_id).orElse(null) == null) {
            return ResponseService.failed("no_permission");
        }

        long deadline = educationMaterial.getDeadline();
        long current_time = System.currentTimeMillis();
        String content = educationMaterial.getContent();
        Map<String, Object> response = new HashMap<>();

        if (educationMaterial.is_test()) {
            if (user.getRole() == Role.STUDENT) {
                GradeEntity grade = gradeRepository.findByStudentAndParent(user_id, education_id).orElse(null);
                if (deadline < current_time) {
                    if (grade == null) {
                        return ResponseService.failed();
                    }
                    response.put("ok", true);
                    if (JSONService.isViewing_correct_answers(content)) {
                        response.put("test", JSONService.getQuestionsWithYourAnswersRight(content, grade.getContent()));
                    } else {
                        response.put("test", JSONService.getQuestionsWithYourAnswers(content, grade.getContent()));
                    }
                    return ResponseEntity.ok().body(response);
                }

                int duration = JSONService.getTime(content);

                if (grade == null) {
                    new GradeEntity(user_id, subject_id, education_id,
                            current_time + duration, false);
                } else {
                    if (current_time < grade.getTime_edited_end()) {
                        return ResponseService.failed("no_access_now");
                    }
                    int attempt = grade.getAttempt();
                    if (attempt >= JSONService.getCount_attempt(content)) {
                        return ResponseService.failed("too_many_tries");
                    }
                    grade.setAttempt(++attempt);
                    grade.setTime_edited_end(current_time + duration);
                    grade.setContent("");
                    gradeRepository.save(grade);
                }
                response.put("ok", true);
                response.put("test", JSONService.getQuestions(content, true));
                return ResponseEntity.ok().body(response);
            } else {
                response.put("test", content);
            }
        } else {
            return ResponseEntity.ok().contentType(MediaType.valueOf(storageService.determineFileType(content)))
                    .body(storageService.downloadFile(content));
        }
        return ResponseService.failed();
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

        if (studentSubjectRepository.findByStudentIdAndSubjectId(user_id, subject_id).orElse(null) == null) {
            return ResponseService.failed("no_permission");
        }
        String homework = requestModel.getModel();

        GradeEntity grade = gradeRepository.findByStudentAndParent(user_id, education_id).orElse(null);

        long current_time = System.currentTimeMillis();


        Map<String, Object> response = new HashMap<>();
        if (educationMaterial.is_test()) {
            if (grade == null) {
                return ResponseService.failed();
            }
            if (grade.getTime_edited_end() < System.currentTimeMillis()) {
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
            gradeRepository.save(grade);
            response.put("ok", true);
            response.put("result", result);
        } else {
            if ((grade == null || grade.getValue() == -1) && JSONService.checkFiles(homework)) {
                gradeRepository.save(new GradeEntity(user_id, subject_id, education_id,
                        current_time, false));
                response.put("ok", true);
            } else {
                return ResponseService.failed();
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

        EducationMaterialEntity educationMaterial = educationMaterialRepository.findById(grade.getParent_id()).orElse(null);

        long current_time = System.currentTimeMillis();
        long deadline = educationMaterial.getDeadline();

        if (deadline + 604_800_000 < current_time || deadline > current_time) {
            return ResponseService.failed("no_permission");
        }

        grade.setDate(current_time);
        grade.setValue(result);

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        return ResponseEntity.ok().body(response);
    }
}