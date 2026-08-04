package io.github.thixvinix.commons.files;

import jakarta.validation.ConstraintValidatorContext;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;

import static java.util.Objects.isNull;

/**
 * Shared functionality for validators that validate collections (List or Map) of base64-encoded
 * files: initialization, individual file validation, and size calculation. Uses composition so it
 * can be reused regardless of the validator's inheritance hierarchy (list vs. map base classes).
 */
@Getter
@Slf4j
public class Base64FileCollectionValidatorHelper {

    private int maxFileCount;
    private int maxTotalSizeInMB;
    private Base64FileValidator base64FileValidator;

    public void initialize(Base64FileValidation annotation) {
        this.maxFileCount = annotation.maxFileCount();
        this.maxTotalSizeInMB = annotation.maxTotalSizeInMB();
        this.base64FileValidator = new Base64FileValidator();
        this.base64FileValidator.initialize(annotation);
    }

    public boolean validateIndividualBase64File(String base64Value, ConstraintValidatorContext context) {
        return base64FileValidator.isValid(base64Value, context);
    }

    /**
     * @return the decoded size in bytes, or 0 if the value is invalid
     */
    public long calculateBase64FileSize(String base64Value) {
        if (isNull(base64Value) || base64Value.isEmpty()) {
            return 0L;
        }

        try {
            String base64Content = base64Value.substring(base64Value.indexOf(",") + 1);
            byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
            return decodedBytes.length;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }
}
