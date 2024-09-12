package com.projects.streamer.controller;

import com.projects.streamer.controller.exception.FileUploadException;
import com.projects.streamer.decorator.ValidFile;
import com.projects.streamer.response.GlobalResponse;
import com.projects.streamer.service.EcsService;
import com.projects.streamer.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("api/video")
@Slf4j
public class VideoController {

    @Autowired
    VideoService videoService;

    @Autowired
    EcsService ecsService;

    @PostMapping
    private ResponseEntity<GlobalResponse<Map<String, String>>> uploadVideoFile(@Validated @ValidFile @RequestParam("file") MultipartFile file) throws FileUploadException, IOException {
        log.info("Entered inside the controller POST /api/video");
        GlobalResponse<Map<String, String>> response = GlobalResponse.<Map<String, String>>builder()
                .statusCode(HttpStatus.OK)
                .message("Video file will be uploaded shortly")
                .data(videoService.uploadVideoFile(file))
                .build();
        log.info("Upload video service completed and returned back to controller");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/logs", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamLogs(@RequestParam String taskArn) {
        log.info("Started streaming aws cloudwatch logs");
        return ecsService.streamLogs(taskArn);
    }

}
