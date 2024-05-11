package com.tetradunity.server.controllers;

import com.tetradunity.server.services.ResponseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleException1(Throwable e) {
        if (!(e instanceof NoResourceFoundException)) {
            System.err.println("[SERVER ERROR]");
            e.printStackTrace(System.err);
        }
        
        return ResponseService.failed("server_error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}