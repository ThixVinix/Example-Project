package io.github.thixvinix.commons.files;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a field or parameter is a valid base64-encoded file string, or a
 * {@code List}/{@code Map} of them, checking format, MIME type, size, count and duplicates.
 *
 * <h4>Validators:</h4>
 * <ol>
 *   <li><strong>{@link Base64FileValidator}:</strong> Validates a single String field.</li>
 *   <li><strong>{@link Base64FileListValidator}:</strong> Validates a {@code List<String>} of base64-encoded files.</li>
 *   <li><strong>{@link Base64FileMapValidator}:</strong> Validates a {@code Map<String, String>} of filename to base64 content.</li>
 * </ol>
 */
@Documented
@Constraint(validatedBy = {Base64FileValidator.class, Base64FileListValidator.class, Base64FileMapValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Base64FileValidation {

    String message() default "The string is not a valid base64 encoded file.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Allowed MIME types for base64 encoded files.
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
    int maxSizePerFileInMB() default 2;

    /**
     * Maximum file count in the list (Default: 5 files).
     */
    int maxFileCount() default 5;

    /**
     * Maximum total size in megabytes for all files combined (Default: 10 MB).
     * Set to 0 to disable total size validation.
     */
    int maxTotalSizeInMB() default 10;

}
