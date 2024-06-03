package com.tetradunity.server.controllers;

import com.tetradunity.server.services.ResponseService;
import com.tetradunity.server.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Object> downloadFile(@PathVariable String fileName) {
        byte[] fileData = storageService.downloadFile(fileName);
        return ResponseEntity.ok()
                .body(fileData);
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<Object> deleteFile(@PathVariable String fileName) {
        storageService.deleteFile(fileName);
        return ResponseEntity.ok("File deleted successfully: " + fileName);
    }

}
