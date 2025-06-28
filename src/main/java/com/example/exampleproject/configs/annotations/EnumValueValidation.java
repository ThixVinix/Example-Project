package com.example.exampleproject.configs.annotations;

import com.example.exampleproject.configs.annotations.validators.EnumValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


/**
 * Annotation for validating if a string value matches one of the constants in a specified Enum class.
 * This annotation can be applied to fields or method parameters.
 * <p>
 * The validation logic is implemented in the {@code EnumValidator} class. If the provided value is null,
 * it is automatically considered valid. For enforcing non-null validation, use the {@code @NotNull} annotation
 * alongside this annotation.
 * <p>
 * The "enumClass" parameter must specify the target Enum class that the validation should check against.
 * Only the valid enum constant names (case-insensitive match) will pass the validation.
 * <p>
 * Constraints:
 * - The provided value must match an existing enum constant from the specified Enum class.
 * - Null values are regarded as valid unless explicitly constrained by other annotations.
 * <p>
 * Parameters:
 * - "enumClass" specifies the Enum class to validate against.
 * - "message" specifies the error message when validation fails.
 * - "groups" allows grouping constraints.
 * - "payload" allows associating custom metadata with the constraint.
 * <p>
 * Example usage should be avoided in this documentation as per the constraints.
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
