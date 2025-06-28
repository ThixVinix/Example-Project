package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.EnumValueValidation;
import com.example.exampleproject.configs.annotations.validators.base.AbstractEnumValidator;
import com.example.exampleproject.utils.MessageUtils;
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

    private EnumValueValidation annotation;

    @Override
    public void initialize(EnumValueValidation annotation) {
        this.annotation = annotation;
        super.initialize(annotation.enumClass(), "getValue");
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isNull(value)) {
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
     * Adds a constraint violation with appropriate error message based on hideValidOptions setting.
     *
     * @param context the validation context
     * @param value the invalid value
     */
    private void addConstraintViolation(ConstraintValidatorContext context, String value) {
        if (annotation.hideValidOptions()) {
            context.disableDefaultConstraintViolation();
            String errorMessage = MessageUtils.getMessage(
                    "msg.validation.request.field.enum.invalid.value.hidden", value
            );
            context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();
        } else {
            addConstraintViolationWithValidValues(context, value, 
                    "msg.validation.request.field.enum.invalid.value");
        }
    }

    /**
     * Checks if the provided value matches the enum value with case-insensitive string comparison.
     * Overrides the parent method to ensure case-insensitive matching for string values.
     *
     * @param enumConstant the enum constant
     * @param value the value to check
     * @return true if the value matches the enum value, false otherwise
     */
    @Override
    protected boolean enumValueMatches(Enum<?> enumConstant, Object value) {
        if (enumConstant.name().equals(value) ||
            (value instanceof String string && enumConstant.name().equalsIgnoreCase(string))) {
            return true;
        }

        if (nonNull(accessorMethod)) {
            try {
                Object enumValue = accessorMethod.invoke(enumConstant);
                if (enumValue instanceof String enumString && value instanceof String valueString) {
                    return enumString.equalsIgnoreCase(valueString);
                }
                return enumValue.equals(value);
            } catch (Exception e) {
                log.debug("Error accessing getValue for enum {}: {}", 
                        enumConstant.name(), e.getMessage());
            }
        }

        return false;
    }
}
