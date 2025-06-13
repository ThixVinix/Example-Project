package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.DateRangeValidation;
import com.example.exampleproject.configs.annotations.validators.base.AbstractValidator;
import com.example.exampleproject.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.time.*;
import java.util.Objects;

@Slf4j
public class DateRangeValidator extends AbstractValidator implements ConstraintValidator<DateRangeValidation, Object> {

    private String dateAField;
    private String dateBField;
    private String dateAJsonProperty;
    private String dateBJsonProperty;

    @Override
    public void initialize(DateRangeValidation constraintAnnotation) {
        this.dateAField = constraintAnnotation.dateAField();
        this.dateBField = constraintAnnotation.dateBField();

        this.dateAJsonProperty = this.dateAField;
        this.dateBJsonProperty = this.dateBField;
    }

    /**
     * Validates if the provided object's date fields follow a specific relation.
     *
     * @param value   the object containing the date fields to be validated
     * @param context context in which the constraint is evaluated
     * @return true if the date in dateAField is not after the date in dateBField, otherwise false
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        boolean isValid = true;

        try {
            // Get JsonProperty names from the actual object being validated
            Class<?> clazz = value.getClass();
            dateAJsonProperty = getJsonPropertyName(clazz, dateAField);
            dateBJsonProperty = getJsonPropertyName(clazz, dateBField);

            Object dateAValue = clazz.getMethod(dateAField).invoke(value);
            Object dateBValue = clazz.getMethod(dateBField).invoke(value);

            if (dateAValue == null && dateBValue == null) {
                return true;
            }

            if (dateAValue == null) {
                addConstraintViolationWithPropertyNode(context, dateAField,
                        "msg.validation.request.field.date.range.empty",
                        dateAJsonProperty, dateBJsonProperty);
                isValid = false;
            } else if (dateBValue == null) {
                addConstraintViolationWithPropertyNode(context, dateBField,
                        "msg.validation.request.field.date.range.empty",
                        dateAJsonProperty, dateBJsonProperty);
                isValid = false;
            } else {
                Instant instantA = DateUtils.toInstant(dateAValue);
                Instant instantB = DateUtils.toInstant(dateBValue);

                if (instantA.isAfter(instantB)) {
                    addConstraintViolationWithPropertyNode(context, dateAField,
                            "msg.validation.request.field.date.range.invalid",
                            dateAJsonProperty, dateBJsonProperty);
                    isValid = false;
                }
            }

        } catch (Exception e) {
            log.warn("Error validating date range: {}", e.getMessage(), e);
            isValid = false;
        }

        return isValid;
    }

    /**
     * Gets the JsonProperty value for a field if it exists.
     *
     * @param clazz     the class containing the field
     * @param fieldName the name of the field
     * @return the JsonProperty value if it exists, otherwise the original field name
     */
    private String getJsonPropertyName(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
            if (Objects.nonNull(jsonProperty)) {
                String value = jsonProperty.value().trim();
                if (!value.isEmpty()) {
                    return value;
                }
            }
        } catch (NoSuchFieldException | SecurityException e) {
            log.warn("Error getting JsonProperty for field {}: {}", fieldName, e.getMessage());
        }
        return fieldName;
    }
}
