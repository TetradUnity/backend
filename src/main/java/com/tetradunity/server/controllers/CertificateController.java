package com.tetradunity.server.controllers;

import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.models.certificates.Certificate;
import com.tetradunity.server.models.general.Role;
import com.tetradunity.server.repositories.CertificateRepository;
import com.tetradunity.server.repositories.UserRepository;
import com.tetradunity.server.services.ResponseService;
import com.tetradunity.server.utils.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("certificate")
public class CertificateController {
    @Autowired
    private CertificateRepository certificateRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("get-certificates")
    public ResponseEntity<Object> getCertificates(@RequestParam long student_id){
        UserEntity user = userRepository.findById(student_id).orElse(null);

        if(user == null || user.getRole() != Role.STUDENT){
            return ResponseService.failed();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("certificates", certificateRepository.findAllCertificates(student_id)
                .stream()
                .map(Certificate::new)
                .collect(Collectors.toList()));
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("check-certificate")
    public ResponseEntity<Object> checkCertificate(@RequestParam String uid) {
        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("isCorrect", certificateRepository.certificateExists(UUID.fromString(uid)));
        return ResponseEntity.ok().body(response);
    }
}