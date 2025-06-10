package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.MultipartFileValidation;
import com.example.exampleproject.configs.annotations.enums.MimeTypeEnum;
import com.example.exampleproject.utils.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Validator to check if a MultipartFile is valid, according to specified types and size constraints.
 */
@Slf4j
public class MultipartFileValidator implements ConstraintValidator<MultipartFileValidation, MultipartFile> {

    private String[] allowedTypes;
    private int maxSizeInMB;
    private Tika tika;

    @Override
    public void initialize(MultipartFileValidation annotation) {
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
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {

        if (file == null || file.isEmpty()) {
            return true;
        }

        String detectedType = detectMimeType(file);
        String originalExtension = getFileExtension(file.getOriginalFilename());

        if (!isMimeTypeExtensionConsistent(detectedType, originalExtension)) {
            addConstraintViolation(context,
                    "msg.validation.request.field.multipartfile.invalid.extension",
                    originalExtension, detectedType);
            return false;
        }

        if (!isMimeTypeAllowed(detectedType)) {
            String allowedMimeTypes = String.join(", ", allowedTypes);
            addConstraintViolation(context,
                    "msg.validation.request.field.multipartfile.invalid.type", allowedMimeTypes);
            return false;
        }

        return isFileSizeValid(file, context);
    }

    private boolean isFileSizeValid(MultipartFile file, ConstraintValidatorContext context) {
        final long BYTES_IN_ONE_MB = (1024L * 1024L);
        long maxFileSizeInBytes = maxSizeInMB * BYTES_IN_ONE_MB;
        long actualFileSizeInBytes = file.getSize();

        double actualFileSizeInMB = (double) actualFileSizeInBytes / BYTES_IN_ONE_MB;
        double maxFileSizeInMB = (double) maxFileSizeInBytes / BYTES_IN_ONE_MB;

        if (actualFileSizeInBytes > maxFileSizeInBytes) {
            addConstraintViolation(context,
                    "msg.validation.request.field.multipartfile.invalid.size",
                    String.format("%.4f", actualFileSizeInMB),
                    String.format("%.0f", maxFileSizeInMB));
            return false;
        }

        return true;
    }

    /**
     * Detects the MIME type of the provided file using Apache Tika.
     *
     * @param file the file to detect.
     * @return the detected MIME type.
     */
    private String detectMimeType(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            return tika.detect(is);
        } catch (IOException e) {
            log.warn("Erro ao detectar tipo MIME real com Tika. contentType original: {}", file.getContentType(), e);
            return file.getContentType();
        }
    }

    /**
     * Validates the consistency between MIME type and file extension.
     *
     * @param mimeType the detected MIME type.
     * @param extension the file extension.
     * @return true if the MIME type matches the extension, false otherwise.
     */
    private boolean isMimeTypeExtensionConsistent(String mimeType, String extension) {
        if (MimeTypeEnum.isNotValidExtension(extension)) {
            log.warn("No known MIME found for the extension: {}", extension);
            return false;
        }

        String expectedExtension = MimeTypeEnum.getExtensionFromMimeType(mimeType);

        return extension.equalsIgnoreCase(expectedExtension);
    }


    /**
     * Retrieves the file extension from its name.
     *
     * @param fileName the file name to extract the extension from.
     * @return the file extension, or null if it cannot be determined.
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }

    /**
     * Checks if the MIME type is in the list of allowed types.
     *
     * @param contentType the MIME type to check.
     * @return true if the MIME type is allowed, false otherwise.
     */
    private boolean isMimeTypeAllowed(String contentType) {
        return ArrayUtils.isEmpty(allowedTypes) || Arrays.asList(allowedTypes).contains(contentType);
    }

    /**
     * Adds a custom validation message with parameters.
     *
     * @param context validation context.
     * @param messageKey the message key to use.
     * @param params the parameters to include in the message.
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

