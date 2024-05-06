package com.example.server.controllers;

import com.example.server.entities.StudentSubjectEntity;
import com.example.server.entities.SubjectEntity;
import com.example.server.entities.UserEntity;
import com.example.server.models.Role;
import com.example.server.models.Subject;
import com.example.server.repositories.StudentSubjectRepository;
import com.example.server.repositories.SubjectRepository;
import com.example.server.repositories.UserRepository;
import com.example.server.services.ResponseService;
import com.example.server.utils.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public ResponseEntity<Object> createSubject(HttpServletRequest req, @RequestBody Subject subject){
        UserEntity user = AuthUtil.authorizedUser(req);

        if(user == null){
            return ResponseService.unauthorized();
        }

        if(user.getRole() == Role.chief_teacher){
            if(subject.getTeacherId() == 0 || subject.getTitle() == null){
                return ResponseService.failed();
            }

            SubjectEntity subjectEntity = subjectRepository.save(new SubjectEntity(subject));
            long subjectId = subjectEntity.getId();

            Set<Long> userId = subject.getStudentsId();

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
}
