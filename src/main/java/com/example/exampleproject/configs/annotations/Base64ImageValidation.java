package com.example.exampleproject.configs.annotations;

import com.example.exampleproject.configs.annotations.validators.Base64ImageValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to validate if a string is a valid base64 encoded image.
 * This annotation checks if the string starts with a valid image MIME type prefix
 * and contains valid base64 encoded data.
 * <p>
 * Constraints:
 * - The string must start with a valid image MIME type prefix (e.g., "data:image/jpeg;base64,")
 * - The content after the prefix must be valid base64 encoded data
 * - Null values are considered valid unless enforced otherwise (e.g., with @NotNull)
 * - Empty strings are considered valid unless enforced otherwise (e.g., with @NotBlank)
 */
@Constraint(validatedBy = Base64ImageValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Base64ImageValidation {

    String message() default "The string is not a valid base64 encoded image.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}