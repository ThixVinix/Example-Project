package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.MultipartFileValidation;
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
 * Validator to check if a MultipartFile is valid according to specified types and size constraints.
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
        // Null or empty files are considered valid (optional validation)
        if (file == null || file.isEmpty()) {
            return true;
        }

        String detectedType = detectMimeType(file);

        // Validate the MIME type of the file
        if (!isMimeTypeAllowed(detectedType)) {
            String allowedMimeTypes = String.join(", ", allowedTypes);
            addConstraintViolation(context,
                    "msg.validation.request.field.multipartfile.invalid.type",
                    allowedMimeTypes
            );
            return false;
        }

        // Validate the file size against the maximum limit
        final long BYTES_IN_ONE_MB = (1024L * 1024L);
        long maxFileSizeInBytes = maxSizeInMB * BYTES_IN_ONE_MB;
        long actualFileSizeInBytes = file.getSize();

        double actualFileSizeInMB = (double) actualFileSizeInBytes / BYTES_IN_ONE_MB;
        double maxFileSizeInMB = (double) maxFileSizeInBytes / BYTES_IN_ONE_MB;

        if (actualFileSizeInBytes > maxFileSizeInBytes) {
            addConstraintViolation(context,
                    "msg.validation.request.field.multipartfile.invalid.size",
                    String.format("%.4f", actualFileSizeInMB),
                    String.format("%.0f", maxFileSizeInMB)
            );
            return false;
        }

        return true; // Valid file
    }

    /**
     * Detects the MIME type of the provided file using an Apache Tika library.
     * If the real MIME type detection fails, the original content type of the file is returned.
     *
     * @param file the {@link MultipartFile} whose MIME type is to be determined
     * @return the detected MIME type or the original content type if detection fails
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
     * Checks if the MIME type is in the list of allowed types.
     *
     * @param contentType the MIME type to check
     * @return true if the MIME type is allowed, false otherwise
     */
    private boolean isMimeTypeAllowed(String contentType) {
        return ArrayUtils.isEmpty(allowedTypes) || Arrays.asList(allowedTypes).contains(contentType);
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
