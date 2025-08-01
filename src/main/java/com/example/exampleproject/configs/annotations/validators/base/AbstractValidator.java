package com.example.exampleproject.configs.annotations.validators.base;

import com.example.exampleproject.utils.MessageUtils;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;

import static java.util.Objects.isNull;

/**
 * Abstract base class for validators providing common functionality.
 * <p>
 * This class provides common methods used across different validators,
 * reducing code duplication and standardizing validation behavior.
 */
@Slf4j
public abstract class AbstractValidator {

    /**
     * Default maximum total size in megabytes for file validations.
     */
    protected static final int DEFAULT_MAX_TOTAL_SIZE_IN_MB = 10;

    /**
     * Number of bytes in one megabyte.
     */
    protected static final long BYTES_IN_ONE_MB = 1024L * 1024L;

    /**
     * Validates and normalizes the maximum total size in megabytes.
     * If the provided value is invalid (less than or equal to zero),
     * the default value will be used.
     *
     * @param maxTotalSizeMB the maximum total size in megabytes to validate
     * @return the validated maximum total size (either the provided value or the default)
     */
    protected int validateMaxTotalSizeMB(int maxTotalSizeMB) {
        if (maxTotalSizeMB <= NumberUtils.INTEGER_ZERO) {
            log.warn("The value of maxTotalSizeMB provided is invalid ({}). Default value of {} MB will be used.",
                    maxTotalSizeMB, DEFAULT_MAX_TOTAL_SIZE_IN_MB);
            return DEFAULT_MAX_TOTAL_SIZE_IN_MB;
        }
        return maxTotalSizeMB;
    }

    /**
     * Adds a custom validation message with parameters.
     *
     * @param context    validation context
     * @param messageKey the message key to use
     * @param params     parameters to include in the message
     */
    protected void addConstraintViolation(ConstraintValidatorContext context, String messageKey, String... params) {
        try {
            context.disableDefaultConstraintViolation();

            String message = (params.length > NumberUtils.INTEGER_ZERO)
                    ? MessageUtils.getMessage(messageKey, (Object[]) params)
                    : MessageUtils.getMessage(messageKey);

            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
        } catch (Exception e) {
            log.error("Error adding constraint violation for: {}", messageKey, e);
        }
    }

    /**
     * Checks if a collection is null or empty.
     *
     * @param collection the collection to check
     * @return true if the collection is null or empty, false otherwise
     */
    protected <T> boolean isNullOrEmpty(Iterable<T> collection) {
        return isNull(collection) || !collection.iterator().hasNext();
    }

    /**
     * Checks if a string is null or empty.
     *
     * @param value the string to check
     * @return true if the string is null or empty, false otherwise
     */
    protected boolean isNullOrEmpty(String value) {
        return isNull(value) || value.isEmpty();
    }

    /**
     * Adds a constraint violation with a property node.
     *
     * @param context      validation context
     * @param propertyName the name of the property
     * @param messageKey   the message key to use
     * @param params       parameters to include in the message
     */
    protected void addConstraintViolationWithPropertyNode(ConstraintValidatorContext context,
                                                          String propertyName,
                                                          String messageKey,
                                                          String... params) {
        try {
            context.disableDefaultConstraintViolation();

            String message = (params.length > NumberUtils.INTEGER_ZERO)
                    ? MessageUtils.getMessage(messageKey, (Object[]) params)
                    : MessageUtils.getMessage(messageKey);

            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(propertyName)
                    .addConstraintViolation();
        } catch (Exception e) {
            log.error("Error adding constraint violation for property {}: {}", propertyName, messageKey, e);
        }
    }
}