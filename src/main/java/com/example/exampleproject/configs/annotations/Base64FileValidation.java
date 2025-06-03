package com.example.exampleproject.configs.annotations;

import com.example.exampleproject.configs.annotations.validators.Base64FileListValidator;
import com.example.exampleproject.configs.annotations.validators.Base64FileMapValidator;
import com.example.exampleproject.configs.annotations.validators.Base64FileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to validate whether the provided field or parameter is a valid base64 encoded file
 * string(s) adhering to specific constraints such as file type, size, and count.
 * This annotation can be applied to String fields, lists of Strings, or maps containing base64-encoded file content.
 *
 * <h4>Validation ensures the following:</h4>
 * <ul>
 *   <li>The string(s) follows the base64 format for files.</li>
 *   <li>The MIME type of the file(s) is within an allowed set of types.</li>
 *   <li>The file size does not exceed a specified maximum limit.</li>
 *   <li>If the field or parameter is a collection, the number of files does not exceed the defined maximum count.</li>
 * </ul>
 *
 * <h4>Validators:</h4>
 * <ol>
 *   <li><strong>{@link Base64FileValidator}:</strong> Validates single String fields.</li>
 *   <li><strong>{@link Base64FileListValidator}:</strong> Validates lists of base64-encoded files.</li>
 *   <li><strong>{@link Base64FileMapValidator}:</strong> Validates maps containing base64-encoded files as values.</li>
 * </ol>
 *
 * <h4>Attributes:</h4>
 * <ul>
 *   <li><strong>message:</strong> The error message template to display upon validation failure.</li>
 *   <li><strong>groups:</strong> Defines validation groups, allowing selective application of rules.</li>
 *   <li><strong>payload:</strong> Provides custom metadata for validation.</li>
 *   <li><strong>allowedTypes:</strong> Specifies a set of permissible MIME types for the base64 file(s).</li>
 *   <li><strong>maxSizeInMB:</strong> Sets the maximum allowed size of a base64-encoded file in megabytes.</li>
 *   <li><strong>maxFileCount:</strong> Specifies the maximum number of files allowed when validating collections.</li>
 * </ul>
 */
@Constraint(validatedBy = {Base64FileValidator.class, Base64FileListValidator.class, Base64FileMapValidator.class})
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
     * Maximum file size in megabytes (Default: 2 MB). Internally converted to bytes.
     */
    int maxSizeInMB() default 2;

    /**
     * Maximum file count in the list (Default: 5 files).
     */
    int maxFileCount() default 5;


}