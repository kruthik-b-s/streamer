package com.projects.streamer.controller;

import com.projects.streamer.controller.exception.FileUploadException;
import com.projects.streamer.decorator.ValidFile;
import com.projects.streamer.response.GlobalResponse;
import com.projects.streamer.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("api/video")
@Slf4j
public class VideoController {

    @Autowired
    VideoService videoService;

    @PostMapping
    private ResponseEntity<GlobalResponse> uploadVideoFile(@Validated @ValidFile @RequestParam("file") MultipartFile file) throws FileUploadException, IOException {
        log.info("Entered inside the controller POST /api/video");
        GlobalResponse response = GlobalResponse.builder()
                .statusCode(HttpStatus.OK)
                .message(videoService.uploadVideoFile(file))
                .build();
        log.info("Upload video service completed and returned back to controller");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
