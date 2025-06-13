package com.example.exampleproject.configs.annotations;

import com.example.exampleproject.configs.annotations.validators.CpfCnpjValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to validate whether the provided field is a valid CPF or CNPJ.
 * <p>
 * CPF (Cadastro de Pessoas Físicas) is the Brazilian individual taxpayer registry identification.
 * CNPJ (Cadastro Nacional da Pessoa Jurídica) is the Brazilian company taxpayer registry identification.
 * <p>
 * This annotation can be applied to String fields that should contain either a valid CPF or CNPJ.
 *
 * <h4>Validator:</h4>
 * <ol>
 *   <li><strong>{@link CpfCnpjValidator}:</strong> Validates if the string is a valid CPF or CNPJ.</li>
 * </ol>
 *
 * <h4>Attributes:</h4>
 * <ul>
 *   <li><strong>message:</strong> The error message template to display upon validation failure.</li>
 *   <li><strong>groups:</strong> Defines validation groups, allowing selective application of rules.</li>
 *   <li><strong>payload:</strong> Provides custom metadata for validation.</li>
 * </ul>
 */
@Documented
@Constraint(validatedBy = {CpfCnpjValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CpfCnpjValidation {

    String message() default "{msg.validation.request.field.cpfcnpj.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}