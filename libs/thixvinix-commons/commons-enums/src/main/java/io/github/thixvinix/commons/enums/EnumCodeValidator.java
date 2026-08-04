package io.github.thixvinix.commons.enums;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * Validator for {@link EnumCodeValidation}: matches an Integer against the enum's
 * {@code getCode()} accessor.
 */
@Slf4j
public class EnumCodeValidator
        extends AbstractEnumValidator implements ConstraintValidator<EnumCodeValidation, Integer> {

    @Override
    public void initialize(EnumCodeValidation annotation) {
        super.initialize(annotation.enumClass(), "getCode", annotation.hideValidOptions());
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (accessorMethod == null) {
            addConstraintViolation(context, EnumMessageKeys.CODE_ACCESS_METHOD_ERROR, enumClass.getSimpleName());
            return false;
        }

        if (value == null) {
            return true;
        }

        boolean isValid = Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(enumConstant -> enumValueMatches(enumConstant, value));

        if (!isValid) {
            addConstraintViolationForEnum(context, value,
                    EnumMessageKeys.INVALID_CODE,
                    EnumMessageKeys.INVALID_CODE_HIDDEN);
        }

        return isValid;
    }
}
