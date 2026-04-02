package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.CpfCnpjValidation;
import com.example.exampleproject.utils.MessageUtils;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for class {@link CpfCnpjValidator}
 */
@Tag(value = "CpfCnpjValidator_Tests")
@DisplayName("CpfCnpjValidator Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
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

    // ── Mocks / SUT ───────────────────────────────────────────────────────────
    @InjectMocks
    private CpfCnpjValidator cpfCnpjValidator;

    private ConstraintValidatorContext context;

    private Locale defaultLocale;

    // ═════════════════════════════════════════════════════════════════════════
    // SETUP / TEARDOWN
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Configures a REAL ResourceBundleMessageSource so that MessageUtils reads
     * from messages_pt_BR.properties and messages_en.properties on disk.
     * MessageUtils is NEVER mocked.
     */
    @BeforeAll
    static void setUpAll() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        ReflectionTestUtils.setField(MessageUtils.class, "messageSourceStatic", messageSource);
    }

    @BeforeEach
    void setUp() {
        defaultLocale = LocaleContextHolder.getLocale();
        context = mock(ConstraintValidatorContext.class);
        CpfCnpjValidation cpfCnpjValidation = mock(CpfCnpjValidation.class);
        cpfCnpjValidator.initialize(cpfCnpjValidation);
    }

    @AfterEach
    void tearDown() {
        LocaleContextHolder.setLocale(defaultLocale);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // LOCALE HELPER
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Converts a language tag from @CsvSource format ("pt_BR", "en") into a Locale
     * and sets it as the current locale for the running test.
     *
     * @param languageTag language tag using underscore notation (e.g. "pt_BR", "en")
     */
    private static void setLocale(String languageTag) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));
    }

    // ═════════════════════════════════════════════════════════════════════════
    // HELPER — mock context for constraint violation capture
    // ═════════════════════════════════════════════════════════════════════════

    private String captureConstraintMessage(Runnable act) {
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        act.run();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(captor.capture());
        return captor.getValue();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // initialize
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Method test for
     * {@link CpfCnpjValidator#initialize(CpfCnpjValidation)}
     */
    @Order(1)
    @Tag(value = INITIALIZE)
    @DisplayName(INITIALIZE + " Given a CpfCnpj annotation, then should initialize correctly")
    @Test
    void initialize_WhenCpfCnpjAnnotation_ThenShouldInitializeCorrectly() {
        // Arrange
        CpfCnpjValidation cpfCnpjValidation = mock(CpfCnpjValidation.class);

        // Act & Assert
        assertDoesNotThrow(() -> cpfCnpjValidator.initialize(cpfCnpjValidation),
                "initialize should not throw an exception");
    }

    // ═════════════════════════════════════════════════════════════════════════
    // isValid — blank / null
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(2)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given a null value, then should return true")
    @Test
    void isValid_WhenNullValue_ThenShouldReturnTrue() {
        // Act
        boolean isValid = cpfCnpjValidator.isValid(null, context);

        // Assert
        assertTrue(isValid, "isValid should return true for a null value");
    }

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(3)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given an empty value, then should return true")
    @Test
    void isValid_WhenEmptyValue_ThenShouldReturnTrue() {
        // Act
        boolean isValid = cpfCnpjValidator.isValid("", context);

        // Assert
        assertTrue(isValid, "isValid should return true for an empty value");
    }

    // ═════════════════════════════════════════════════════════════════════════
    // isValid — CPF happy path
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(4)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given a valid CPF without formatting, then should return true")
    @Test
    void isValid_WhenValidCpfWithoutFormatting_ThenShouldReturnTrue() {
        // Act
        boolean isValid = cpfCnpjValidator.isValid(VALID_CPF_NUMERIC, context);

        // Assert
        assertTrue(isValid, "isValid should return true for a valid CPF without formatting");
    }

    // ═════════════════════════════════════════════════════════════════════════
    // isValid — CNPJ numeric happy path
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(5)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given a valid numeric CNPJ without formatting, then should return true")
    @Test
    void isValid_WhenValidCnpjWithoutFormatting_ThenShouldReturnTrue() {
        // Act
        boolean isValid = cpfCnpjValidator.isValid(VALID_CNPJ_NUMERIC, context);

        // Assert
        assertTrue(isValid, "isValid should return true for a valid numeric CNPJ without formatting");
    }

    // ═════════════════════════════════════════════════════════════════════════
    // isValid — CNPJ alphanumeric happy path
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
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
        // Act
        boolean isValid = cpfCnpjValidator.isValid(cnpj, context);

        // Assert
        assertTrue(isValid, "isValid should return true for valid alphanumeric CNPJ: " + cnpj);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // isValid — invalid format (with formatting characters)
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(7)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given a valid CPF with formatting, then should return false")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|CPF ou CNPJ deve conter apenas caracteres numéricos, sem formatação ou caracteres especiais.",
            "en|CPF or CNPJ must contain only numeric characters, without formatting or special characters."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenValidCpfWithFormatting_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        // Arrange
        setLocale(languageTag);

        // Act & Assert
        String capturedMessage = captureConstraintMessage(
                () -> assertFalse(cpfCnpjValidator.isValid(VALID_CPF, context),
                        "isValid should return false for a CPF with formatting"));

        assertEquals(expectedMessage, capturedMessage,
                "The constraint message should match the localized error for lang=" + languageTag);
    }

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(8)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given a valid CNPJ with formatting, then should return false")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|O CPF ou CNPJ deve conter 11 ou 14 dígitos, respectivamente.",
            "en|CPF or CNPJ must contain exactly 11 or 14 digits, respectively."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenValidCnpjWithFormatting_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        // Arrange
        setLocale(languageTag);

        // Act & Assert
        // VALID_CNPJ = "11.222.333/0001-81" has 18 characters — invalid length triggers invalidLength message
        String capturedMessage = captureConstraintMessage(
                () -> assertFalse(cpfCnpjValidator.isValid(VALID_CNPJ, context),
                        "isValid should return false for a CNPJ with formatting"));

        assertEquals(expectedMessage, capturedMessage,
                "The constraint message should match the localized error for lang=" + languageTag);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // isValid — invalid length
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(9)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given a value with invalid length, then should return false")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|O CPF ou CNPJ deve conter 11 ou 14 dígitos, respectivamente.",
            "en|CPF or CNPJ must contain exactly 11 or 14 digits, respectively."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidLength_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        // Arrange
        setLocale(languageTag);
        String invalidLengthValue = "1234567890"; // 10 digits

        // Act & Assert
        String capturedMessage = captureConstraintMessage(
                () -> assertFalse(cpfCnpjValidator.isValid(invalidLengthValue, context),
                        "isValid should return false for a value with invalid length"));

        assertEquals(expectedMessage, capturedMessage,
                "The constraint message should match the localized error for lang=" + languageTag);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // isValid — invalid CPF (repeated digits / bad check digit)
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(10)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given a CPF with repeated digits, then should return false")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|CPF inválido. O cálculo dos dígitos verificadores falhou.",
            "en|Invalid CPF. Check digit validation failed."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenCpfWithRepeatedDigits_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        // Arrange
        setLocale(languageTag);
        String repeatedDigitsCpf = "11111111111";

        // Act & Assert
        String capturedMessage = captureConstraintMessage(
                () -> assertFalse(cpfCnpjValidator.isValid(repeatedDigitsCpf, context),
                        "isValid should return false for a CPF with repeated digits"));

        assertEquals(expectedMessage, capturedMessage,
                "The constraint message should match the localized error for lang=" + languageTag);
    }

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(11)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given a CPF with invalid check digits, then should return false")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|CPF inválido. O cálculo dos dígitos verificadores falhou.",
            "en|Invalid CPF. Check digit validation failed."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenCpfWithInvalidCheckDigits_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        // Arrange
        setLocale(languageTag);

        // Act & Assert
        String capturedMessage = captureConstraintMessage(
                () -> assertFalse(cpfCnpjValidator.isValid(INVALID_CPF_CHECK_DIGITS, context),
                        "isValid should return false for a CPF with invalid check digits"));

        assertEquals(expectedMessage, capturedMessage,
                "The constraint message should match the localized error for lang=" + languageTag);
    }

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(12)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given an invalid CPF (all zeros), then should return false")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|CPF inválido. O cálculo dos dígitos verificadores falhou.",
            "en|Invalid CPF. Check digit validation failed."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidCpf_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        // Arrange
        setLocale(languageTag);

        // Act & Assert
        String capturedMessage = captureConstraintMessage(
                () -> assertFalse(cpfCnpjValidator.isValid(INVALID_CPF, context),
                        "isValid should return false for an invalid CPF"));

        assertEquals(expectedMessage, capturedMessage,
                "The constraint message should match the localized error for lang=" + languageTag);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // isValid — invalid CNPJ (repeated digits / bad check digit)
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(13)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given a CNPJ with repeated digits, then should return false")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|CNPJ inválido. O cálculo dos dígitos verificadores falhou.",
            "en|Invalid CNPJ. Check digit validation failed."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenCnpjWithRepeatedDigits_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        // Arrange
        setLocale(languageTag);
        String repeatedDigitsCnpj = "11111111111111";

        // Act & Assert
        String capturedMessage = captureConstraintMessage(
                () -> assertFalse(cpfCnpjValidator.isValid(repeatedDigitsCnpj, context),
                        "isValid should return false for a CNPJ with repeated digits"));

        assertEquals(expectedMessage, capturedMessage,
                "The constraint message should match the localized error for lang=" + languageTag);
    }

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(14)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given a CNPJ with invalid check digits, then should return false")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|CNPJ inválido. O cálculo dos dígitos verificadores falhou.",
            "en|Invalid CNPJ. Check digit validation failed."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenCnpjWithInvalidCheckDigits_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        // Arrange
        setLocale(languageTag);

        // Act & Assert
        String capturedMessage = captureConstraintMessage(
                () -> assertFalse(cpfCnpjValidator.isValid(INVALID_CNPJ_CHECK_DIGITS, context),
                        "isValid should return false for a CNPJ with invalid check digits"));

        assertEquals(expectedMessage, capturedMessage,
                "The constraint message should match the localized error for lang=" + languageTag);
    }

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(15)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given an invalid CNPJ (all zeros), then should return false")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|CNPJ inválido. O cálculo dos dígitos verificadores falhou.",
            "en|Invalid CNPJ. Check digit validation failed."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidCnpj_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        // Arrange
        setLocale(languageTag);

        // Act & Assert
        String capturedMessage = captureConstraintMessage(
                () -> assertFalse(cpfCnpjValidator.isValid(INVALID_CNPJ, context),
                        "isValid should return false for an invalid CNPJ"));

        assertEquals(expectedMessage, capturedMessage,
                "The constraint message should match the localized error for lang=" + languageTag);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // isValid — invalid alphanumeric CNPJ (bad check digit)
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
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
        // Arrange
        setLocale(languageTag);
        // AT18C9ER000133 is valid; changing last two digits to 99 makes it invalid
        String invalidAlphaCnpj = "AT18C9ER000199";

        // Act & Assert
        String capturedMessage = captureConstraintMessage(
                () -> assertFalse(cpfCnpjValidator.isValid(invalidAlphaCnpj, context),
                        "isValid should return false for an alphanumeric CNPJ with invalid check digits"));

        assertEquals(expectedMessage, capturedMessage,
                "The constraint message should match the localized error for lang=" + languageTag);
    }

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
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
        // Arrange
        setLocale(languageTag);
        // Lowercase letters are not allowed in alphanumeric CNPJ (only 0-9 and A-Z)
        String lowercaseCnpj = "at18c9er000133";

        // Act & Assert
        String capturedMessage = captureConstraintMessage(
                () -> assertFalse(cpfCnpjValidator.isValid(lowercaseCnpj, context),
                        "isValid should return false for an alphanumeric CNPJ with lowercase letters"));

        assertEquals(expectedMessage, capturedMessage,
                "The constraint message should match the localized error for lang=" + languageTag);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Anti-regression — i18n key existence
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Verifies that every error message key used by {@link CpfCnpjValidator}
     * is present in BOTH messages_pt_BR.properties AND messages_en.properties.
     */
    @Order(99)
    @Tag(value = "i18n_key_existence")
    @DisplayName("i18n Given all error message keys, then they must resolve in pt_BR and en")
    @ParameterizedTest(name = "[{index}] key={0} | lang={1}")
    @CsvSource(value = {
            "msg.validation.request.field.cpfcnpj.invalid|pt_BR",
            "msg.validation.request.field.cpfcnpj.invalid|en",
            "msg.validation.request.field.cpfcnpj.invalidLength|pt_BR",
            "msg.validation.request.field.cpfcnpj.invalidLength|en",
            "msg.validation.request.field.cpf.invalidCheckDigit|pt_BR",
            "msg.validation.request.field.cpf.invalidCheckDigit|en",
            "msg.validation.request.field.cnpj.invalidCheckDigit|pt_BR",
            "msg.validation.request.field.cnpj.invalidCheckDigit|en"
    }, delimiter = CSV_DELIMITER)
    void i18n_WhenErrorKeyResolved_ThenShouldNotReturnTheKeyItself(String messageKey, String languageTag) {
        // Arrange
        setLocale(languageTag);

        // Act
        String resolved = MessageUtils.getMessage(messageKey, LocaleContextHolder.getLocale());

        // Assert
        assertNotNull(resolved,
                "Resolved message should not be null for key=" + messageKey + " lang=" + languageTag);
        assertNotEquals(messageKey, resolved,
                "Resolved message equals the key itself — key is MISSING from .properties file: "
                + messageKey + " | lang=" + languageTag);
    }
}
