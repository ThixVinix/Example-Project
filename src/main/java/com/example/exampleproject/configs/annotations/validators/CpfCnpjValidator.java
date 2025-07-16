package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.CpfCnpjValidation;
import com.example.exampleproject.configs.annotations.validators.base.AbstractValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * Validator class for validating if a string is a valid CPF or CNPJ.
 * <p>
 * Implements the {@link ConstraintValidator} interface for the {@link CpfCnpjValidation} annotation.
 * <p>
 * This validator implements custom validation logic for CPF and CNPJ.
 */
@Slf4j
public class CpfCnpjValidator extends AbstractValidator implements ConstraintValidator<CpfCnpjValidation, String> {

    private static final byte CPF_LENGTH = 11;
    private static final byte CNPJ_LENGTH = 14;
    private static final Pattern DIGITS_ONLY_PATTERN = Pattern.compile("^\\d+$");

    // Constants for verification digit calculation
    private static final int VERIFICATION_DIGIT_MODULO = 11;
    private static final int VERIFICATION_DIGIT_THRESHOLD = 2;
    private static final int VERIFICATION_DIGIT_BASE = 11;

    // CPF weight constants
    private static final int CPF_WEIGHT_10 = 10;
    private static final int CPF_WEIGHT_9 = 9;
    private static final int CPF_WEIGHT_8 = 8;
    private static final int CPF_WEIGHT_7 = 7;
    private static final int CPF_WEIGHT_6 = 6;
    private static final int CPF_WEIGHT_5 = 5;
    private static final int CPF_WEIGHT_4 = 4;
    private static final int CPF_WEIGHT_3 = 3;
    private static final int CPF_WEIGHT_2 = 2;
    private static final int CPF_WEIGHT_11 = 11;

    // CNPJ weight constants
    private static final int CNPJ_WEIGHT_9 = 9;
    private static final int CNPJ_WEIGHT_8 = 8;
    private static final int CNPJ_WEIGHT_7 = 7;
    private static final int CNPJ_WEIGHT_6 = 6;
    private static final int CNPJ_WEIGHT_5 = 5;
    private static final int CNPJ_WEIGHT_4 = 4;
    private static final int CNPJ_WEIGHT_3 = 3;
    private static final int CNPJ_WEIGHT_2 = 2;

    // Position constants
    private static final int CPF_FIRST_DIGIT_POSITION = 9;
    private static final int CPF_SECOND_DIGIT_POSITION = 10;
    private static final int CNPJ_FIRST_DIGIT_POSITION = 12;
    private static final int CNPJ_SECOND_DIGIT_POSITION = 13;

    @Override
    public void initialize(CpfCnpjValidation constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) {
            return true;
        }

        if (!hasValidFormat(value, context)) {
            return false;
        }

        return validateByLength(value, context);
    }

    private boolean hasValidFormat(String value, ConstraintValidatorContext context) {
        if (!DIGITS_ONLY_PATTERN.matcher(value).matches()) {
            addConstraintViolation(context, "msg.validation.request.field.cpfcnpj.invalid");
            return false;
        }
        return true;
    }

    private boolean validateByLength(String value, ConstraintValidatorContext context) {
        if (value.length() == CPF_LENGTH) {
            return validateCpf(value, context);
        } else if (value.length() == CNPJ_LENGTH) {
            return validateCnpj(value, context);
        } else {
            addConstraintViolation(context, "msg.validation.request.field.cpfcnpj.invalidLength");
            return false;
        }
    }

    private boolean validateCpf(String value, ConstraintValidatorContext context) {
        if (isValidCpf(value)) {
            return true;
        }
        addConstraintViolation(context, "msg.validation.request.field.cpf.invalidCheckDigit");
        return false;
    }

    private boolean validateCnpj(String value, ConstraintValidatorContext context) {
        if (isValidCnpj(value)) {
            return true;
        }
        addConstraintViolation(context, "msg.validation.request.field.cnpj.invalidCheckDigit");
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
        int digit1 = calculateVerificationDigit(cpf, new int[]{
                CPF_WEIGHT_10, CPF_WEIGHT_9, CPF_WEIGHT_8, CPF_WEIGHT_7, CPF_WEIGHT_6,
                CPF_WEIGHT_5, CPF_WEIGHT_4, CPF_WEIGHT_3, CPF_WEIGHT_2
        }, CPF_FIRST_DIGIT_POSITION);

        int digit2 = calculateVerificationDigit(cpf, new int[]{
                CPF_WEIGHT_11, CPF_WEIGHT_10, CPF_WEIGHT_9, CPF_WEIGHT_8, CPF_WEIGHT_7,
                CPF_WEIGHT_6, CPF_WEIGHT_5, CPF_WEIGHT_4, CPF_WEIGHT_3, CPF_WEIGHT_2
        }, CPF_SECOND_DIGIT_POSITION);

        return (cpf.charAt(CPF_FIRST_DIGIT_POSITION) - '0' == digit1) &&
                (cpf.charAt(CPF_SECOND_DIGIT_POSITION) - '0' == digit2);
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
        int digit1 = calculateVerificationDigit(cnpj, new int[]{
                CNPJ_WEIGHT_5, CNPJ_WEIGHT_4, CNPJ_WEIGHT_3, CNPJ_WEIGHT_2, CNPJ_WEIGHT_9, CNPJ_WEIGHT_8,
                CNPJ_WEIGHT_7, CNPJ_WEIGHT_6, CNPJ_WEIGHT_5, CNPJ_WEIGHT_4, CNPJ_WEIGHT_3, CNPJ_WEIGHT_2
        }, CNPJ_FIRST_DIGIT_POSITION);

        int digit2 = calculateVerificationDigit(cnpj, new int[]{
                CNPJ_WEIGHT_6, CNPJ_WEIGHT_5, CNPJ_WEIGHT_4, CNPJ_WEIGHT_3, CNPJ_WEIGHT_2, CNPJ_WEIGHT_9,
                CNPJ_WEIGHT_8, CNPJ_WEIGHT_7, CNPJ_WEIGHT_6, CNPJ_WEIGHT_5, CNPJ_WEIGHT_4, CNPJ_WEIGHT_3, CNPJ_WEIGHT_2
        }, CNPJ_SECOND_DIGIT_POSITION);

        return (cnpj.charAt(CNPJ_FIRST_DIGIT_POSITION) - '0' == digit1) &&
                (cnpj.charAt(CNPJ_SECOND_DIGIT_POSITION) - '0' == digit2);
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
            sum += ((value.charAt(i) - '0') * weights[i]);
        }
        int remainder = (sum % VERIFICATION_DIGIT_MODULO);
        return (remainder < VERIFICATION_DIGIT_THRESHOLD) ? 0 : (VERIFICATION_DIGIT_BASE - remainder);
    }
}
