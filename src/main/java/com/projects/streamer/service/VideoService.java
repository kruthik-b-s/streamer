package com.projects.streamer.service;

import com.projects.streamer.controller.exception.FileUploadException;
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

    /**
     * Uploads a video file to the aws s3.
     *
     * <p>This method uploads the provided {@link MultipartFile} to s3 bucket.
     * It generates a new filename for the file and attempts to write it to the specified s3 bucket.
     * If the file is empty or if writing the file fails, it throws a {@link FileUploadException}.</p>
     *
     * @param file The {@link MultipartFile} object representing the video file to be uploaded.
     * @return A {@link String} message indicating the success of the file upload.
     * @throws FileUploadException If the file is empty or if there is an error during the file upload process.
     * @throws IOException If an I/O error occurs while writing the file to S3.
     */
    public String uploadVideoFile(MultipartFile file) throws FileUploadException, IOException {
        if (file.isEmpty()) {
            log.error("Uploaded empty file");
            throw new FileUploadException();
        }

        String generatedFileName = generateFileName(file.getOriginalFilename());

        /*
        * Uncomment this code to use local uploads directory for file uploading.
        * Create an uploads directory before execution
        *
        * ClassPathResource resource = new ClassPathResource("uploads/");
        * long bytesWritten = Files.copy(file.getInputStream(), Path.of(resource.getPath() + generatedFileName));
        *
        * if (bytesWritten <= 0) {
        *   log.error("Failed to write to file");
        *   throw new FileUploadException();
        * }
        */

        // Uploads file to S3
        String fileURL = s3Service.uploadFile(file, "uploads/" + generatedFileName);

        log.info("File {} uploaded successfully with name {}", file.getOriginalFilename(), generatedFileName);
        return "File " + file.getOriginalFilename() + " uploaded successfully. Can be accessed through " + fileURL;
    }

    /**
     * Generates a new filename based on the current timestamp and the original filename.
     *
     * <p>The generated filename consists of the current epoch second timestamp followed by the original filename,
     * separated by an underscore. This ensures that each file has a unique name based on the time it was processed.</p>
     *
     * @param originalFilename The original name of the file to be included in the generated filename.
     * @return A {@link String} representing the new filename that includes a timestamp and the original filename.
     */
    private String generateFileName(String originalFilename) {
        return Instant.now().getEpochSecond() + "_" + originalFilename;
    }

}
