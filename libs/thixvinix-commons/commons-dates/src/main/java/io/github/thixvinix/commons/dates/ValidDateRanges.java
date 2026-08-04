package io.github.thixvinix.commons.dates;

import jakarta.validation.Constraint;

import java.lang.annotation.*;

/**
 * Container for repeated {@link DateRangeValidation} annotations on the same type.
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
public @interface ValidDateRanges {
    DateRangeValidation[] value();
}
