package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.EnumValueValidation;
import com.example.exampleproject.utils.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Validator to check if a value matches with the custom "value" of an Enum,
 * if available (via a "getValue" method), or falls back to validating against
 * the literal name of the Enum constant.
 */
@Slf4j
public class EnumValueValidator implements ConstraintValidator<EnumValueValidation, String> {

    private Class<? extends Enum<?>> enumClass;
    private Method valueMethod;

    @Override
    public void initialize(EnumValueValidation annotation) {
        this.enumClass = annotation.enumClass();

        try {
            this.valueMethod = enumClass.getMethod("getValue");
        } catch (NoSuchMethodException e) {
            log.warn("The Enum {} does not contain the required 'getValue' method. Falling back to literal names.",
                    enumClass.getSimpleName(), e);
            this.valueMethod = null;
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        boolean isValid = Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(enumConstant -> enumValueMatches(enumConstant, value));

        if (!isValid) {
            addConstraintViolation(context, value);
        }

        return isValid;
    }

    /**
     * Checks if the given value matches the Enum's value using "getValue" (if available)
     * or falls back to the literal name of the constant.
     *
     * @param enumConstant the Enum constant
     * @param value        the value to be validated
     * @return true if the value matches, false otherwise
     */
    private boolean enumValueMatches(Enum<?> enumConstant, String value) {
        if (valueMethod != null) {
            try {
                String enumValue = (String) valueMethod.invoke(enumConstant);
                return enumValue.equalsIgnoreCase(value);
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "Failed to access the 'getValue' method of Enum " + enumClass.getSimpleName(), e);
            }
        } else {
            return enumConstant.name().equalsIgnoreCase(value);
        }
    }

    /**
     * Constructs a custom validation violation message, including the invalid value
     * and a list of valid values. The list is sorted alphabetically.
     *
     * @param context      the validation context
     * @param invalidValue the invalid value provided by the user
     */
    private void addConstraintViolation(ConstraintValidatorContext context, String invalidValue) {
        context.disableDefaultConstraintViolation();

        String validValues = Arrays.stream(enumClass.getEnumConstants())
                .map(this::getEnumValueOrName)
                .sorted()
                .collect(Collectors.joining(", "));

        String message = MessageUtils.getMessage("msg.validation.request.field.enum.invalid.value", invalidValue, validValues);

        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }

    /**
     * Fetches the value of the Enum using "getValue" or falls back to the literal constant name
     * if the method is not available.
     *
     * @param enumConstant the Enum constant
     * @return the value of the Enum (via "getValue" or its literal name)
     */
    private String getEnumValueOrName(Enum<?> enumConstant) {
        if (valueMethod != null) {
            try {
                return (String) valueMethod.invoke(enumConstant);
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "Failed to access the 'getValue' method of Enum " + enumClass.getSimpleName(), e);
            }
        } else {
            return enumConstant.name();
        }
    }
}