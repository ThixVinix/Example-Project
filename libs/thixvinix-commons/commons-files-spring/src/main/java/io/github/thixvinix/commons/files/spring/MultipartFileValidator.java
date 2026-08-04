package io.github.thixvinix.commons.files.spring;

import io.github.thixvinix.commons.files.AbstractFileValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static java.util.Objects.isNull;

/**
 * Validator for {@link MultipartFileValidation} on a single {@code MultipartFile}: checks
 * extension/MIME-type consistency, allowed MIME type, and size.
 */
@Slf4j
public class MultipartFileValidator
        extends AbstractFileValidator implements ConstraintValidator<MultipartFileValidation, MultipartFile> {

    private Tika tika;

    @Override
    public void initialize(MultipartFileValidation annotation) {
        this.allowedTypes = annotation.allowedTypes();
        this.maxSizeInMB = validateMaxSizeInMB(annotation.maxSizeInMB());
        this.tika = new Tika();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (isNull(file) || file.isEmpty()) {
            return true;
        }

        return !hasValidationErrors(file, context);
    }

    private boolean hasValidationErrors(MultipartFile file, ConstraintValidatorContext context) {
        String detectedType = detectMimeType(file);
        String originalExtension = getFileExtension(file.getOriginalFilename());

        if (!isMimeTypeExtensionConsistent(detectedType, originalExtension)) {
            addConstraintViolation(context,
                    MultipartFileMessageKeys.INVALID_EXTENSION,
                    originalExtension, detectedType);
            return true;
        }

        if (isMimeTypeNotAllowed(detectedType)) {
            String allowedMimeTypes = String.join(", ", allowedTypes);
            addConstraintViolation(context, MultipartFileMessageKeys.INVALID_TYPE, allowedMimeTypes);
            return true;
        }

        return nonValidateFileSize(file.getSize(), context, MultipartFileMessageKeys.INVALID_SIZE);
    }

    /**
     * Detects the MIME type of the provided file using Apache Tika.
     */
    private String detectMimeType(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            return tika.detect(is);
        } catch (IOException e) {
            log.warn("Error detecting real MIME with tika. original contentType: {}", file.getContentType(), e);
            return file.getContentType();
        }
    }
}
