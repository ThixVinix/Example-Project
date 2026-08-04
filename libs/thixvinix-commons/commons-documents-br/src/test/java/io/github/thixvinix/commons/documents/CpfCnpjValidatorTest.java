package io.github.thixvinix.commons.documents;

import io.github.thixvinix.commons.i18n.Messages;
import io.github.thixvinix.commons.i18n.testing.CommonsI18nExtension;
import io.github.thixvinix.commons.i18n.testing.TestLocale;
import io.github.thixvinix.commons.validation.testing.ConstraintViolationCapture;

import jakarta.validation.ConstraintValidatorContext;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link CpfCnpjValidator}.
 */
@Tag(value = "CpfCnpjValidator_Tests")
@DisplayName("CpfCnpjValidator Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(CommonsI18nExtension.class)
class CpfCnpjValidatorTest {

    // ── Tested method names ───────────────────────────────────────────────────
    private static final String IS_VALID = "isValid";
    private static final String INITIALIZE = "initialize";

    // ── Test data values ──────────────────────────────────────────────────────
    private static final String VALID_CPF = "529.982.247-25";
    private static final String VALID_CPF_NUMERIC = "52998224725";
    private static final String VALID_CNPJ = "11.222.333/0001-81";
    private static final String VALID_CNPJ_NUMERIC = "11222333000181";
    private static final String INVALID_CPF = "00000000000";
    private static final String INVALID_CNPJ = "00000000000000";
    private static final String INVALID_CPF_CHECK_DIGITS = "52998224799";
    private static final String INVALID_CNPJ_CHECK_DIGITS = "11222333000199";

    // ── I18N: CSV delimiter ───────────────────────────────────────────────────
    private static final char CSV_DELIMITER = '|';

    // ── SUT ────────────────────────────────────────────────────────────────────
    private final CpfCnpjValidator cpfCnpjValidator = new CpfCnpjValidator();

    private ConstraintValidatorContext context;
    private java.util.List<String> capturedMessages;

    @BeforeEach
    void setUp() {
        ConstraintViolationCapture.Captured captured = ConstraintViolationCapture.mockContext();
        context = captured.context();
        capturedMessages = captured.messages();

        CpfCnpjValidation cpfCnpjValidation = mockAnnotation();
        cpfCnpjValidator.initialize(cpfCnpjValidation);
    }

    private static CpfCnpjValidation mockAnnotation() {
        return org.mockito.Mockito.mock(CpfCnpjValidation.class);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // initialize
    // ═════════════════════════════════════════════════════════════════════════

    @Order(1)
    @Tag(value = INITIALIZE)
    @DisplayName(INITIALIZE + " Given a CpfCnpj annotation, then should initialize correctly")
    @Test
    void initialize_WhenCpfCnpjAnnotation_ThenShouldInitializeCorrectly() {
        CpfCnpjValidation cpfCnpjValidation = mockAnnotation();

        assertDoesNotThrow(() -> cpfCnpjValidator.initialize(cpfCnpjValidation),
                "initialize should not throw an exception");
    }

    // ═════════════════════════════════════════════════════════════════════════
    // isValid — blank / null
    // ═════════════════════════════════════════════════════════════════════════

    @Order(2)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given a null value, then should return true")
    @Test
    void isValid_WhenNullValue_ThenShouldReturnTrue() {
        assertTrue(cpfCnpjValidator.isValid(null, context), "isValid should return true for a null value");
    }

    @Order(3)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given an empty value, then should return true")
    @Test
    void isValid_WhenEmptyValue_ThenShouldReturnTrue() {
        assertTrue(cpfCnpjValidator.isValid("", context), "isValid should return true for an empty value");
    }

    // ═════════════════════════════════════════════════════════════════════════
    // isValid — CPF / CNPJ happy path
    // ═════════════════════════════════════════════════════════════════════════

    @Order(4)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given a valid CPF without formatting, then should return true")
    @Test
    void isValid_WhenValidCpfWithoutFormatting_ThenShouldReturnTrue() {
        assertTrue(cpfCnpjValidator.isValid(VALID_CPF_NUMERIC, context),
                "isValid should return true for a valid CPF without formatting");
    }

    @Order(5)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given a valid numeric CNPJ without formatting, then should return true")
    @Test
    void isValid_WhenValidCnpjWithoutFormatting_ThenShouldReturnTrue() {
        assertTrue(cpfCnpjValidator.isValid(VALID_CNPJ_NUMERIC, context),
                "isValid should return true for a valid numeric CNPJ without formatting");
    }

    @Order(6)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given valid alphanumeric CNPJs, then should return true")
    @ParameterizedTest(name = "[{index}] cnpj={0}")
    @CsvSource(value = {
            "AT18C9ER000133",
            "C4HXD8K6000138",
            "76RNY9ZB000117",
            "3LRVVK6M000184",
            "WNEN73GW000124",
            "WXW92C47000155",
            "74J9JLL0000102",
            "PZKRJNM8000166",
            "LJ92Y071000131",
            "K18459AZ000102"
    })
    void isValid_WhenValidAlphanumericCnpj_ThenShouldReturnTrue(String cnpj) {
        assertTrue(cpfCnpjValidator.isValid(cnpj, context),
                "isValid should return true for valid alphanumeric CNPJ: " + cnpj);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // isValid — invalid format (with formatting characters)
    // ═════════════════════════════════════════════════════════════════════════

    @Order(7)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given a valid CPF with formatting, then should return false")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|CPF ou CNPJ deve conter apenas caracteres numéricos, sem formatação ou caracteres especiais.",
            "en|CPF or CNPJ must contain only numeric characters, without formatting or special characters."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenValidCpfWithFormatting_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        TestLocale.set(languageTag);

        assertFalse(cpfCnpjValidator.isValid(VALID_CPF, context),
                "isValid should return false for a CPF with formatting");
        assertEquals(expectedMessage, lastCapturedMessage(languageTag));
    }

    @Order(8)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given a valid CNPJ with formatting, then should return false")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|O CPF ou CNPJ deve conter 11 ou 14 dígitos, respectivamente.",
            "en|CPF or CNPJ must contain exactly 11 or 14 digits, respectively."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenValidCnpjWithFormatting_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        TestLocale.set(languageTag);

        // VALID_CNPJ = "11.222.333/0001-81" has 18 characters — invalid length triggers invalidLength message
        assertFalse(cpfCnpjValidator.isValid(VALID_CNPJ, context),
                "isValid should return false for a CNPJ with formatting");
        assertEquals(expectedMessage, lastCapturedMessage(languageTag));
    }

    // ═════════════════════════════════════════════════════════════════════════
    // isValid — invalid length
    // ═════════════════════════════════════════════════════════════════════════

    @Order(9)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given a value with invalid length, then should return false")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|O CPF ou CNPJ deve conter 11 ou 14 dígitos, respectivamente.",
            "en|CPF or CNPJ must contain exactly 11 or 14 digits, respectively."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidLength_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        TestLocale.set(languageTag);
        String invalidLengthValue = "1234567890"; // 10 digits

        assertFalse(cpfCnpjValidator.isValid(invalidLengthValue, context),
                "isValid should return false for a value with invalid length");
        assertEquals(expectedMessage, lastCapturedMessage(languageTag));
    }

    // ═════════════════════════════════════════════════════════════════════════
    // isValid — invalid CPF (repeated digits / bad check digit)
    // ═════════════════════════════════════════════════════════════════════════

    @Order(10)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given a CPF with repeated digits, then should return false")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|CPF inválido. O cálculo dos dígitos verificadores falhou.",
            "en|Invalid CPF. Check digit validation failed."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenCpfWithRepeatedDigits_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        TestLocale.set(languageTag);
        String repeatedDigitsCpf = "11111111111";

        assertFalse(cpfCnpjValidator.isValid(repeatedDigitsCpf, context),
                "isValid should return false for a CPF with repeated digits");
        assertEquals(expectedMessage, lastCapturedMessage(languageTag));
    }

    @Order(11)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given a CPF with invalid check digits, then should return false")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|CPF inválido. O cálculo dos dígitos verificadores falhou.",
            "en|Invalid CPF. Check digit validation failed."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenCpfWithInvalidCheckDigits_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        TestLocale.set(languageTag);

        assertFalse(cpfCnpjValidator.isValid(INVALID_CPF_CHECK_DIGITS, context),
                "isValid should return false for a CPF with invalid check digits");
        assertEquals(expectedMessage, lastCapturedMessage(languageTag));
    }

    @Order(12)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given an invalid CPF (all zeros), then should return false")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|CPF inválido. O cálculo dos dígitos verificadores falhou.",
            "en|Invalid CPF. Check digit validation failed."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidCpf_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        TestLocale.set(languageTag);

        assertFalse(cpfCnpjValidator.isValid(INVALID_CPF, context),
                "isValid should return false for an invalid CPF");
        assertEquals(expectedMessage, lastCapturedMessage(languageTag));
    }

    // ═════════════════════════════════════════════════════════════════════════
    // isValid — invalid CNPJ (repeated digits / bad check digit)
    // ═════════════════════════════════════════════════════════════════════════

    @Order(13)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given a CNPJ with repeated digits, then should return false")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|CNPJ inválido. O cálculo dos dígitos verificadores falhou.",
            "en|Invalid CNPJ. Check digit validation failed."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenCnpjWithRepeatedDigits_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        TestLocale.set(languageTag);
        String repeatedDigitsCnpj = "11111111111111";

        assertFalse(cpfCnpjValidator.isValid(repeatedDigitsCnpj, context),
                "isValid should return false for a CNPJ with repeated digits");
        assertEquals(expectedMessage, lastCapturedMessage(languageTag));
    }

    @Order(14)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given a CNPJ with invalid check digits, then should return false")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|CNPJ inválido. O cálculo dos dígitos verificadores falhou.",
            "en|Invalid CNPJ. Check digit validation failed."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenCnpjWithInvalidCheckDigits_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        TestLocale.set(languageTag);

        assertFalse(cpfCnpjValidator.isValid(INVALID_CNPJ_CHECK_DIGITS, context),
                "isValid should return false for a CNPJ with invalid check digits");
        assertEquals(expectedMessage, lastCapturedMessage(languageTag));
    }

    @Order(15)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given an invalid CNPJ (all zeros), then should return false")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|CNPJ inválido. O cálculo dos dígitos verificadores falhou.",
            "en|Invalid CNPJ. Check digit validation failed."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidCnpj_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        TestLocale.set(languageTag);

        assertFalse(cpfCnpjValidator.isValid(INVALID_CNPJ, context),
                "isValid should return false for an invalid CNPJ");
        assertEquals(expectedMessage, lastCapturedMessage(languageTag));
    }

    // ═════════════════════════════════════════════════════════════════════════
    // isValid — invalid alphanumeric CNPJ (bad check digit / lowercase)
    // ═════════════════════════════════════════════════════════════════════════

    @Order(16)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given an alphanumeric CNPJ with invalid check digits, then should return false")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|CNPJ inválido. O cálculo dos dígitos verificadores falhou.",
            "en|Invalid CNPJ. Check digit validation failed."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenAlphanumericCnpjWithInvalidCheckDigits_ThenShouldReturnFalse(
            String languageTag, String expectedMessage) {
        TestLocale.set(languageTag);
        // AT18C9ER000133 is valid; changing last two digits to 99 makes it invalid
        String invalidAlphaCnpj = "AT18C9ER000199";

        assertFalse(cpfCnpjValidator.isValid(invalidAlphaCnpj, context),
                "isValid should return false for an alphanumeric CNPJ with invalid check digits");
        assertEquals(expectedMessage, lastCapturedMessage(languageTag));
    }

    @Order(17)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given an alphanumeric CNPJ with lowercase letters, then should return false")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|CPF ou CNPJ deve conter apenas caracteres numéricos, sem formatação ou caracteres especiais.",
            "en|CPF or CNPJ must contain only numeric characters, without formatting or special characters."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenAlphanumericCnpjWithLowercaseLetters_ThenShouldReturnFalse(
            String languageTag, String expectedMessage) {
        TestLocale.set(languageTag);
        // Lowercase letters are not allowed in alphanumeric CNPJ (only 0-9 and A-Z)
        String lowercaseCnpj = "at18c9er000133";

        assertFalse(cpfCnpjValidator.isValid(lowercaseCnpj, context),
                "isValid should return false for an alphanumeric CNPJ with lowercase letters");
        assertEquals(expectedMessage, lastCapturedMessage(languageTag));
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Anti-regression — i18n key existence
    // ═════════════════════════════════════════════════════════════════════════

    @Order(99)
    @Tag(value = "i18n_key_existence")
    @DisplayName("i18n Given all error message keys, then they must resolve in pt_BR and en")
    @ParameterizedTest(name = "[{index}] key={0} | lang={1}")
    @CsvSource(value = {
            DocumentMessageKeys.CPFCNPJ_INVALID + "|pt_BR",
            DocumentMessageKeys.CPFCNPJ_INVALID + "|en",
            DocumentMessageKeys.CPFCNPJ_INVALID_LENGTH + "|pt_BR",
            DocumentMessageKeys.CPFCNPJ_INVALID_LENGTH + "|en",
            DocumentMessageKeys.CPF_INVALID_CHECK_DIGIT + "|pt_BR",
            DocumentMessageKeys.CPF_INVALID_CHECK_DIGIT + "|en",
            DocumentMessageKeys.CNPJ_INVALID_CHECK_DIGIT + "|pt_BR",
            DocumentMessageKeys.CNPJ_INVALID_CHECK_DIGIT + "|en"
    }, delimiter = CSV_DELIMITER)
    void i18n_WhenErrorKeyResolved_ThenShouldNotReturnTheKeyItself(String messageKey, String languageTag) {
        TestLocale.set(languageTag);

        String resolved = Messages.get(TestLocale.get(), messageKey);

        assertNotNull(resolved,
                "Resolved message should not be null for key=" + messageKey + " lang=" + languageTag);
        assertNotEquals(messageKey, resolved,
                "Resolved message equals the key itself — key is MISSING from .properties file: "
                        + messageKey + " | lang=" + languageTag);
    }

    private String lastCapturedMessage(String languageTag) {
        assertFalse(capturedMessages.isEmpty(), "No constraint violation was captured for lang=" + languageTag);
        return capturedMessages.get(capturedMessages.size() - 1);
    }
}
