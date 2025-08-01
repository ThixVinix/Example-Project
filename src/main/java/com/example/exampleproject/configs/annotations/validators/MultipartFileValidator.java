package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.MultipartFileValidation;
import com.example.exampleproject.configs.annotations.validators.base.AbstractFileValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static java.util.Objects.isNull;

/**
 * Validator class for validating MultipartFile objects for specific constraints such as file type
 * consistency, allowed MIME types, and maximum file size.
 * <p>
 * Implements the {@link ConstraintValidator} interface for the {@link MultipartFileValidation} annotation.
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

        String detectedType = detectMimeType(file);
        String originalExtension = getFileExtension(file.getOriginalFilename());

        if (!isMimeTypeExtensionConsistent(detectedType, originalExtension)) {
            addConstraintViolation(context,
                    "msg.validation.request.field.multipartfile.invalid.extension",
                    originalExtension, detectedType);
            return false;
        }

        if (isMimeTypeNotAllowed(detectedType)) {
            String allowedMimeTypes = String.join(", ", allowedTypes);
            addConstraintViolation(context,
                    "msg.validation.request.field.multipartfile.invalid.type", allowedMimeTypes);
            return false;
        }

        return validateFileSize(file.getSize(), context,
                "msg.validation.request.field.multipartfile.invalid.size");
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
            log.warn("Error detecting real MIME with tika. original contentType: {}", file.getContentType(), e);
            return file.getContentType();
        }
    }
}
