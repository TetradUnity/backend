package com.tetradunity.server.controllers;

import com.tetradunity.server.entities.StudentSubjectEntity;
import com.tetradunity.server.entities.SubjectEntity;
import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.models.AnnounceSubject;
import com.tetradunity.server.models.Role;
import com.tetradunity.server.models.Subject;
import com.tetradunity.server.models.SubjectCreate;
import com.tetradunity.server.repositories.StudentSubjectRepository;
import com.tetradunity.server.repositories.SubjectRepository;
import com.tetradunity.server.repositories.UserRepository;
import com.tetradunity.server.services.ResponseService;
import com.tetradunity.server.services.CheckValidTestService;
import com.tetradunity.server.utils.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import org.json.JSONObject;

@RestController
@RequestMapping("/subject")
public class SubjectController {
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private StudentSubjectRepository studentSubjectRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("create")
    public ResponseEntity<Object> createSubject(HttpServletRequest req, @RequestBody SubjectCreate subject){
        UserEntity user = AuthUtil.authorizedUser(req);

        if(user == null){
            return ResponseService.unauthorized();
        }

        if(user.getRole() == Role.chief_teacher){
//            if(subject.getTeacherId() == 0 || subject.getTitle() == null ||
//                subject.getExamEnd() == null || subject.getStart() == null){
//                return ResponseService.failed();
//            }

            if(subject.getTeacherEmail() == null || subject.getTitle() == null ||
                    subject.getExamEnd() == 0 || subject.getStart() == 0){
                return ResponseService.failed();
            }

            if(System.currentTimeMillis() + 259_199_999 > subject.getExamEnd() &&
                subject.getExamEnd() + 86_399_999 > subject.getStart()){
                    return ResponseService.failed("error_time");
                }

            if(subject.getDescription() == null){
                subject.setDescription("");
            }

            if(subject.getExam() == null){
                subject.setExam("");
            }
            else{
                try{
                    subject.setExam(CheckValidTestService.check(subject.getExam()));
                }catch(Exception e){
                    return ResponseService.failed();
                }
            }

            UserEntity teacher = userRepository.findByEmail(subject.getTeacherEmail).orElse(null);

            if(teacher == null){
                return ResponseService.failed();
            }

            subjectRepository.save(new SubjectEntity(subject, teacher.getId()));

            Map<String, Object> response = new HashMap<>();
            response.put("ok", true);
            return ResponseEntity.ok().body(response);
        }
        return ResponseService.failed("no_permission");
    }

   @GetMapping("get")
   public ResponseEntity<Object> getSubject(HttpServletRequest req, @RequestBody long subjectId){
       UserEntity user = AuthUtil.authorizedUser(req);

       if(user == null){
           return ResponseService.unauthorized();
       }

       SubjectEntity subjectEntity = subjectRepository.findById(subjectId).orElse(null);

       if(subjectEntity == null){
           return ResponseService.failed();
       }

       if(studentSubjectRepository.findByStudentIdAndSubjectId(user.getId(), subjectId).isPresent() ||
           subjectEntity.getTeacherId() == user.getId() || user.getRole() == Role.chief_teacher
       ){
           UserEntity teacher = userRepository.findById(subjectEntity.getTeacherId()).orElse(null);

           if(teacher == null){
               return ResponseService.failed();
           }

           List<UserEntity> students = new ArrayList<>();
           List<StudentSubjectEntity> students = studentSubjectRepository.findBySubjectId(subjectId);
           UserEntity student;
           for(StudentSubjectEntity studentSubject : students){
            if((student = userRepository.findById(studentSubject.getId()).orElse(null)) != null){
                studentsId.add(student);
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
   public ResponseEntity<Object> getAnnounceSubjects(){
        List<SubjectEntity> subjectsEntity = subjectRepository.findAccessAnnounceSubject();
        List<AnnounceSubject> subjects = new ArrayList<>();
        for(SubjectEntity temp : subjectsEntity){
            subjects.add(new AnnounceSubject(temp));
        }
        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("subjects", subjects);
        return ResponseEntity.ok().body(response);
   }
   
}
