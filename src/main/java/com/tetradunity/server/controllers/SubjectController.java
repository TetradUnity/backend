package com.tetradunity.server.controllers;

import com.tetradunity.server.entities.*;
import com.tetradunity.server.models.*;
import com.tetradunity.server.repositories.*;
import com.tetradunity.server.services.CheckValidService;
import com.tetradunity.server.services.JSONService;
import com.tetradunity.server.services.MailService;
import com.tetradunity.server.services.ResponseService;
import com.tetradunity.server.utils.AuthUtil;
import com.tetradunity.server.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    private ResultTestRepository resultTestRepository;
    @Autowired
    private MailService mailService;


    @PostMapping("create")
    public ResponseEntity<Object> createSubject(HttpServletRequest req,
                                                @RequestBody(required = false) SubjectCreate subject){
        UserEntity user = AuthUtil.authorizedUser(req);

        if(user == null){
            return ResponseService.unauthorized();
        }

        if(subject == null){
            return ResponseService.failed();
        }

        if(user.getRole() == Role.chief_teacher){
            if(subject.getTeacher_email() == null || subject.getTitle() == null ||
                    subject.getExam_end() == 0 || subject.getStart() == 0 ||
                    subject.getShort_description() == null || subject.getDuration() == 0 ||
                    subject.getTimetable() == null || subject.getTags() == null){
                return ResponseService.failed();
            }

            UserEntity teacher = userRepository.findByEmail(subject.getTeacher_email()).orElse(null);

            if(teacher == null){
                return ResponseService.failed("teacher_not_exists");
            }

            if(System.currentTimeMillis() + 259_199_999 > subject.getExam_end() &&
                subject.getExam_end() + 86_399_999 > subject.getStart() &&
                subject.getDuration() < 259_199_999){
                    return ResponseService.failed("error_time");
                }

            String[] tags = subject.getTags();

            for(String tag : tags){
                if(tagRepository.findByTag(tag).isEmpty()){
                    return ResponseService.failed("tag_no_exists");
                }
            }

            if(subject.getExam() == null){
                subject.setExam("");
            }
            else{
                try{
                    subject.setExam(JSONService.checkTest(subject.getExam()));
                }catch(Exception e){
                    return ResponseService.failed();
                }
            }

            if(subject.getDescription() == null){
                subject.setDescription("");
            }


            SubjectEntity subjectEntity;

            Map<String, Object> response = new HashMap<>();
            response.put("ok", true);
            response.put("subject", subjectEntity = subjectRepository.save(new SubjectEntity(subject, teacher.getId())));

            long id = subjectEntity.getId();

            for(String tag : tags){
                tagSubjectRepository.save(new TagSubjectEntity(id, tag));
            }

            return ResponseEntity.ok().body(response);
        }
        return ResponseService.failed("no_permission");
    }

   @GetMapping("get")
   public ResponseEntity<Object> getSubject(HttpServletRequest req,
                                            @RequestParam long subjectId){
       UserEntity user = AuthUtil.authorizedUser(req);

       if(user == null){
           return ResponseService.unauthorized();
       }

       SubjectEntity subjectEntity = subjectRepository.findById(subjectId).orElse(null);

       if(subjectEntity == null){
           return ResponseService.failed();
       }

       if(studentSubjectRepository.findByStudentIdAndSubjectId(user.getId(), subjectId).isPresent() ||
           subjectEntity.getTeacher_id() == user.getId() || user.getRole() == Role.chief_teacher
       ){
           UserEntity teacher = userRepository.findById(subjectEntity.getTeacher_id()).orElse(null);

           if(teacher == null){
               return ResponseService.failed();
           }

           List<UserEntity> students = new ArrayList<>();
           List<StudentSubjectEntity> studentsSubject = studentSubjectRepository.findBySubjectId(subjectId);
           UserEntity student;
           for(StudentSubjectEntity studentSubject : studentsSubject){
            if((student = userRepository.findById(studentSubject.getStudentId()).orElse(null))
                    != null){
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

   @GetMapping("get-announce-subjects")
   public ResponseEntity<Object> getAnnounceSubjects(
           @RequestParam(name = "page", required = false, defaultValue = "1") int page){
        List<SubjectEntity> subjectsEntity = subjectRepository.findAccessAnnounceSubject(page);
        List<AnnounceSubject> subjects = new ArrayList<>();
        List<String> tags;
        for(SubjectEntity temp : subjectsEntity){
            tags = tagSubjectRepository.findBySubject(temp.getId());
            UserEntity teacher = userRepository.findById(temp.getId()).orElse(null);
            subjects.add(new AnnounceSubject(temp, teacher.getFirst_name(),
                    teacher.getLast_name(), tags.toArray(new String[tags.size()])));
        }
        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("subjects", subjects);
        return ResponseEntity.ok().body(response);
   }

   @GetMapping("get-detail-announce-subject")
   public ResponseEntity<Object> getAnnounceSubjects(@RequestParam long id) {
        SubjectEntity subject = subjectRepository.findById(id).orElse(null);
        if(subject == null){
            return ResponseService.failed();
        }
        int durationExam = JSONService.getTime(subject.getExam());
        if(subject.getExam_end() < System.currentTimeMillis() + 10_800_000 + durationExam){
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

   @PostMapping("start-exam")
    public ResponseEntity<Object> startExam(@RequestBody(required = false) ExaminationRequest request){
        if(request == null || request.isNull()){
            return ResponseService.failed();
        }

        long subject_id = request.getSubjectId();

        SubjectEntity subject = subjectRepository.findById(subject_id).orElse(null);

        if(subject == null){
            return ResponseService.failed();
        }
        int durationExam = JSONService.getTime(subject.getExam());

        if(subject.getExam_end() < System.currentTimeMillis() + 10_800_000 + durationExam){
            return ResponseService.failed();
        }

        String email = request.getEmail();
        String first_name = request.getFirst_name();
        String last_name = request.getLast_name();

        if(resultTestRepository.existsByEmailAndSubjectId(email, subject_id)){
            return ResponseService.failed();
        }

        String validData = CheckValidService.checkUser(email, first_name, last_name);

        if(!validData.equals("ok")) {
            return ResponseService.failed(validData);
        }

        String uid = UUID.randomUUID().toString();

        ResultTestEntity resultTestEntity = new ResultTestEntity(subject_id, email, first_name, last_name, "", -1, 0, true, uid);

        resultTestRepository.save(resultTestEntity);

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);

        mailService.sendLinkToExam(first_name, last_name, uid, subject.getTitle(), email);

        return ResponseEntity.ok().body(response);
   }
}
