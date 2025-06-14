package com.example.exampleproject.configs.annotations.validators.base;

import com.example.exampleproject.utils.MessageUtils;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Abstract base class for validators providing common functionality.
 * <p>
 * This class provides common methods used across different validators,
 * reducing code duplication and standardizing validation behavior.
 */
@Slf4j
public abstract class AbstractValidator {

    /**
     * Adds a custom validation message with parameters.
     *
     * @param context validation context
     * @param messageKey the message key to use
     * @param params parameters to include in the message
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
        return collection == null || !collection.iterator().hasNext();
    }

    /**
     * Checks if a string is null or empty.
     *
     * @param value the string to check
     * @return true if the string is null or empty, false otherwise
     */
    protected boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    /**
     * Adds a constraint violation with a property node.
     *
     * @param context validation context
     * @param propertyName the name of the property
     * @param messageKey the message key to use
     * @param params parameters to include in the message
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