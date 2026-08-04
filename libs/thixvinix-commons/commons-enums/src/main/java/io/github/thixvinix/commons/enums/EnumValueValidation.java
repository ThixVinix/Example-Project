package io.github.thixvinix.commons.enums;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a String value matches one of the constants in a specified Enum class.
 * <p>
 * If the enum declares a {@code getValue()} accessor, the value is matched against it
 * (case-insensitively for strings); otherwise it falls back to the enum constant's literal name.
 * A {@code null} value is always considered valid — combine with {@code @NotNull} to require presence.
 */
@Documented
@Constraint(validatedBy = EnumValueValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValueValidation {

    @SuppressWarnings("squid:S1452")
    Class<? extends Enum<?>> enumClass();

    boolean hideValidOptions() default false;

    String message() default "The value provided does not correspond to a valid value of the ENUM.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
