package io.github.thixvinix.commons.enums;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Validator for {@link EnumValueValidation}: matches against the enum's {@code getValue()}
 * accessor when available, falling back to the literal constant name.
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
                    EnumMessageKeys.INVALID_VALUE,
                    EnumMessageKeys.INVALID_VALUE_HIDDEN);
        }

        return isValid;
    }

    /**
     * Overrides the parent method to ensure case-insensitive matching for string values.
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
