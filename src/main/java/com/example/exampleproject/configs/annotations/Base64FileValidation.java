package com.example.exampleproject.configs.annotations;

import com.example.exampleproject.configs.annotations.validators.Base64FileListValidator;
import com.example.exampleproject.configs.annotations.validators.Base64FileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation to validate base64 encoded files. This can be used for fields or parameters
 * where base64 encoded content is expected, ensuring that the content meets the specified
 * criteria for format, MIME type, and size.
 * <p>
 * Constraints:
 * - Ensures the content is a valid base64 encoded file.
 * - Checks if the MIME type of the file is included in the `allowedTypes` array.
 * - Verifies that the file size does not exceed `maxSizeInMB`.
 * <p>
 * This annotation is supported for single base64 strings as well as lists of base64 strings.
 */
@Constraint(validatedBy = {Base64FileValidator.class, Base64FileListValidator.class})
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