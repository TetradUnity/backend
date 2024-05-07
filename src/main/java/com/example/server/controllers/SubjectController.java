package com.example.server.controllers;

import com.example.server.entities.StudentSubjectEntity;
import com.example.server.entities.SubjectEntity;
import com.example.server.entities.UserEntity;
import com.example.server.models.Role;
import com.example.server.models.Subject;
import com.example.server.models.SubjectCreate;
import com.example.server.repositories.StudentSubjectRepository;
import com.example.server.repositories.SubjectRepository;
import com.example.server.repositories.UserRepository;
import com.example.server.services.ResponseService;
import com.example.server.utils.AuthUtil;
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

    @PostMapping("create")
    public ResponseEntity<Object> createSubject(HttpServletRequest req, @RequestBody SubjectCreate subjectCreate){
        UserEntity user = AuthUtil.authorizedUser(req);

        if(user == null){
            return ResponseService.unauthorized();
        }

        if(user.getRole() == Role.chief_teacher){
            if(subjectCreate.getTeacherId() == 0 || subjectCreate.getTitle() == null){
                return ResponseService.failed();
            }

            SubjectEntity subjectEntity = subjectRepository.save(new SubjectEntity(subjectCreate));
            long subjectId = subjectEntity.getId();

            Set<Long> userId = subjectCreate.getStudentsId();

            for(long id : userId){
                user = userRepository.findById(id).orElse(null);
                if(user != null && user.getRole() == Role.student){
                    studentSubjectRepository.save(new StudentSubjectEntity(id, subjectId));
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("ok", true);
            return ResponseEntity.ok().body(response);
        }
        return ResponseService.failed("no_permission");
    }

//    @GetMapping("get")
//    public ResponseEntity<Object> getSubject(HttpServletRequest req, @RequestBody long subjectId){
//        UserEntity user = AuthUtil.authorizedUser(req);
//
//        if(user == null){
//            return ResponseService.unauthorized();
//        }
//
//        SubjectEntity subjectEntity = subjectRepository.findById(subjectId).orElse(null);
//
//        if(subjectEntity == null){
//            return ResponseService.failed();
//        }
//
//        if(studentSubjectRepository.findByStudentIdAndSubjectId(user.getId(), subjectId).isPresent() ||
//            subjectEntity.getTeacherId() == user.getId() || user.getRole() == Role.chief_teacher
//        ){
//            UserEntity teacher = userRepository.findById(subjectEntity.getTeacherId()).orElse(null);
//
//            if(teacher == null){
//                return ResponseService.failed();
//            }
//
//            //List<>
//
//            Subject subject = new Subject(subjectEntity, teacher.getFirst_name(), teacher.getLast_name(), );
//        }
//    }
}
