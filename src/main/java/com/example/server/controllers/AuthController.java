package com.example.server.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
	@GetMapping("/main")
	public String sfg(@RequestParam(defaultValue = "maks", required = false, name = "name") String name){
		return "Hello " + name + "!";
	}
}