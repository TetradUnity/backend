package com.tetradunity.server.controllers;

import com.tetradunity.server.services.ResponseService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return ResponseService.failed("incorrect_data");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return ResponseService.notFound();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        System.err.println("[SQL ERROR]");

        return ResponseService.failed("server_error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException e) {
        return ResponseService.notFound();
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleThrowable(Throwable e) {
        System.err.println("[SERVER ERROR]");
        e.printStackTrace(System.err);

        return ResponseService.failed("server_error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}