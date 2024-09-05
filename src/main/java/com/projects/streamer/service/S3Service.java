package com.projects.streamer.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.projects.streamer.controller.exception.FileUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
@Slf4j
public class S3Service {

    @Value("${s3.bucket}")
    private String bucket;

    @Autowired
    private AmazonS3 s3Service;

    public String uploadFile(MultipartFile file, String fileName) throws IOException, FileUploadException {
        byte[] byteFile = file.getBytes();

        log.info("Setting up metadata for put object request to S3");
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(byteFile.length);
        metadata.setContentType("video/mp4");

        log.info("Converting the multipart file to byte stream");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteFile);
        PutObjectResult fileUploadResult = s3Service.putObject(bucket, fileName, byteArrayInputStream, metadata);

        if(fileUploadResult == null) {
            log.error("Failed to upload file to S3");
            throw new FileUploadException();
        }

        log.info("Successfully uploaded file to s3 bucket");
        return String.valueOf(s3Service.getUrl(bucket, fileName));
    }

}
