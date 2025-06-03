package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.Base64FileValidation;
import com.example.exampleproject.utils.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

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

        String mimeType = extractMimeType(value);

        if (!isMimeTypeAllowed(mimeType)) {
            String allowedMimeTypes = String.join(", ", allowedTypes);
            addConstraintViolation(context,
                    "msg.validation.request.field.base64file.invalid.type",
                    allowedMimeTypes
            );
            return false;
        }

        String base64Content = value.substring(value.indexOf(",") + NumberUtils.INTEGER_ONE);

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Content);

            final long BYTES_IN_ONE_MB = (1024L * 1024L);

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
        } catch (IllegalArgumentException e) {
            log.debug("Invalid base64 content: {}", e.getMessage());
            addConstraintViolation(context, "msg.validation.request.field.base64file.invalid.content");
            return false;
        }

    }

    /**
     * Extracts the MIME type from the base64 string.
     *
     * @param value the base64 string
     * @return the MIME type
     */
    private String extractMimeType(String value) {
        int startIndex = 5;
        int endIndex = value.indexOf(";base64");

        if (endIndex > startIndex) {
            return value.substring(startIndex, endIndex);
        }

        return StringUtils.EMPTY;
    }

    /**
     * Checks if the MIME type is in the list of allowed types.
     *
     * @param mimeType the MIME type to check
     * @return true if the MIME type is allowed, false otherwise
     */
    private boolean isMimeTypeAllowed(String mimeType) {
        return Arrays.asList(allowedTypes).contains(mimeType);
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