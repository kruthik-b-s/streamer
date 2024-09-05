package com.projects.streamer.validator;

import com.projects.streamer.decorator.ValidFile;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashSet;

@Slf4j
public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    @Override
    public void initialize(ValidFile constraintAnnotation) { }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        String contentType = multipartFile.getContentType();
        if(contentType == null) {
            log.error("Content type not found for the request, Handing over to exception handler");
            throw new MultipartException("Please select at-least one file.");
        }
        if (!isSupportedContentType(contentType)) {
            log.error("Content type not supported, Handing over to exception handler");
            throw new ConstraintViolationException("Invalid file type. Only video files are allowed.", new HashSet<>());
        }
        log.info("File input validated successfully");
        return true;
    }

    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("video/mp4")
                || contentType.equals("video/MP2T")
                || contentType.equals("video/3gpp")
                || contentType.equals("video/quicktime")
                || contentType.equals("video/x-msvideo")
                || contentType.equals("video/x-ms-wmv")
                || contentType.equals("video/mpeg")
                || contentType.equals("application/mp4")
                || contentType.equals("application/x-mpegURL");
    }

}