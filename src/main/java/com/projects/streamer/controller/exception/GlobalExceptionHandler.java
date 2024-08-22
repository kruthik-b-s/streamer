package com.projects.streamer.controller.exception;

import com.projects.streamer.response.GlobalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({ FileUploadException.class, InternalError.class })
    public final ResponseEntity<GlobalResponse> handleException(Exception ex, WebRequest request) {
        if (ex instanceof FileUploadException) {
            GlobalResponse response = GlobalResponse.builder()
                    .statusCode(HttpStatus.BAD_REQUEST)
                    .message("Failed to upload video file. Please check if the file is empty or try uploading again.")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else {
            if (log.isWarnEnabled()) {
                log.warn("Unknown exception type: {}", ex.getClass().getName());
            }

            GlobalResponse response = GlobalResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Internal Server Error")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
