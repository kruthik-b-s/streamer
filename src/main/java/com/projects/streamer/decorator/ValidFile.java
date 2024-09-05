package com.projects.streamer.decorator;

import com.projects.streamer.validator.FileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileValidator.class)
public @interface ValidFile {

    String message() default "Invalid file type. Only video files are allowed.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
