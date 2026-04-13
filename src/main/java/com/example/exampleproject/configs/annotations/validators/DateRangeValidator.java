package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.DateRangeValidation;
import com.example.exampleproject.configs.annotations.validators.base.AbstractValidator;
import com.example.exampleproject.configs.exceptions.handler.helper.ExceptionHandlerMessageHelper;
import com.example.exampleproject.utils.DateUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Objects;

import static java.util.Objects.isNull;

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
            Class<?> clazz = value.getClass();
            dateAJsonProperty = getMappedFieldName(clazz, dateAField);
            dateBJsonProperty = getMappedFieldName(clazz, dateBField);

            Object dateAValue = getFieldValue(value, clazz, dateAField);
            Object dateBValue = getFieldValue(value, clazz, dateBField);

            if (isNull(dateAValue) && isNull(dateBValue)) {
                return true;
            }

            if (isNull(dateAValue)) {
                addConstraintViolationWithPropertyNode(context, dateAField,
                        "msg.validation.request.field.date.range.empty",
                        dateAJsonProperty, dateBJsonProperty);
                isValid = false;
            } else if (isNull(dateBValue)) {
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
     * Gets the value of a field from an object using a getter method or field access.
     *
     * @param target    the object containing the field
     * @param clazz     the class of the object
     * @param fieldName the name of the field
     * @return the value of the field
     * @throws ReflectiveOperationException if any error occurs during access
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
        } catch (NoSuchMethodException _) {
            return false;
        }
    }

    private String capitalizeFirstLetter(String fieldName) {
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    private String getMappedFieldName(Class<?> clazz, String fieldName) {
        Field field = ExceptionHandlerMessageHelper.getFieldRecursive(clazz, fieldName);
        if (Objects.nonNull(field)) {
            return ExceptionHandlerMessageHelper.getMappedName(clazz, field);
        }
        return fieldName;
    }
}
