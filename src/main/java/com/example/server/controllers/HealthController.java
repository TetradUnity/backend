package com.example.server.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class HealthController {
    @GetMapping("health-check")
    public ResponseEntity<Object> method(){
        HashMap<String, Object> resp = new HashMap<>();
        resp.put("ok", true);
        return ResponseEntity.ok().body(resp);
    }
}
