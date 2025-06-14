package com.example.exampleproject.configs.annotations.validators.base;

import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;

/**
 * Abstract base class for validators that validate files.
 * <p>
 * This class provides common methods for validating files, such as checking
 * file size and MIME types.
 */
@Slf4j
public abstract class AbstractFileValidator extends AbstractValidator {

    protected String[] allowedTypes;
    protected int maxSizeInMB;

    /**
     * Validates the maximum file size in MB.
     *
     * @param maxSizeInMB the maximum file size in MB
     * @return the validated maximum file size in MB
     */
    protected int validateMaxSizeInMB(int maxSizeInMB) {
        final int DEFAULT_MAX_SIZE_IN_MB = 2;
        if (maxSizeInMB <= NumberUtils.INTEGER_ZERO) {
            log.warn("The value of maxSizeInMB provided is invalid ({}). Default value of {} MB will be used.",
                    maxSizeInMB, DEFAULT_MAX_SIZE_IN_MB);
            return DEFAULT_MAX_SIZE_IN_MB;
        }
        return maxSizeInMB;
    }

    /**
     * Validates that the file size does not exceed the maximum size.
     *
     * @param fileSizeInBytes the file size in bytes
     * @param context the validation context
     * @param messageKey the message key for the error message
     * @return true if the file size is valid, false otherwise
     */
    protected boolean validateFileSize(long fileSizeInBytes, ConstraintValidatorContext context, String messageKey) {
        final long BYTES_IN_ONE_MB = 1024L * 1024L;
        long maxFileSizeInBytes = maxSizeInMB * BYTES_IN_ONE_MB;

        double actualFileSizeInMB = (double) fileSizeInBytes / BYTES_IN_ONE_MB;
        double maxFileSizeInMB = (double) maxFileSizeInBytes / BYTES_IN_ONE_MB;

        if (fileSizeInBytes > maxFileSizeInBytes) {
            addConstraintViolation(context,
                    messageKey,
                    String.format("%.4f", actualFileSizeInMB),
                    String.format("%.0f", maxFileSizeInMB)
            );
            return false;
        }
        return true;
    }

    /**
     * Checks if the MIME type is in the list of allowed types.
     *
     * @param mimeType the MIME type to check
     * @return true if the MIME type is allowed, false otherwise
     */
    protected boolean isMimeTypeAllowed(String mimeType) {
        return Arrays.asList(allowedTypes).contains(mimeType);
    }

    /**
     * Checks if the MIME type is not in the list of allowed types.
     *
     * @param mimeType the MIME type to check
     * @return true if the MIME type is not allowed, false otherwise
     */
    protected boolean isMimeTypeNotAllowed(String mimeType) {
        return !Arrays.asList(allowedTypes).contains(mimeType);
    }

}
