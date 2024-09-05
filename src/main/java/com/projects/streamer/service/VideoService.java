package com.projects.streamer.service;

import com.projects.streamer.controller.exception.FileUploadException;
import com.projects.streamer.entity.Video;
import com.projects.streamer.repository.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.Instant;

@Service
@Slf4j
public class VideoService {

    @Autowired
    S3Service s3Service;

    @Autowired
    EcsService ecsService;

    @Autowired
    VideoRepository videoRepository;

    public String uploadVideoFile(MultipartFile file) throws FileUploadException, IOException {
        if (file.isEmpty()) {
            log.error("Uploaded empty file");
            throw new FileUploadException();
        }

        log.info("Generating file name...");
        String generatedFileName = generateFileName(file.getOriginalFilename());

        /*
        * ClassPathResource resource = new ClassPathResource("uploads/");
        * long bytesWritten = Files.copy(file.getInputStream(), Path.of(resource.getPath() + generatedFileName));
        *
        * if (bytesWritten <= 0) {
        *   log.error("Failed to write to file");
        *   throw new FileUploadException();
        * }
        */

        String fileURL = s3Service.uploadFile(file, "uploads/" + generatedFileName);
        // TODO: retry logic in case of failure
        ecsService.handleContainer(generatedFileName);
        // TODO: execute based on the task status
        createVideoRecord(file, generatedFileName);

        log.info("File {} uploaded successfully with name {}", file.getOriginalFilename(), generatedFileName);
        return "File " + file.getOriginalFilename() + " uploaded successfully.";
    }

    private String generateFileName(String originalFilename) {
        return Instant.now().getEpochSecond() + "_" + originalFilename;
    }

    private void createVideoRecord(MultipartFile file, String generatedFileName) {
        Video video = new Video();
        video.setFileName(generatedFileName);
        video.setFileType(file.getContentType());
        log.info("Creating a record for the uploaded file in database...");
        // TODO: check for entity manager sessions (getting error prepared statement s_1 already exist)
        videoRepository.save(video);
    }

}
