package io.github.thixvinix.commons.enums;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that an Integer value matches the numeric "code" of a constant in a specified Enum
 * class (via a {@code getCode()} accessor). A {@code null} value is always considered valid —
 * combine with {@code @NotNull} to require presence.
 */
@Documented
@Constraint(validatedBy = EnumCodeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumCodeValidation {

    @SuppressWarnings("squid:S1452")
    Class<? extends Enum<?>> enumClass();

    boolean hideValidOptions() default false;

    String message() default "The code provided does not correspond to a valid value of the ENUM.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
