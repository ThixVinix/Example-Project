package io.github.thixvinix.commons.documents;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that a field is a valid CPF or CNPJ.
 * <p>
 * CPF (Cadastro de Pessoas Físicas) is the Brazilian individual taxpayer registry identification.
 * CNPJ (Cadastro Nacional da Pessoa Jurídica) is the Brazilian company taxpayer registry
 * identification, including the alphanumeric CNPJ format (SERPRO spec).
 * <p>
 * Applies to unformatted values only — 11 digits for CPF, 14 characters for CNPJ. A blank or
 * null value is considered valid; combine with {@code @NotBlank} to require presence.
 *
 * <h4>Validator:</h4>
 * <ol>
 *   <li><strong>{@link CpfCnpjValidator}:</strong> Validates if the string is a valid CPF or CNPJ.</li>
 * </ol>
 */
@Documented
@Constraint(validatedBy = {CpfCnpjValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CpfCnpjValidation {

    /**
     * Dead on every failure path: the validator always calls
     * {@code disableDefaultConstraintViolation()} and builds its own localized message via
     * {@code io.github.thixvinix.commons.i18n.Messages}. Kept as a plain literal (not a
     * {@code {key}} placeholder) because this module ships no {@code ValidationMessages.properties}
     * for the standard Bean Validation interpolation path.
     */
    String message() default "Invalid CPF or CNPJ.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
