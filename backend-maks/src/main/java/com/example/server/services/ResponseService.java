package com.example.server.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;

public class ResponseService {
	static public ResponseEntity<Object> failed(String error, HttpStatus httpStatus) {
		HashMap<String, Object> response = new HashMap<>();

		response.put("ok", false);
		response.put("error", error);

		return ResponseEntity
				.status(httpStatus)
				.body(response);
	}

	public static ResponseEntity<Object> unauthorized() {
		return failed("unauthorized", HttpStatus.UNAUTHORIZED);
	}

	static public ResponseEntity<Object> failed(String error) {
		return failed(error, HttpStatus.BAD_REQUEST);
	}

	static public ResponseEntity<Object> failed(){return failed("incorrect_data");}
}