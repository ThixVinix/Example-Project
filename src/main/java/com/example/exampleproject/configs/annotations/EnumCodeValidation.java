package com.example.exampleproject.configs.annotations;

import com.example.exampleproject.configs.annotations.validators.EnumCodeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Annotation to validate if a numerical value corresponds to the "code" of a constant in a specified Enum class.
 * The "enumClass" parameter must be provided to indicate the target Enum.
 * This annotation supports code-based validation for Enums with a numerical code field.
 * <p>
 * Constraints:
 * - The provided value must match a valid code from the specified Enum class.
 * - Null values are considered valid unless enforced otherwise (e.g., with @NotNull).
 */
@Documented
@Constraint(validatedBy = EnumCodeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumCodeValidation {

    @SuppressWarnings("squid:S1452")
    Class<? extends Enum<?>> enumClass();

    String message() default "The code provided does not correspond to a valid value of the ENUM.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}