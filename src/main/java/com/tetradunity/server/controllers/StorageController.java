package com.tetradunity.server.controllers;

import com.tetradunity.server.services.ResponseService;
import com.tetradunity.server.services.StorageService;
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
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder") String folder) {

        String path = storageService.uploadFile(file, folder);

        if (path == null) {
            return ResponseService.failed("storage_error");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("path", path);
        return ResponseEntity.ok(response);
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
