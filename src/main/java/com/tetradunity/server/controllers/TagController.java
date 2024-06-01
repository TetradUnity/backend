package com.tetradunity.server.controllers;

import com.tetradunity.server.entities.TagEntity;
import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.models.Role;
import com.tetradunity.server.repositories.TagRepository;
import com.tetradunity.server.services.ResponseService;
import com.tetradunity.server.utils.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("tag")
public class TagController {
    @Autowired
    private TagRepository tagRepository;

    @PostMapping("create")
    public ResponseEntity<Object> createTag(HttpServletRequest req, @RequestParam(name = "tag", required = true) String tag) {
        UserEntity user = AuthUtil.authorizedUser(req);

        if (user == null) {
            return ResponseService.failed();
        }

        if (user.getRole() != Role.CHIEF_TEACHER) {
            return ResponseService.failed();
        }

        if (tag == null || tagRepository.existsTag(tag)) {
            return ResponseService.failed("tag_exists");
        }

        tagRepository.save(new TagEntity(tag));

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("get-tags")
    public ResponseEntity<Object> getTags() {
        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("tags", tagRepository.findAll().toArray());
        return ResponseEntity.ok().body(response);
    }
}
