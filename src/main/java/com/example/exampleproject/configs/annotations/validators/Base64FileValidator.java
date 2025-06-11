package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.Base64FileValidation;
import com.example.exampleproject.utils.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.tika.Tika;

import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Validator to check if a string is a valid base64 encoded file of specified types.
 */
@Slf4j
public class Base64FileValidator implements ConstraintValidator<Base64FileValidation, String> {

    private static final Pattern BASE64_FILE_PATTERN =
            Pattern.compile("^data:[a-zA-Z0-9.+-]+/[a-zA-Z0-9.+-]+;base64,.*");

    private String[] allowedTypes;
    private int maxSizeInMB;
    private Tika tika;

    @Override
    public void initialize(Base64FileValidation annotation) {
        this.allowedTypes = annotation.allowedTypes();
        this.maxSizeInMB = annotation.maxSizeInMB();

        if (this.maxSizeInMB <= NumberUtils.INTEGER_ZERO) {
            final int DEFAULT_MAX_SIZE_IN_MB = 2;
            log.warn("The value of maxSizeInMB provided is invalid ({}). Default value of {} MB will be used.",
                    this.maxSizeInMB, DEFAULT_MAX_SIZE_IN_MB);
            this.maxSizeInMB = DEFAULT_MAX_SIZE_IN_MB;
        }

        tika = new Tika();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        if (!BASE64_FILE_PATTERN.matcher(value).matches()) {
            addConstraintViolation(context, "msg.validation.request.field.base64file.invalid.format");
            return false;
        }

        String base64Content = value.substring(value.indexOf(",") + NumberUtils.INTEGER_ONE);

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Content);

            if (!isFileSizeValid(decodedBytes, context)) {
                return false;
            }

            String detectedMimeType = tika.detect(decodedBytes);

            if (isMimeTypeNotAllowed(detectedMimeType)) {
                log.warn("The MIME detected type ({}) is not allowed. Expected types: {}",
                        detectedMimeType, Arrays.toString(allowedTypes));
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

    /**
     * Validates the file size against the maximum size allowed.
     *
     * @param decodedBytes the decoded byte array of the file
     * @param context the context for validation messages
     * @return true if the file size is valid, false otherwise
     */
    private boolean isFileSizeValid(byte[] decodedBytes, ConstraintValidatorContext context) {
        final long BYTES_IN_ONE_MB = 1024L * 1024L;
        long maxFileSizeInBytes = maxSizeInMB * BYTES_IN_ONE_MB;
        long actualFileSizeInBytes = decodedBytes.length;

        double actualFileSizeInMB = (double) actualFileSizeInBytes / BYTES_IN_ONE_MB;
        double maxFileSizeInMB = (double) maxFileSizeInBytes / BYTES_IN_ONE_MB;

        if (actualFileSizeInBytes > maxFileSizeInBytes) {
            addConstraintViolation(context,
                    "msg.validation.request.field.base64file.invalid.size",
                    String.format("%.4f", actualFileSizeInMB),
                    String.format("%.0f", maxFileSizeInMB)
            );
            return false;
        }
        return true;
    }

    /**
     * Checks if the provided MIME type is not in the allowed list.
     *
     * @param mimeType the MIME type to check
     * @return true if the MIME type is not allowed, false otherwise
     */
    private boolean isMimeTypeNotAllowed(String mimeType) {
        return !Arrays.asList(allowedTypes).contains(mimeType);
    }

    /**
     * Adds a custom validation message with parameters.
     *
     * @param context validation context
     * @param messageKey the message key to use
     * @param params parameters to include in the message
     */
    private void addConstraintViolation(ConstraintValidatorContext context, String messageKey, String... params) {
        context.disableDefaultConstraintViolation();

        String message = (params.length > NumberUtils.INTEGER_ZERO)
                ? MessageUtils.getMessage(messageKey, (Object[]) params)
                : MessageUtils.getMessage(messageKey);

        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }

}