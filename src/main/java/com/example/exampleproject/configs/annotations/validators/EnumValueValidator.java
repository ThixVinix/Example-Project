package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.EnumValueValidation;
import com.example.exampleproject.configs.annotations.validators.base.AbstractEnumValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

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
        super.initialize(annotation.enumClass(), "getValue", annotation.hideValidOptions());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isNull(value)) {
            return true;
        }

        boolean isValid = Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(enumConstant -> enumValueMatches(enumConstant, value));

        if (!isValid) {
            addConstraintViolationForEnum(context, value,
                    "msg.validation.request.field.enum.invalid.value",
                    "msg.validation.request.field.enum.invalid.value.hidden");
        }

        return isValid;
    }

    /**
     * Checks if the provided value matches the enum value with case-insensitive string comparison.
     * Overrides the parent method to ensure case-insensitive matching for string values.
     *
     * @param enumConstant the enum constant
     * @param value        the value to check
     * @return true if the value matches the enum value, false otherwise
     */
    @Override
    protected boolean enumValueMatches(Enum<?> enumConstant, Object value) {
        if (matchesEnumName(enumConstant, value)) {
            return true;
        }

        if (nonNull(accessorMethod)) {
            return matchesEnumValueViaAccessor(enumConstant, value);
        }

        return false;
    }

    private boolean matchesEnumName(Enum<?> enumConstant, Object value) {
        return enumConstant.name().equals(value) ||
                (value instanceof String string && enumConstant.name().equalsIgnoreCase(string));
    }

    private boolean matchesEnumValueViaAccessor(Enum<?> enumConstant, Object value) {
        try {
            Object enumValue = accessorMethod.invoke(enumConstant);
            if (enumValue instanceof String enumString && value instanceof String valueString) {
                return enumString.equalsIgnoreCase(valueString);
            }
            return enumValue.equals(value);
        } catch (Exception e) {
            log.debug("Error accessing getValue for enum {}: {}",
                    enumConstant.name(), e.getMessage());
            return false;
        }
    }

}
