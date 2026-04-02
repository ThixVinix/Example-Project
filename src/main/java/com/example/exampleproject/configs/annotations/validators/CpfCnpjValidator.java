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
 * <p>
 * Supports both numeric-only CNPJ (legacy) and alphanumeric CNPJ (new format),
 * as defined by SERPRO's specification for "CNPJ alfanumérico".
 * <p>
 * In the alphanumeric CNPJ format, each character (0-9, A-Z) is converted to its
 * calculation value by subtracting 48 from its ASCII code. The verification digit
 * algorithm (modulo 11, weights 2-9 right to left) remains the same.
 */
@Slf4j
public class CpfCnpjValidator extends AbstractValidator implements ConstraintValidator<CpfCnpjValidation, String> {

    private static final byte CPF_LENGTH = 11;
    private static final byte CNPJ_LENGTH = 14;
    private static final Pattern CPF_DIGITS_ONLY_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern CNPJ_ALPHANUMERIC_PATTERN = Pattern.compile("^[0-9A-Z]+$");

    // Constants for verification digit calculation
    private static final int VERIFICATION_DIGIT_MODULO = 11;
    private static final int VERIFICATION_DIGIT_THRESHOLD = 2;
    private static final int VERIFICATION_DIGIT_BASE = 11;

    // ASCII offset used to convert alphanumeric characters to their calculation values
    // '0' (ASCII 48) -> 0, '1' (ASCII 49) -> 1, ..., '9' (ASCII 57) -> 9,
    // 'A' (ASCII 65) -> 17, 'B' (ASCII 66) -> 18, ..., 'Z' (ASCII 90) -> 42
    private static final int ASCII_OFFSET = 48;

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

