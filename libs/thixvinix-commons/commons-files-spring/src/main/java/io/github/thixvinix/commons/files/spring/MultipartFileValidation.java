package io.github.thixvinix.commons.files.spring;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that a Spring {@code MultipartFile} field/parameter (or a {@code List} of them)
 * has an allowed MIME type, consistent extension, and stays within size/count limits.
 *
 * <h4>Validators:</h4>
 * <ol>
 *   <li><strong>{@link MultipartFileValidator}:</strong> Validates a single {@code MultipartFile}.</li>
 *   <li><strong>{@link MultipartFileListValidator}:</strong> Validates a {@code List<MultipartFile>}.</li>
 * </ol>
 */
@Documented
@Constraint(validatedBy = {MultipartFileValidator.class, MultipartFileListValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MultipartFileValidation {

    String message() default "The file is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Allowed MIME types for the file(s).
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
     * Maximum file size in megabytes (Default: 2 MB).
     */
    int maxSizeInMB() default 2;

    /**
     * Maximum file count in the list (Default: 5 files).
     */
    int maxFileCount() default 5;

    /**
     * Maximum total size in megabytes for all files combined (Default: 10 MB).
     * Set to 0 to disable total size validation.
     */
    int maxTotalSizeMB() default 10;
}
