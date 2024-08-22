package com.projects.streamer.controller;

import com.projects.streamer.controller.exception.FileUploadException;
import com.projects.streamer.response.GlobalResponse;
import com.projects.streamer.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("api/video")
public class VideoController {

    @Autowired
    VideoService videoService;

    @PostMapping
    private ResponseEntity<GlobalResponse> uploadVideoFile(@RequestParam("file") MultipartFile file) throws FileUploadException, IOException {
        GlobalResponse response = GlobalResponse.builder()
                .statusCode(HttpStatus.OK)
                .message(videoService.uploadVideoFile(file))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
