package com.projects.streamer.response;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record GlobalResponse(HttpStatus statusCode, String message) { }
