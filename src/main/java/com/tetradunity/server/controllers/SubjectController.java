package com.tetradunity.server.controllers;

import com.tetradunity.server.entities.*;
import com.tetradunity.server.models.*;
import com.tetradunity.server.projections.AnnounceSubjectProjection;
import com.tetradunity.server.repositories.*;
import com.tetradunity.server.services.*;
import com.tetradunity.server.utils.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/subject")
public class SubjectController {
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private StudentSubjectRepository studentSubjectRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private TagSubjectRepository tagSubjectRepository;
    @Autowired
    private ResultExamRepository resultExamRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private StorageService storageService;

    private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @PostMapping("create")
    public ResponseEntity<Object> createSubject(HttpServletRequest req,
                                                @RequestBody(required = false) SubjectCreate subject) {
        UserEntity user = AuthUtil.authorizedUser(req);

        if (user == null) {
            return ResponseService.unauthorized();
        }

        if (subject == null) {
            return ResponseService.failed();
        }

        if (user.getRole() == Role.CHIEF_TEACHER) {
            if (subject.getTeacher_email() == null || subject.getTitle() == null ||
                    subject.getTime_exam_end() == 0 || subject.getTime_start() == 0 ||
                    subject.getShort_description() == null || subject.getDuration() == 0 ||
                    subject.getTimetable() == null || subject.getTags() == null) {
                return ResponseService.failed("incorrect_data");
            }

            UserEntity teacher = userRepository.findByEmail(subject.getTeacher_email()).orElse(null);


            if (System.currentTimeMillis() + 25_920_000 > subject.getTime_exam_end() ||
                    subject.getTime_exam_end() + 86_399_999 > subject.getTime_start() ||
                    subject.getDuration() < 25_920_000) {
                return ResponseService.failed("error_time");
            }

            if (teacher == null) {
                return ResponseService.failed("teacher_not_exists");
            }

            String[] tags = subject.getTags();

            if (subject.getExam() == null) {
                subject.setExam("");
            } else {
                try {
                    subject.setExam(JSONService.checkTest(subject.getExam()));
                } catch (Exception e) {
                    return ResponseService.failed("incorrect_format_exam");
                }
            }

            if (subject.getDescription() != null) {
                String description;
                subject.setDescription(description = subject.getDescription().trim());
                if (description.length() < 100) {
                    return ResponseService.failed();
                }
            } else {
                return ResponseService.failed();
            }

            String banner = subject.getBanner();

            Random rand = new Random();

            if (banner == null) {
                subject.setBanner("banners/base" + rand.nextInt(1, 5));
            } else {
                if (storageService.downloadFile("banners/" + banner) != null) {
                    subject.setBanner(banner);
                } else {
                    subject.setBanner("banners/base" + rand.nextInt(1, 5));
                }
            }
            SubjectEntity subjectEntity = subjectRepository.save(new SubjectEntity(subject, teacher.getId()));

            long id = subjectEntity.getId();

            for (String tag : tags) {
                if (tagRepository.findByTag(tag).isEmpty()) {
                    tagRepository.save(new TagEntity(tag));
                }
                tagSubjectRepository.save(new TagSubjectEntity(id, tag));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("ok", true);
            response.put("subject_id", id);
            return ResponseEntity.ok().body(response);
        }
        return ResponseService.failed("no_permission");
    }

    @GetMapping("get")
    public ResponseEntity<Object> getSubject(HttpServletRequest req,
                                             @RequestParam long subjectId) {
        UserEntity user = AuthUtil.authorizedUser(req);

        if (user == null) {
            return ResponseService.unauthorized();
        }

        SubjectEntity subjectEntity = subjectRepository.findById(subjectId).orElse(null);

        if (subjectEntity == null) {
            return ResponseService.failed();
        }

        if (studentSubjectRepository.findByStudentIdAndSubjectId(user.getId(), subjectId).isPresent() ||
                subjectEntity.getTeacher_id() == user.getId() || user.getRole() == Role.CHIEF_TEACHER
        ) {
            UserEntity teacher = userRepository.findById(subjectEntity.getTeacher_id()).orElse(null);

            if (teacher == null) {
                return ResponseService.failed();
            }

            List<UserEntity> students = new ArrayList<>();
            List<StudentSubjectEntity> studentsSubject = studentSubjectRepository.findBySubjectId(subjectId);
            UserEntity student;
            for (StudentSubjectEntity studentSubject : studentsSubject) {
                if ((student = userRepository.findById(studentSubject.getStudentId()).orElse(null))
                        != null) {
                    students.add(student);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("ok", true);
            response.put("subject", new Subject(subjectEntity, teacher, students));
            return ResponseEntity.ok().body(response);
        }
        return ResponseService.failed("no_permission");
    }

    @PostMapping("get-announce-subjects")
    public ResponseEntity<Object> getAnnounceSubjects(
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestBody(required = false) SubjectFilter filter) {
        List<AnnounceSubjectProjection> subjectsAnnounce;

        int pos = (page - 1) * 10;

        subjectsAnnounce = subjectRepository.findAccessAnnounceSubject(pos, filter);

        List<AnnounceSubject> subjects = new ArrayList<>();
        List<String> tags;
        for (AnnounceSubjectProjection temp : subjectsAnnounce) {
            tags = tagSubjectRepository.findBySubject(temp.getId());
            UserEntity teacher = userRepository.findById(temp.getTeacher_id()).orElse(null);
            subjects.add(new AnnounceSubject(temp, teacher.getFirst_name(),
                    teacher.getLast_name(), tags.toArray(new String[tags.size()])));
        }
        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("subjects", subjects);
        response.put("count_pages", subjectRepository.countAnnounceSubject(filter));
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("get-detail-announce-subject")
    public ResponseEntity<Object> getAnnounceSubjects(@RequestParam long id) {
        SubjectEntity subject = subjectRepository.findById(id).orElse(null);
        if (subject == null) {
            return ResponseService.notFound();
        }
        int durationExam = JSONService.getTime(subject.getExam());
        if (subject.getTime_exam_end() < System.currentTimeMillis() + 10_800_000 + durationExam) {
            return ResponseService.failed("late");
        }

        UserEntity teacher = userRepository.findById(subject.getTeacher_id()).orElse(null);

        DetailsAnnounceSubject subjectInfo = new DetailsAnnounceSubject(subject, durationExam,
                teacher.getFirst_name(), teacher.getLast_name());

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("subject", subjectInfo);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("create-link-exam")
    public ResponseEntity<Object> createLinkExam(HttpServletRequest req, @RequestBody(required = false) ExaminationRequest request) {
        UserEntity user = AuthUtil.authorizedUser(req);


        if (request == null || request.isNull()) {
            return ResponseService.failed();
        }

        long subject_id = request.getSubjectId();

        SubjectEntity subject = subjectRepository.findById(subject_id).orElse(null);

        if (subject == null) {
            return ResponseService.failed();
        }

        int durationExam = JSONService.getTime(subject.getExam());

        if (subject.getTime_exam_end() < System.currentTimeMillis() + 10_800_000 + durationExam) {
            return ResponseService.failed();
        }

        String email = request.getEmail();

        if (resultExamRepository.existsByEmailAndSubjectId(email, subject_id)) {
            return ResponseService.failed();
        }

        String first_name;
        String last_name;

        user = user == null ? userRepository.findByEmail(email).orElse(null) : null;

        if (user != null) {
            first_name = user.getFirst_name();
            last_name = user.getLast_name();
        } else {
            first_name = request.getFirst_name();
            last_name = request.getLast_name();
            String validData = CheckValidService.checkUser(email, first_name, last_name);

            if (!validData.equals("ok")) {
                return ResponseService.failed(validData);
            }
        }

        String uid = UUID.randomUUID().toString();

        ResultExamEntity resultExamEntity = new ResultExamEntity(subject_id, email, first_name, last_name, "", -1, 0, uid, -1);

        resultExamRepository.save(resultExamEntity);

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);

        mailService.sendLinkToExam(first_name, last_name, uid, subject.getTitle(), email);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("start-exam")
    public ResponseEntity<Object> createLinkExam(@RequestParam String uid) {
        if (uid == null) {
            return ResponseService.failed();
        }

        ResultExamEntity resultExam = resultExamRepository.findByUID(uid).orElse(null);

        if (resultExam == null) {
            return ResponseService.failed();
        }

        SubjectEntity subject = subjectRepository.findById(resultExam.getParent_id()).orElse(null);


        int duration = JSONService.getTime(subject.getExam());

        if (subject.getTime_exam_end() < System.currentTimeMillis() + duration + 20_000) {
            return ResponseService.failed("late");
        }

        resultExam.setEnd_time(System.currentTimeMillis() + duration);

        resultExamRepository.save(resultExam);

        Map<String, Object> response = new HashMap<>();
        response.put("exam", JSONService.getQuestions(subject.getExam(), true));
        response.put("ok", true);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("send-answer-exam")
    public ResponseEntity<Object> sendAnswerExam(@RequestBody AnswersTest request) {
        if (request == null) {
            return ResponseService.failed();
        }

        ResultExamEntity resultTest = resultExamRepository.findByUID(request.getUid()).orElse(null);

        if (resultTest == null) {
            return ResponseService.failed();
        }

        SubjectEntity subject = subjectRepository.findById(resultTest.getParent_id()).orElse(null);

        if (subject == null) {
            return ResponseService.failed();
        }

        if (subject.getTime_exam_end() < System.currentTimeMillis()) {
            return ResponseService.failed("late");
        }

        String exam = subject.getExam();
        int passing_grade = JSONService.getPassing_grade(exam);
        double result;

        try {
            result = JSONService.checkAnswers(subject.getExam(), request.getAnswer());
        } catch (RuntimeException ex) {
            return ResponseService.failed();
        }

        if (result < passing_grade) {
            resultExamRepository.delete(resultTest);
        } else {
            int duration = JSONService.getTime(exam);
            long current = System.currentTimeMillis();

            resultTest.setAnswers(request.getAnswer());
            resultTest.setResult(result);
            resultTest.setDuration((int) (current + duration - resultTest.getEnd_time()));
            resultTest.setEnd_time(current);

            resultExamRepository.save(resultTest);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("result", result);
        response.put("passing_grade", passing_grade);
        response.put("ok", true);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("get-candidates")
    public ResponseEntity<Object> getCandidates(HttpServletRequest req, @RequestParam(required = true) long subjectId) {

        UserEntity user = AuthUtil.authorizedUser(req);

        if (user == null) {
            return ResponseService.unauthorized();
        }

        SubjectEntity subject = subjectRepository.findById(subjectId).orElse(null);

        if (subject == null) {
            return ResponseService.failed();
        }

        if (subject.is_start()) {
            return ResponseService.failed("late");
        }

        if (user.getRole() != Role.CHIEF_TEACHER && subject.getTeacher_id() != user.getId()) {
            return ResponseService.failed("no_permission");
        }

        List<Candidate> candidates = resultExamRepository.findCandidatesByParent_id(subjectId);

        Map<String, Object> response = new HashMap<>();
        response.put("candidates", candidates);
        response.put("ok", true);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("get-answers-candidate")
    public ResponseEntity<Object> getAnswersCandidate(HttpServletRequest req, @RequestParam(required = true) long id) {
        UserEntity user = AuthUtil.authorizedUser(req);

        if (user == null) {
            return ResponseService.unauthorized();
        }
        ResultExamEntity resultTest = resultExamRepository.findById(id).orElse(null);

        if (resultTest == null) {
            return ResponseService.failed();
        }

        SubjectEntity subject = subjectRepository.findById(resultTest.getParent_id()).orElse(null);

        if (subject == null) {
            return ResponseService.failed();
        }

        if (subject.getTeacher_id() != user.getId() && user.getRole() != Role.CHIEF_TEACHER) {
            return ResponseService.failed("no_permission");
        }

        String answers = resultTest.getAnswers();

        Map<String, Object> response = new HashMap<>();
        response.put("answers", answers);
        response.put("ok", true);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("reject-candidate")
    public ResponseEntity<Object> responseCandidate(HttpServletRequest req, @RequestParam(required = true) long id) {
        UserEntity user = AuthUtil.authorizedUser(req);

        if (user == null) {
            return ResponseService.unauthorized();
        }
        ResultExamEntity resultTest = resultExamRepository.findById(id).orElse(null);

        if (resultTest == null) {
            return ResponseService.failed();
        }

        SubjectEntity subject = subjectRepository.findById(resultTest.getParent_id()).orElse(null);

        if (subject == null) {
            return ResponseService.failed();
        }

        if (subject.getTeacher_id() != user.getId()) {
            return ResponseService.failed("no_permission");
        }
        mailService.sendExamFail(resultTest.getFirst_name(), user.getLast_name(), subject.getTitle(), resultTest.getEmail());


        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("approve-students")
    public ResponseEntity<Object> approveStudents(HttpServletRequest req, @RequestParam(required = true) long id) {
        UserEntity user = AuthUtil.authorizedUser(req);

        if (user == null) {
            return ResponseService.unauthorized();
        }
        ResultExamEntity resultTest = resultExamRepository.findById(id).orElse(null);

        if (resultTest == null) {
            return ResponseService.failed();
        }

        SubjectEntity subject = subjectRepository.findById(resultTest.getParent_id()).orElse(null);

        if (subject == null || subject.is_start()) {
            return ResponseService.failed();
        }

        if (subject.getTeacher_id() != user.getId()) {
            return ResponseService.failed("no_permission");
        }

        List<Candidate> candidates = resultExamRepository.findCandidatesByParent_id(subject.getId());

        String studentEmail;
        String first_name;
        String last_name;
        String password;
        String subject_title = subject.getTitle();

        UserEntity student;

        for (Candidate candidate : candidates) {
            studentEmail = candidate.getEmail();
            student = userRepository.findByEmail(studentEmail).orElse(null);

            if (student == null) {
                password = UserService.generatePassword();
                first_name = candidate.getFirst_name();
                last_name = candidate.getLast_name();
                student = new UserEntity(studentEmail, passwordEncoder.encode(password), first_name, last_name, Role.STUDENT);
                String valid = CheckValidService.checkUser(student, false);
                if (!valid.equals("ok")) {
                    continue;
                }
                student = userRepository.save(student);

                mailService.sendAuth(first_name, last_name, subject_title, password, studentEmail);
            }

            mailService.sendExamComplete(student.getFirst_name(), student.getLast_name(), subject_title, studentEmail);
            studentSubjectRepository.save(new StudentSubjectEntity(student.getId(), subject.getId()));
        }

        subject.set_start(true);

        long current = System.currentTimeMillis();

        if (current > subject.getTime_start()) {
            subject.setTime_start(current);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        return ResponseEntity.ok().body(response);
    }
}