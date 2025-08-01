package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.Base64FileValidation;
import com.example.exampleproject.configs.annotations.validators.base.AbstractFileValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.tika.Tika;

import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Validator for base64-encoded files, ensuring compliance with specified constraints such as
 * format, MIME type, and size. This class validates single String fields annotated with
 * {@link Base64FileValidation}.
 * <p>
 * This validator validates base64 content by:
 * - Checking if the content matches a valid base64 file format.
 * - Verifying that the decoded file size does not exceed the specified maximum file size (in MB).
 * - Ensuring that the MIME type of the file matches one of the allowed types.
 * <p>
 * The validator logs warnings and errors for invalid configurations or unexpected scenarios
 * and provides custom validation error messages for invalid base64 file inputs.
 * <p>
 * Implements the {@link ConstraintValidator} interface for validation logic, bound to
 * {@link Base64FileValidation}.
 */
@Slf4j
public class Base64FileValidator
        extends AbstractFileValidator implements ConstraintValidator<Base64FileValidation, String> {

    private static final Pattern BASE64_FILE_PATTERN =
            Pattern.compile("^data:[a-zA-Z0-9.+-]+/[a-zA-Z0-9.+-]+;base64,.*");

    private Tika tika;

    @Override
    public void initialize(Base64FileValidation annotation) {
        this.allowedTypes = annotation.allowedTypes();
        this.maxSizeInMB = validateMaxSizeInMB(annotation.maxSizePerFileInMB());
        this.tika = new Tika();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isNullOrEmpty(value)) {
            return true;
        }

        if (!BASE64_FILE_PATTERN.matcher(value).matches()) {
            addConstraintViolation(context, "msg.validation.request.field.base64file.invalid.format");
            return false;
        }

        String base64Content = value.substring(value.indexOf(",") + NumberUtils.INTEGER_ONE);

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Content);

            if (!validateFileSize(decodedBytes.length, context,
                    "msg.validation.request.field.base64file.invalid.size")) {
                return false;
            }

            String detectedMimeType = tika.detect(decodedBytes);

            if (isMimeTypeNotAllowed(detectedMimeType)) {
                log.warn("The MIME detected type ({}) is not allowed. Expected types: {}",
                        detectedMimeType, String.join(", ", allowedTypes));
                addConstraintViolation(context,
                        "msg.validation.request.field.base64file.invalid.detected.type",
                        detectedMimeType,
                        String.join(", ", allowedTypes)
                );
                return false;
            }

            return true;
        } catch (IllegalArgumentException e) {
            log.debug("Base64 Invalid Content: {}", e.getMessage());
            addConstraintViolation(context, "msg.validation.request.field.base64file.invalid.content");
            return false;
        } catch (Exception e) {
            log.error("Error when detecting mime type using Apache Tika: {}", e.getMessage(), e);
            addConstraintViolation(context, "msg.validation.request.field.base64file.invalid.general");
            return false;
        }
    }
}
