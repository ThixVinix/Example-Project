package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.EnumValueValidation;
import com.example.exampleproject.configs.annotations.validators.base.AbstractEnumValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * Validator to check if a value matches with the custom "value" of an Enum,
 * if available (via a "getValue" method), or falls back to validating against
 * the literal name of the Enum constant.
 */
@Slf4j
public class EnumValueValidator
        extends AbstractEnumValidator implements ConstraintValidator<EnumValueValidation, String> {

    @Override
    public void initialize(EnumValueValidation annotation) {
        super.initialize(annotation.enumClass(), "getValue");
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        // First check for exact match or case-insensitive match with enum name
        for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
            if (enumConstant.name().equals(value) || 
                enumConstant.name().equalsIgnoreCase(value)) {
                return true;
            }

            // Also check for case-insensitive match with getValue() result
            if (accessorMethod != null) {
                try {
                    accessorMethod.setAccessible(true);
                    Object enumValue = accessorMethod.invoke(enumConstant);
                    if (enumValue instanceof String && 
                        ((String) enumValue).equalsIgnoreCase(value)) {
                        return true;
                    }
                } catch (Exception e) {
                    log.debug("Error accessing getValue for enum {}: {}", 
                            enumConstant.name(), e.getMessage());
                }
            }
        }

        // Then check using the standard accessor method match
        boolean isValid = Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(enumConstant -> enumValueMatches(enumConstant, value));

        if (!isValid) {
            addConstraintViolationWithValidValues(context, value, 
                    "msg.validation.request.field.enum.invalid.value");
        }

        return isValid;
    }
}
