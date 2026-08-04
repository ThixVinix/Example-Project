package io.github.thixvinix.commons.validation;

import io.github.thixvinix.commons.i18n.Messages;

import jakarta.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.isNull;

/**
 * Base class for {@code ConstraintValidator} implementations, providing shared message
 * resolution and constraint-violation reporting.
 * <p>
 * Message resolution failures (e.g. a missing key) are isolated to the resolution step: a
 * constraint violation is always registered, falling back to the raw message key as text if
 * resolution fails, rather than silently accepting an invalid value as the original
 * implementation's broader try/catch did.
 */
@Slf4j
public abstract class AbstractValidator {

    protected static final int DEFAULT_MAX_TOTAL_SIZE_IN_MB = 10;

    protected static final long BYTES_IN_ONE_MB = 1024L * 1024L;

    /**
     * Validates and normalizes the maximum total size in megabytes.
     * If the provided value is invalid (less than or equal to zero),
     * the default value will be used.
     */
    protected int validateMaxTotalSizeMB(int maxTotalSizeMB) {
        if (maxTotalSizeMB <= 0) {
            log.warn("The value of maxTotalSizeMB provided is invalid ({}). Default value of {} MB will be used.",
                    maxTotalSizeMB, DEFAULT_MAX_TOTAL_SIZE_IN_MB);
            return DEFAULT_MAX_TOTAL_SIZE_IN_MB;
        }
        return maxTotalSizeMB;
    }

    /**
     * Adds a custom validation message with parameters.
     */
    protected void addConstraintViolation(ConstraintValidatorContext context, String messageKey, String... params) {
        registerViolation(context, null, messageKey, params);
    }

    /**
     * Adds a constraint violation with a property node.
     */
    protected void addConstraintViolationWithPropertyNode(ConstraintValidatorContext context,
                                                           String propertyName,
                                                           String messageKey,
                                                           String... params) {
        registerViolation(context, propertyName, messageKey, params);
    }

    private void registerViolation(ConstraintValidatorContext context,
                                    String propertyName,
                                    String messageKey,
                                    String... params) {
        String escapedMessage = ConstraintMessages.escapeTemplate(resolveMessage(messageKey, params));

        context.disableDefaultConstraintViolation();
        ConstraintValidatorContext.ConstraintViolationBuilder builder =
                context.buildConstraintViolationWithTemplate(escapedMessage);

        if (propertyName != null) {
            builder.addPropertyNode(propertyName).addConstraintViolation();
        } else {
            builder.addConstraintViolation();
        }
    }

    private String resolveMessage(String messageKey, String... params) {
        try {
            return (params.length > 0) ? Messages.get(messageKey, (Object[]) params) : Messages.get(messageKey);
        } catch (Exception e) {
            log.error("Error resolving message for key: {}", messageKey, e);
            return messageKey;
        }
    }

    /**
     * Checks if a collection is null or empty.
     */
    protected <T> boolean isNullOrEmpty(Iterable<T> collection) {
        return isNull(collection) || !collection.iterator().hasNext();
    }

    /**
     * Checks if a string is null or empty.
     */
    protected boolean isNullOrEmpty(String value) {
        return isNull(value) || value.isEmpty();
    }
}
