package com.tetradunity.server.controllers;

import com.tetradunity.server.services.ResponseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleException1() {
        return ResponseService.failed("server_error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}