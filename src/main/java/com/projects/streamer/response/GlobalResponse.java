package com.projects.streamer.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GlobalResponse<T>(HttpStatus statusCode, String message, T data) { }
