package com.example.exampleproject.configs.annotations;

import com.example.exampleproject.configs.annotations.validators.Base64FileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to validate if a string is a valid base64 encoded file of a specified type.
 * This annotation checks if the string starts with a valid MIME type prefix
 * and contains valid base64 encoded data.
 * <p>
 * Constraints:
 * - The string must start with a valid MIME type prefix (e.g., "data:application/pdf;base64,")
 * - The content after the prefix must be valid base64 encoded data
 * - Null values are considered valid unless enforced otherwise (e.g., with @NotNull)
 * - Empty strings are considered valid unless enforced otherwise (e.g., with @NotBlank)
 */
@Constraint(validatedBy = Base64FileValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Base64FileValidation {

    String message() default "The string is not a valid base64 encoded file.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    
    /**
     * Specifies the allowed MIME types for base64 encoded files.
     * If not specified, defaults to a predefined set of MIME types.
     *
     * @return an array of allowed MIME types for validation
     */
    String[] allowedTypes() default {
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/bmp",
            "image/webp",
            "text/plain",
            "text/csv",
            "application/pdf",
            "application/msword",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" // .xlsx
    };

    /**
     * Maximum file size in megabytes (Default: 5 MB). Internally converted to bytes.
     */
    int maxSizeInMB() default 5;


}