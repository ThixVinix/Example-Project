package io.github.thixvinix.commons.validation;

import jakarta.validation.ConstraintValidatorContext;

/**
 * Minimal concrete subclass exposing {@link AbstractValidator}'s protected members for direct testing.
 */
class ProbeValidator extends AbstractValidator {

    void violation(ConstraintValidatorContext context, String messageKey, String... params) {
        addConstraintViolation(context, messageKey, params);
    }

    void violationOnProperty(ConstraintValidatorContext context, String propertyName, String messageKey, String... params) {
        addConstraintViolationWithPropertyNode(context, propertyName, messageKey, params);
    }

    int normalizeMaxTotalSizeMB(int value) {
        return validateMaxTotalSizeMB(value);
    }

    boolean stringNullOrEmpty(String value) {
        return isNullOrEmpty(value);
    }

    boolean iterableNullOrEmpty(Iterable<?> value) {
        return isNullOrEmpty(value);
    }
}
