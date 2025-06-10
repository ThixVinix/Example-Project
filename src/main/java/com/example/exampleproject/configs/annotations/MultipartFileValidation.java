package com.example.exampleproject.configs.annotations;

import com.example.exampleproject.configs.annotations.validators.MultipartFileArrayValidator;
import com.example.exampleproject.configs.annotations.validators.MultipartFileListValidator;
import com.example.exampleproject.configs.annotations.validators.MultipartFileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to validate whether the provided MultipartFile field or parameter adheres 
 * to specific constraints such as file type, size, and count.
 * This annotation can be applied to MultipartFile fields or lists of MultipartFile.
 *
 * <h4>Validation ensures the following:</h4>
 * <ul>
 *   <li>The file's MIME type is within an allowed set of types.</li>
 *   <li>The file size does not exceed a specified maximum limit.</li>
 *   <li>If the field or parameter is a collection, the number of files does not exceed the defined maximum count.</li>
 * </ul>
 *
 * <h4>Validators:</h4>
 * <ol>
 *   <li><strong>{@link MultipartFileValidator}:</strong> Validates single MultipartFile fields.</li>
 *   <li><strong>{@link MultipartFileListValidator}:</strong> Validates lists of MultipartFile objects.</li>
 * </ol>
 *
 * <h4>Attributes:</h4>
 * <ul>
 *   <li><strong>message:</strong> The error message template to display upon validation failure.</li>
 *   <li><strong>groups:</strong> Defines validation groups, allowing selective application of rules.</li>
 *   <li><strong>payload:</strong> Provides custom metadata for validation.</li>
 *   <li><strong>allowedTypes:</strong> Specifies a set of permissible MIME types for the file.</li>
 *   <li><strong>maxSizeInMB:</strong> Sets the maximum allowed size of a file in megabytes.</li>
 *   <li><strong>maxFileCount:</strong> Specifies the maximum number of files allowed when validating collections.</li>
 * </ul>
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
     * Specifies the allowed MIME types for files.
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
     * Maximum file size in megabytes (Default: 2 MB). Internally converted to bytes.
     */
    int maxSizeInMB() default 2;

    /**
     * Maximum file count in the list (Default: 5 files).
     */
    int maxFileCount() default 5;
}
