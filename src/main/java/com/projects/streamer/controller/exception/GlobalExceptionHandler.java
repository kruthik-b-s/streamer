package com.projects.streamer.controller.exception;

import com.projects.streamer.response.GlobalResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import java.io.IOException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(FileUploadException.class)
    public final ResponseEntity<GlobalResponse> handleException(Exception ex, WebRequest request) {
        GlobalResponse response = GlobalResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .build();
        log.error("Resolved FileUploadException in handler with message: {}", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MultipartException.class)
    public final ResponseEntity<GlobalResponse> handleMultipartException(MultipartException ex, WebRequest request) throws IOException {
        GlobalResponse response = GlobalResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .build();
        log.error("Resolved MultipartException in handler with message: {}", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<GlobalResponse> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        GlobalResponse response = GlobalResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .build();
        log.error("Resolved ConstraintViolationException in handler with message: {}", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<GlobalResponse> handleOtherException(Exception ex, WebRequest request) {
        GlobalResponse response = GlobalResponse.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Internal server error")
                .build();
        log.error("Resolved {} in handler with message: {}", ex.getClass(), ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
