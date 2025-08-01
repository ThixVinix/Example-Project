package com.example.exampleproject.configs.annotations.validators.helpers;

import com.example.exampleproject.configs.annotations.Base64FileValidation;
import com.example.exampleproject.configs.annotations.validators.Base64FileValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Base64;

import static java.util.Objects.isNull;

/**
 * Utility class that provides common functionality for validators that validate collections of base64 files.
 * <p>
 * This class contains shared methods for validating collections (List or Map) of base64-encoded files,
 * including initialization, individual file validation, and size calculation.
 * It uses a composition pattern to be used by different validator classes regardless of their inheritance hierarchy.
 */
@Getter
@Slf4j
public class Base64FileCollectionValidatorHelper {

    private int maxFileCount;
    private int maxTotalSizeInMB;
    private Base64FileValidator base64FileValidator;

    /**
     * Initializes the helper with the given annotation.
     *
     * @param annotation the Base64FileValidation annotation
     */
    public void initialize(Base64FileValidation annotation) {
        this.maxFileCount = annotation.maxFileCount();
        this.maxTotalSizeInMB = annotation.maxTotalSizeInMB();
        this.base64FileValidator = new Base64FileValidator();
        this.base64FileValidator.initialize(annotation);
    }

    /**
     * Validates an individual base64 file using the Base64FileValidator.
     *
     * @param base64Value the base64 file value
     * @param context     the validation context
     * @return true if the file is valid, false otherwise
     */
    public boolean validateIndividualBase64File(String base64Value, ConstraintValidatorContext context) {
        return base64FileValidator.isValid(base64Value, context);
    }

    /**
     * Calculates the size of a base64 encoded file in bytes.
     *
     * @param base64Value the base64 encoded file string
     * @return the size in bytes, or 0 if the value is invalid
     */
    public long calculateBase64FileSize(String base64Value) {
        if (isNull(base64Value) || base64Value.isEmpty()) {
            return NumberUtils.INTEGER_ZERO;
        }

        try {
            String base64Content = base64Value.substring(base64Value.indexOf(",") + NumberUtils.INTEGER_ONE);
            byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
            return decodedBytes.length;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return NumberUtils.INTEGER_ZERO;
        }
    }
}
