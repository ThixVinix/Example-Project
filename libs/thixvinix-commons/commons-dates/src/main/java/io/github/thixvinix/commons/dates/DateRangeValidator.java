package io.github.thixvinix.commons.dates;

import io.github.thixvinix.commons.validation.AbstractValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

import static java.util.Objects.isNull;

/**
 * Validator for {@link DateRangeValidation}.
 */
@Slf4j
public class DateRangeValidator extends AbstractValidator implements ConstraintValidator<DateRangeValidation, Object> {

    private String dateAField;
    private String dateBField;

    @Override
    public void initialize(DateRangeValidation constraintAnnotation) {
        this.dateAField = constraintAnnotation.dateAField();
        this.dateBField = constraintAnnotation.dateBField();
    }

    /**
     * @return true if the date in dateAField is not after the date in dateBField, otherwise false
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Class<?> clazz = value.getClass();
            String dateAJsonProperty = PropertyNameResolver.resolveDisplayName(clazz, dateAField);
            String dateBJsonProperty = PropertyNameResolver.resolveDisplayName(clazz, dateBField);

            Object dateAValue = getFieldValue(value, clazz, dateAField);
            Object dateBValue = getFieldValue(value, clazz, dateBField);

            if (isNull(dateAValue) && isNull(dateBValue)) {
                return true;
            }

            if (isNull(dateAValue)) {
                addConstraintViolationWithPropertyNode(context, dateAField,
                        DateMessageKeys.RANGE_EMPTY, dateAJsonProperty, dateBJsonProperty);
                return false;
            }

            if (isNull(dateBValue)) {
                addConstraintViolationWithPropertyNode(context, dateBField,
                        DateMessageKeys.RANGE_EMPTY, dateAJsonProperty, dateBJsonProperty);
                return false;
            }

            Instant instantA = TemporalConversions.toInstant(dateAValue);
            Instant instantB = TemporalConversions.toInstant(dateBValue);

            if (instantA.isAfter(instantB)) {
                addConstraintViolationWithPropertyNode(context, dateAField,
                        DateMessageKeys.RANGE_INVALID, dateAJsonProperty, dateBJsonProperty);
                return false;
            }

            return true;
        } catch (Exception e) {
            log.warn("Error validating date range: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Gets the value of a field from an object using a getter method or field access.
     */
    private Object getFieldValue(Object target, Class<?> clazz, String fieldName) throws ReflectiveOperationException {
        String normalizedFieldName = capitalizeFirstLetter(fieldName);
        String accessorName = resolveAccessorName(clazz, fieldName, normalizedFieldName);
        return clazz.getMethod(accessorName).invoke(target);
    }

    private String resolveAccessorName(Class<?> clazz, String fieldName, String normalizedFieldName) {
        if (hasMethod(clazz, "get" + normalizedFieldName)) {
            return "get" + normalizedFieldName;
        }

        if (hasMethod(clazz, "is" + normalizedFieldName)) {
            return "is" + normalizedFieldName;
        }

        return fieldName;
    }

    private boolean hasMethod(Class<?> clazz, String methodName) {
        try {
            clazz.getMethod(methodName);
            return true;
        } catch (NoSuchMethodException ignored) {
            return false;
        }
    }

    private String capitalizeFirstLetter(String fieldName) {
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }
}
