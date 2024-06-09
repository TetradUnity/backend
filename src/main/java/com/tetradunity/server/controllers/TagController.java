package com.tetradunity.server.controllers;

import com.tetradunity.server.entities.TagEntity;
import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.models.general.Role;
import com.tetradunity.server.repositories.TagRepository;
import com.tetradunity.server.services.ResponseService;
import com.tetradunity.server.utils.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("tag")
public class TagController {
    @Autowired
    private TagRepository tagRepository;

    @GetMapping("find-tags-prefix")
    public ResponseEntity<Object> findTagsPrefix(@RequestParam String prefix){
        if(prefix.length() < 2){
            ResponseService.notFound();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("tags", tagRepository
                .findOptionByPrefixTag(prefix)
                .stream()
                .map(TagEntity::getTag)
                .collect(Collectors.toList()));
        return ResponseEntity.ok().body(response);
    }
}
