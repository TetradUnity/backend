package com.tetradunity.server.controllers;

import com.tetradunity.server.entities.EducationMaterialEntity;
import com.tetradunity.server.entities.SubjectEntity;
import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.models.User;
import com.tetradunity.server.repositories.EducationMaterialRepository;
import com.tetradunity.server.repositories.SubjectRepository;
import com.tetradunity.server.services.JSONService;
import com.tetradunity.server.services.ResponseService;
import com.tetradunity.server.utils.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/education")
public class EducationController {

    @Autowired
    private EducationMaterialRepository educationMaterialRepository;
    @Autowired
    private SubjectRepository subjectRepository;

    @PostMapping("create-education-material")
    public ResponseEntity<Object> createEducationMaterial(HttpServletRequest req, @RequestBody EducationMaterialEntity educationMaterial) {
        UserEntity user = AuthUtil.authorizedUser(req);

        if (user == null) {
            return ResponseService.unauthorized();
        }

        if (educationMaterial == null) {

        }

        SubjectEntity subject;

        if (educationMaterial == null ||
                (subject = subjectRepository.findById(educationMaterial.getSubject_id()).orElse(null)) == null
        ) {
            return ResponseService.failed();
        }

        if (user.getId() != subject.getTeacher_id()) {
            return ResponseService.failed("no_permission");
        }

        boolean is_test = educationMaterial.is_test();
        long deadline = educationMaterial.getDeadline();

        if (!(!is_test && deadline == 0)) {
            if (deadline + 1_800_000 < System.currentTimeMillis()) {
                return ResponseService.failed("incorrect_time");
            }
        }

        if (is_test) {
            try {
                educationMaterial.setContent(JSONService.checkTest(educationMaterial.getContent()));
            } catch (RuntimeException ex) {
                return ResponseService.failed();
            }
        } else {
            JSONService.checkFiles(JSONService.checkTest(educationMaterial.getContent()));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("education_material", educationMaterialRepository.save(new EducationMaterialEntity(educationMaterial)));
        return ResponseEntity.ok().body(response);
    }

}