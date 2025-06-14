package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.EnumCodeValidation;
import com.example.exampleproject.configs.annotations.validators.base.AbstractEnumValidator;
import com.example.exampleproject.utils.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * Validator to check if a numeric value matches with the "code" of a constant in a specified Enum class.
 */
@Slf4j
public class EnumCodeValidator
        extends AbstractEnumValidator implements ConstraintValidator<EnumCodeValidation, Integer> {

    @Override
    public void initialize(EnumCodeValidation annotation) {
        super.initialize(annotation.enumClass(), "getCode");
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (accessorMethod == null) {
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
                .anyMatch(enumConstant -> enumValueMatches(enumConstant, value));

        if (!isValid) {
            addConstraintViolationWithValidValues(context, value, 
                    "msg.validation.request.field.enum.invalid.code");
        }

        return isValid;
    }
}
