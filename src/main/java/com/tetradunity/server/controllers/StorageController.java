package com.tetradunity.server.controllers;

import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.models.Role;
import com.tetradunity.server.services.ResponseService;
import com.tetradunity.server.services.StorageService;
import com.tetradunity.server.utils.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/storage")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<Object> uploadFile(
            HttpServletRequest req,
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder") String folder) {
        UserEntity user = AuthUtil.authorizedUser(req);
        Role role;

        if (user == null) {
            return ResponseService.failed();
        }
        role = user.getRole();

        if (switch (folder) {
            case "avatars" -> storageService.findRatio(file) == 1;
            case "banners" -> storageService.findRatio(file) == 4.04;
            case "exam_resources" -> role == Role.CHIEF_TEACHER;
            case "education_materials", "education_material_resources" -> role == Role.TEACHER;
            case "homework", "homework_resources" -> role == Role.STUDENT;
            default -> false;
        }) {
            String path = storageService.uploadFile(file, folder);

            if (path == null) {
                return ResponseService.failed("storage_error");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("ok", true);
            response.put("path", path);
            return ResponseEntity.ok(response);
        }
        return ResponseService.failed();
    }

    @GetMapping("/download")
    public ResponseEntity<Object> downloadFile(@RequestParam("path") String path) {
        return ResponseEntity.ok().contentType(MediaType.valueOf(storageService.determineFileType(path))).body(storageService.downloadFile(path));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteFile(@RequestParam("path") String path) {
        storageService.deleteFile(path);
        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        return ResponseEntity.ok(response);
    }

}
