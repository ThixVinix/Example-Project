package io.github.thixvinix.commons.files;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

import org.apache.tika.Tika;

import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Validator for {@link Base64FileValidation} on a single {@code String} field: checks the base64
 * data-URI format, decoded size, and detected MIME type (via Apache Tika) against the allowed list.
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

        return !hasValidationErrors(value, context);
    }

    private boolean hasValidationErrors(String value, ConstraintValidatorContext context) {
        if (!BASE64_FILE_PATTERN.matcher(value).matches()) {
            addConstraintViolation(context, FileMessageKeys.BASE64_INVALID_FORMAT);
            return true;
        }

        String base64Content = value.substring(value.indexOf(",") + 1);
        boolean hasError = false;

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Content);

            if (nonValidateFileSize(decodedBytes.length, context, FileMessageKeys.BASE64_INVALID_SIZE)) {
                hasError = true;
            } else {
                String detectedMimeType = tika.detect(decodedBytes);

                if (isMimeTypeNotAllowed(detectedMimeType)) {
                    log.warn("The MIME detected type ({}) is not allowed. Expected types: {}",
                            detectedMimeType, String.join(", ", allowedTypes));
                    addConstraintViolation(context,
                            FileMessageKeys.BASE64_INVALID_DETECTED_TYPE,
                            detectedMimeType,
                            String.join(", ", allowedTypes)
                    );
                    hasError = true;
                }
            }
        } catch (IllegalArgumentException e) {
            log.debug("Base64 Invalid Content: {}", e.getMessage());
            addConstraintViolation(context, FileMessageKeys.BASE64_INVALID_CONTENT);
            hasError = true;
        } catch (Exception e) {
            log.error("Error when detecting mime type using Apache Tika: {}", e.getMessage(), e);
            addConstraintViolation(context, FileMessageKeys.BASE64_INVALID_GENERAL);
            hasError = true;
        }

        return hasError;
    }
}
