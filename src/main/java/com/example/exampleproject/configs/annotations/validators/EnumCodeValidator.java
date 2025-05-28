package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.EnumCodeValidation;
import com.example.exampleproject.utils.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Validator to check if a numeric value matches with the "code" of a constant in a specified Enum class.
 */
@Slf4j
public class EnumCodeValidator implements ConstraintValidator<EnumCodeValidation, Integer> {

    private Class<? extends Enum<?>> enumClass;
    private Method codeMethod;

    @Override
    public void initialize(EnumCodeValidation annotation) {
        this.enumClass = annotation.enumClass();

        try {
            this.codeMethod = enumClass.getMethod("getCode");
        } catch (NoSuchMethodException e) {
            log.error("The Enum {} does not have the required 'getCode' method.", enumClass.getSimpleName(), e);
            this.codeMethod = null;
        }
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (this.codeMethod == null) {
            context.disableDefaultConstraintViolation();

            String errorMessage = MessageUtils.getMessage(
                    "msg.validation.request.field.enum.code.error.access.method",
                    enumClass.getSimpleName()
            );

            context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();

            return false;
        }


        if (value == null) {
            return true;
        }

        boolean isValid = Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(enumConstant -> enumCodeMatches(enumConstant, value));

        if (!isValid) {
            addConstraintViolation(context, value);
        }

        return isValid;
    }

    /**
     * Checks if the provided value matches the "code" value of an Enum instance.
     *
     * @param enumConstant instance of the Enum
     * @param value        the provided value
     * @return true if the value matches the code, false otherwise
     */
    private boolean enumCodeMatches(Enum<?> enumConstant, Integer value) {
        try {
            Integer enumCode = (Integer) codeMethod.invoke(enumConstant);
            return enumCode.equals(value);
        } catch (Exception e) {
            log.error("Error accessing the 'getCode' method for Enum {}.", enumClass.getSimpleName(), e);
            throw new IllegalArgumentException(
                    "Error accessing the 'getCode' method of the Enum " + enumClass.getSimpleName());
        }
    }

    /**
     * Adds a custom validation message, including valid Enum values sorted in ascending order.
     *
     * @param context      validation context
     * @param invalidValue the invalid value provided by the user
     */
    private void addConstraintViolation(ConstraintValidatorContext context, Integer invalidValue) {
        context.disableDefaultConstraintViolation();

        String validValues = Arrays.stream(enumClass.getEnumConstants())
                .map(enumConstant -> {
                    try {
                        return (Integer) codeMethod.invoke(enumConstant);
                    } catch (Exception e) {
                        log.error("Error accessing the 'getCode' value for Enum {}", enumClass.getSimpleName(), e);
                        throw new IllegalArgumentException(
                                "Error accessing the 'getCode' value of the Enum " + enumClass.getSimpleName());
                    }
                })
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        String message = MessageUtils.getMessage(
                "msg.validation.request.field.enum.invalid.code",
                invalidValue,
                validValues
        );

        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}