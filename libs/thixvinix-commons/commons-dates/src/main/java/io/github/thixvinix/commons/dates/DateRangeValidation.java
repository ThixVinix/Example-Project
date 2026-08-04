package io.github.thixvinix.commons.dates;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Class-level constraint verifying that {@code dateAField} is not after {@code dateBField}, and
 * that both are present or both absent. Supports {@link java.util.Date}, {@link java.time.LocalDate},
 * {@link java.time.LocalDateTime} and {@link java.time.ZonedDateTime} fields (may be mixed).
 * <p>
 * The violation is reported on {@code dateAField}, using its {@code @JsonProperty} name (or, for
 * Spring consumers with {@code -parameters} enabled, its constructor {@code @BindParam} name) if
 * present.
 */
@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ValidDateRanges.class)
public @interface DateRangeValidation {

    String message() default "Invalid or out of order dates: {dateAField} and {dateBField}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * The name of the first date field to be validated in the range.
     */
    String dateAField();

    /**
     * The name of the second date field to be validated in the range.
     */
    String dateBField();
}
