package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.CpfCnpjValidation;
import com.example.exampleproject.utils.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Validator class for validating if a string is a valid CPF or CNPJ.
 * <p>
 * Implements the {@link ConstraintValidator} interface for the {@link CpfCnpjValidation} annotation.
 * <p>
 * This validator implements custom validation logic for CPF and CNPJ.
 */
@Slf4j
public class CpfCnpjValidator implements ConstraintValidator<CpfCnpjValidation, String> {


    private static final byte CPF_LENGTH = 11;
    private static final byte CNPJ_LENGTH = 14;

    @Override
    public void initialize(CpfCnpjValidation constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) {
            return true;
        }

        if (!value.matches("^\\d+$")) {
            addConstraintViolation(context, "msg.validation.request.field.cpfcnpj.invalid");
            return false;
        }

        String unformatted = value.replaceAll("\\D", "");

        if (unformatted.length() == CPF_LENGTH) {
            if (isValidCpf(unformatted)) {
                return true;
            }
            addConstraintViolation(context, "msg.validation.request.field.cpf.invalidCheckDigit");
            return false;
        }

        if (unformatted.length() == CNPJ_LENGTH) {
            if (isValidCnpj(unformatted)) {
                return true;
            }
            addConstraintViolation(context, "msg.validation.request.field.cnpj.invalidCheckDigit");
            return false;
        }

        addConstraintViolation(context, "msg.validation.request.field.cpfcnpj.invalidLength");
        return false;

    }

    /**
     * Validates whether the provided CPF (Cadastro de Pessoas Físicas - Brazilian individual taxpayer registry number)
     * is valid based on specific rules, including checks for repeated digits, length, and verification digits.
     *
     * @param cpf the CPF string to be validated, expected to be a numeric string of length 11
     * @return true if the CPF is valid, according to the validation rules; false otherwise
     */
    private boolean isValidCpf(String cpf) {
        if (isRepeatedDigits(cpf) || cpf.length() != CPF_LENGTH) {
            return false;
        }
        int digit1 = calculateVerificationDigit(cpf, new int[]{10, 9, 8, 7, 6, 5, 4, 3, 2}, 9);
        int digit2 = calculateVerificationDigit(cpf, new int[]{11, 10, 9, 8, 7, 6, 5, 4, 3, 2}, 10);
        return cpf.charAt(9) - '0' == digit1 && cpf.charAt(10) - '0' == digit2;
    }

    /**
     * Validates whether the provided CNPJ
     * (Cadastro Nacional da Pessoa Jurídica - Brazilian company taxpayer registry number)
     * is valid based on specific rules, including checks for repeated digits, length, and verification digits.
     *
     * @param cnpj the CNPJ string to be validated, expected to be a numeric string of length 14
     * @return true if the CNPJ is valid, according to the validation rules; false otherwise
     */
    private boolean isValidCnpj(String cnpj) {
        if (isRepeatedDigits(cnpj) || cnpj.length() != CNPJ_LENGTH) {
            return false;
        }
        int digit1 = calculateVerificationDigit(cnpj, new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2}, 12);
        int digit2 = calculateVerificationDigit(cnpj, new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2}, 13);
        return cnpj.charAt(12) - '0' == digit1 && cnpj.charAt(13) - '0' == digit2;
    }

    /**
     * Checks if all characters in the given string are repeated digits, i.e.,
     * whether all characters in the string are the same.
     *
     * @param value the string to be checked for repeated digits
     * @return true if all characters in the string are the same, false otherwise
     */
    private boolean isRepeatedDigits(String value) {
        return value.chars().distinct().count() == 1;
    }

    /**
     * Calculates the verification digit for a given numeric value based on an array of weights and a specified length.
     * This method applies a weighted sum algorithm to determine the verification digit, commonly used for validation
     * of numeric identifiers such as CPF or CNPJ.
     *
     * @param value  the numeric string for which the verification digit should be calculated
     * @param weights an array of integers representing the weights to be applied to each digit of the value
     * @param length the number of digits from the value to be considered for the calculation
     * @return the calculated verification digit as an integer
     */
    private int calculateVerificationDigit(String value, int[] weights, int length) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += (value.charAt(i) - '0') * weights[i];
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String messageKey) {
        try {
            context.disableDefaultConstraintViolation();
            String message = MessageUtils.getMessage(messageKey);
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        } catch (Exception e) {
            log.error("Error adding constraint violation for: {}", messageKey, e);
        }
    }

}
