package com.tetradunity.server.controllers;

import com.tetradunity.server.entities.StudentSubjectEntity;
import com.tetradunity.server.entities.SubjectEntity;
import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.models.Role;
import com.tetradunity.server.models.Subject;
import com.tetradunity.server.repositories.StudentSubjectRepository;
import com.tetradunity.server.repositories.SubjectRepository;
import com.tetradunity.server.repositories.UserRepository;
import com.tetradunity.server.services.ResponseService;
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
    public ResponseEntity<Object> createSubject(HttpServletRequest req, @RequestBody SubjectEntity subject){
        UserEntity user = AuthUtil.authorizedUser(req);

        if(user == null){
            return ResponseService.unauthorized();
        }

        if(user.getRole() == Role.chief_teacher){
            if(subject.getTeacherId() == 0 || subject.getTitle() == null ||
                subject.getExamEnd() == null || subject.getStart() == null){
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
                    JSONArray questions = new JSONArray(subject.getExam());
                    String title;
                    for(JSONObject question : questions){
                        title = question.getString("title");
                        if(title.equals(title.trim())){
                            throw new RuntimeException();
                        }
                        switch (question.getString("type")) {
                            case "MULTY_ANSWER":
                                JSONArray answers = question.getJSONArray("answer");
                                Set<JSONObject> setAnswers = new TreeSet<>();
                                JSONObject answer;
                                for(int i = 0; i < answers.length; i++){
                                    answer = answers.getJSONObject(i);
                                    if(answer.has("title")){
                                        answer.getString("title");
                                    }
                                    if(answer.has("imgSource")){
                                        answer.getString("imgSource");
                                    }
                                    else{
                                        if(!answer.has("title")){
                                            throw new RuntimeException();
                                        }
                                    }
                                    if(setAnswers.contains(answer)){
                                        answers.remove(i--);
                                    }
                                    else{
                                        setAnswers.add(answer);
                                    }
                                }
                                if(answers.length() <= 1){
                                    throw new RuntimeException();
                                }
                                JSONArray rightAnswers = question.getJSONArray("rightAnswers");
                                Set<Integer> setRightAnswers = new TreeSet<>();
                                int rightAnswer;
                                for(int i = 0; i < rightAnswers.length; i++){
                                    rightAnswer = rightAnswers.get(i);
                                    if(setRightAnswers.contains(rightAnswer)){
                                        rightAnswers.remove(i--);
                                    }
                                    else{
                                        setRightAnswers.add(rightAnswer);
                                    }
                                }
                                if(rightAnswers.length() <= 1 || rightAnswers.length() > answers.length()){
                                    throw new RuntimeException();
                                }
                            case "ONE_ANSWER":
                                if(rightAnswers != 1){
                                    throw new RuntimeException();
                                }
                                break;
                            case "TEXT":
                                JSONArray rightAnswers = question.getJSONArray("rightAnswer");
                                Set<String> setAnswers = new TreeSet<>();
                                String answer;
                                for(int i = 0; i < rightAnswers.length(); i++){
                                    answer = rightAnswers.getString(i);
                                    if(answer.equals(answer.trim())){
                                        rightAnswers.remove(i);
                                    }
                                    else{
                                        if(setAnswers.contains(answer)){
                                            rightAnswers.remove(i--);
                                        }
                                        else{
                                            setAnswers.add(rightAnswers);
                                        }
                                    }
                                }
                                if(rightAnswers.length < 1){
                                    throw new RuntimeException();
                                }
                                break;  
                            default:
                                throw new RuntimeException();
                                break;
                        }
                    }
                }catch(Exception e){
                    return ResponseService.failed();
                }
            }

            subjectRepository.save(subject);

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
