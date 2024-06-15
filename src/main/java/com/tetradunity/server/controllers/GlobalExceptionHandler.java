package com.tetradunity.server.controllers;

import com.amazonaws.SdkClientException;
import com.tetradunity.server.services.ResponseService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Object> handlerMultipartException(MultipartException e) {
        return ResponseService.notFound();
    }

    @ExceptionHandler(SdkClientException.class)
    public ResponseEntity<Object> handlerSdkClientException(SdkClientException e) {
        System.out.println("[amazon_error]");
        return ResponseService.notFound();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handlerMissingServletRequestParameterException(MissingServletRequestParameterException e) {
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