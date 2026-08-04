package io.github.thixvinix.commons.files;

import io.github.thixvinix.commons.validation.AbstractValidator;

import jakarta.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Locale;

import static java.util.Objects.isNull;

/**
 * Abstract base class for validators that validate files: shared file-size and
 * MIME-type/extension-consistency checks.
 */
@Slf4j
public abstract class AbstractFileValidator extends AbstractValidator {

    protected String[] allowedTypes;
    protected int maxSizeInMB;

    /**
     * Validates the maximum file size in MB, falling back to a 2 MB default for non-positive values.
     */
    protected int validateMaxSizeInMB(int maxSizeInMB) {
        final int defaultMaxSizeInMB = 2;
        if (maxSizeInMB <= 0) {
            log.warn("The value of maxSizeInMB provided is invalid ({}). Default value of {} MB will be used.",
                    maxSizeInMB, defaultMaxSizeInMB);
            return defaultMaxSizeInMB;
        }
        return maxSizeInMB;
    }

    /**
     * Validates that the file size does not exceed the maximum size.
     *
     * @return true if the file size is invalid (exceeds the maximum), false otherwise
     */
    protected boolean nonValidateFileSize(long fileSizeInBytes, ConstraintValidatorContext context, String messageKey) {
        long maxFileSizeInBytes = maxSizeInMB * BYTES_IN_ONE_MB;

        double actualFileSizeInMB = (double) fileSizeInBytes / BYTES_IN_ONE_MB;
        double maxFileSizeInMB = (double) maxFileSizeInBytes / BYTES_IN_ONE_MB;

        if (fileSizeInBytes > maxFileSizeInBytes) {
            addConstraintViolation(context,
                    messageKey,
                    String.format(Locale.US, "%.4f", actualFileSizeInMB),
                    String.format(Locale.US, "%.0f", maxFileSizeInMB)
            );
            return true;
        }
        return false;
    }

    /**
     * Checks if the MIME type is not in the list of allowed types.
     */
    protected boolean isMimeTypeNotAllowed(String mimeType) {
        return allowedTypes != null && allowedTypes.length > 0 && !Arrays.asList(allowedTypes).contains(mimeType);
    }

    /**
     * Retrieves the file extension from its name.
     *
     * @return the file extension, or null if it cannot be determined.
     */
    protected String getFileExtension(String fileName) {
        if (isNull(fileName) || !fileName.contains(".")) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }

    /**
     * Gets the extension from a MIME type using {@link MimeTypeEnum}.
     */
    protected String getExtensionFromMimeType(String mimeType) {
        return MimeTypeEnum.getExtensionFromMimeType(mimeType);
    }

    /**
     * Validates the consistency between MIME type and file extension.
     */
    protected boolean isMimeTypeExtensionConsistent(String mimeType, String extension) {
        if (MimeTypeEnum.isNotValidExtension(extension)) {
            log.warn("No known MIME found for the extension: {}", extension);
            return false;
        }

        String expectedExtension = getExtensionFromMimeType(mimeType);
        return extension.equalsIgnoreCase(expectedExtension);
    }
}