        return validateByLength(value, context);
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
        if (!CPF_DIGITS_ONLY_PATTERN.matcher(value).matches()) {
            addConstraintViolation(context, "msg.validation.request.field.cpfcnpj.invalid");
            return false;
        }
        if (isValidCpf(value)) {
            return true;
        }
        addConstraintViolation(context, "msg.validation.request.field.cpf.invalidCheckDigit");
        return false;
    }

    private boolean validateCnpj(String value, ConstraintValidatorContext context) {
        // CNPJ: first 12 characters can be alphanumeric (0-9, A-Z), last 2 must be numeric digits
        String cnpjBase = value.substring(0, CNPJ_FIRST_DIGIT_POSITION);
        String cnpjDigits = value.substring(CNPJ_FIRST_DIGIT_POSITION);

        if (!CNPJ_ALPHANUMERIC_PATTERN.matcher(cnpjBase).matches()
                || !CPF_DIGITS_ONLY_PATTERN.matcher(cnpjDigits).matches()) {
            addConstraintViolation(context, "msg.validation.request.field.cpfcnpj.invalid");
            return false;
        }
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
        int digit1 = calculateCpfVerificationDigit(cpf, new int[]{
                CPF_WEIGHT_10, CPF_WEIGHT_9, CPF_WEIGHT_8, CPF_WEIGHT_7, CPF_WEIGHT_6,
                CPF_WEIGHT_5, CPF_WEIGHT_4, CPF_WEIGHT_3, CPF_WEIGHT_2
        }, CPF_FIRST_DIGIT_POSITION);

        int digit2 = calculateCpfVerificationDigit(cpf, new int[]{
                CPF_WEIGHT_11, CPF_WEIGHT_10, CPF_WEIGHT_9, CPF_WEIGHT_8, CPF_WEIGHT_7,
                CPF_WEIGHT_6, CPF_WEIGHT_5, CPF_WEIGHT_4, CPF_WEIGHT_3, CPF_WEIGHT_2
        }, CPF_SECOND_DIGIT_POSITION);

        return (cpf.charAt(CPF_FIRST_DIGIT_POSITION) - '0' == digit1) &&
                (cpf.charAt(CPF_SECOND_DIGIT_POSITION) - '0' == digit2);
    }

    /**
     * Validates whether the provided CNPJ is valid.
     * <p>
     * Supports both legacy numeric-only CNPJ and the new alphanumeric CNPJ format.
     * In the alphanumeric format, each character's value is calculated by subtracting 48
     * from its ASCII code (e.g., '0'=0, '9'=9, 'A'=17, 'Z'=42).
     * <p>
     * The verification digit algorithm uses modulo 11 with weights distributed from 2 to 9
     * (right to left, cycling back after 9).
     *
     * @param cnpj the CNPJ string to be validated, expected to be a string of length 14
     * @return true if the CNPJ is valid, according to the validation rules; false otherwise
     */
    private boolean isValidCnpj(String cnpj) {
        if (isRepeatedCharacters(cnpj) || cnpj.length() != CNPJ_LENGTH) {
            return false;
        }
        int digit1 = calculateCnpjVerificationDigit(cnpj, new int[]{
                CNPJ_WEIGHT_5, CNPJ_WEIGHT_4, CNPJ_WEIGHT_3, CNPJ_WEIGHT_2, CNPJ_WEIGHT_9, CNPJ_WEIGHT_8,
                CNPJ_WEIGHT_7, CNPJ_WEIGHT_6, CNPJ_WEIGHT_5, CNPJ_WEIGHT_4, CNPJ_WEIGHT_3, CNPJ_WEIGHT_2
        }, CNPJ_FIRST_DIGIT_POSITION);

        int digit2 = calculateCnpjVerificationDigit(cnpj, new int[]{
                CNPJ_WEIGHT_6, CNPJ_WEIGHT_5, CNPJ_WEIGHT_4, CNPJ_WEIGHT_3, CNPJ_WEIGHT_2, CNPJ_WEIGHT_9,
                CNPJ_WEIGHT_8, CNPJ_WEIGHT_7, CNPJ_WEIGHT_6, CNPJ_WEIGHT_5, CNPJ_WEIGHT_4, CNPJ_WEIGHT_3, CNPJ_WEIGHT_2
        }, CNPJ_SECOND_DIGIT_POSITION);

        return (cnpj.charAt(CNPJ_FIRST_DIGIT_POSITION) - '0' == digit1) &&
                (cnpj.charAt(CNPJ_SECOND_DIGIT_POSITION) - '0' == digit2);
    }

    /**
     * Checks if all characters in the given string are the same digit.
     * Used for CPF validation (numeric only).
     *
     * @param value the string to be checked for repeated digits
     * @return true if all characters in the string are the same, false otherwise
     */
    private boolean isRepeatedDigits(String value) {
        return value.chars().distinct().count() == 1;
    }

    /**
     * Checks if all characters in the given string are the same character.
     * Used for CNPJ validation (supports alphanumeric).
     *
     * @param value the string to be checked for repeated characters
     * @return true if all characters in the string are the same, false otherwise
     */
    private boolean isRepeatedCharacters(String value) {
        return value.chars().distinct().count() == 1;
    }

    /**
     * Calculates the verification digit for CPF using numeric-only logic.
     * This preserves the original behavior for CPF validation.
     *
     * @param value   the numeric string for which the verification digit should be calculated
     * @param weights an array of integers representing the weights to be applied to each digit
     * @param length  the number of digits from the value to be considered for the calculation
     * @return the calculated verification digit as an integer
     */
    private int calculateCpfVerificationDigit(String value, int[] weights, int length) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += ((value.charAt(i) - '0') * weights[i]);
        }
        int remainder = (sum % VERIFICATION_DIGIT_MODULO);
        return (remainder < VERIFICATION_DIGIT_THRESHOLD) ? 0 : (VERIFICATION_DIGIT_BASE - remainder);
    }

    /**
     * Calculates the verification digit for CNPJ using alphanumeric-compatible logic.
     * <p>
     * Each character is converted to its calculation value by subtracting 48 from its ASCII code:
     * <ul>
     *   <li>'0' (ASCII 48) → 0</li>
     *   <li>'1' (ASCII 49) → 1</li>
     *   <li>...</li>
     *   <li>'9' (ASCII 57) → 9</li>
     *   <li>'A' (ASCII 65) → 17</li>
     *   <li>'B' (ASCII 66) → 18</li>
     *   <li>...</li>
     *   <li>'Z' (ASCII 90) → 42</li>
     * </ul>
     * <p>
     * This method is fully backward-compatible: for numeric-only CNPJs,
     * (char - 48) produces the same result as (char - '0'), since '0' == 48.
     *
     * @param value   the alphanumeric string for which the verification digit should be calculated
     * @param weights an array of integers representing the weights to be applied to each character
     * @param length  the number of characters from the value to be considered for the calculation
     * @return the calculated verification digit as an integer
     */
    private int calculateCnpjVerificationDigit(String value, int[] weights, int length) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            int charValue = value.charAt(i) - ASCII_OFFSET;
            sum += (charValue * weights[i]);
        }
        int remainder = (sum % VERIFICATION_DIGIT_MODULO);
        return (remainder < VERIFICATION_DIGIT_THRESHOLD) ? 0 : (VERIFICATION_DIGIT_BASE - remainder);
    }
}