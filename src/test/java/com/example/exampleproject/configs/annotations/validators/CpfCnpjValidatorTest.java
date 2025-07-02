package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.MessageConfig;
import com.example.exampleproject.configs.annotations.CpfCnpjValidation;
import com.example.exampleproject.utils.MessageUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintValidatorContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for class {@link CpfCnpjValidator}
 */
@SpringBootTest(classes = {MessageConfig.class, MessageUtils.class})
@Tag(value = "CpfCnpjValidator_Tests")
@DisplayName("CpfCnpjValidator Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class CpfCnpjValidatorTest {

    private static final char CSV_DELIMITER = '|';
    private static final String IS_VALID = "isValid";
    private static final String INITIALIZE = "initialize";

    // Valid CPF and CNPJ examples
    private static final String VALID_CPF = "529.982.247-25";
    private static final String VALID_CPF_NUMERIC = "52998224725";
    private static final String VALID_CNPJ = "11.222.333/0001-81";
    private static final String VALID_CNPJ_NUMERIC = "11222333000181";

    // Invalid CPF and CNPJ examples (without formatting but with invalid check digits)
    private static final String INVALID_CPF = "00000000000";
    private static final String INVALID_CNPJ = "00000000000000";

    private Locale defaultLocale;

    private ConstraintValidatorContext context;

    @InjectMocks
    private CpfCnpjValidator cpfCnpjValidator;

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

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(1)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a valid CPF with formatting, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|CPF ou CNPJ deve conter apenas caracteres numéricos, sem formatação ou caracteres especiais.",
            "en_US|CPF or CNPJ must contain only numeric characters, without formatting or special characters."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenValidCpfWithFormatting_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = cpfCnpjValidator.isValid(VALID_CPF, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a CPF with formatting");
    }

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(2)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a valid CPF without formatting, then should return true")
    @Test
    void isValid_WhenValidCpfWithoutFormatting_ThenShouldReturnTrue() {
        // Act
        boolean isValid = cpfCnpjValidator.isValid(VALID_CPF_NUMERIC, context);

        // Assert
        assertTrue(isValid, "isValid should return true for a valid CPF without formatting");
    }

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(3)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a valid CNPJ with formatting, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|CPF ou CNPJ deve conter apenas caracteres numéricos, sem formatação ou caracteres especiais.",
            "en_US|CPF or CNPJ must contain only numeric characters, without formatting or special characters."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenValidCnpjWithFormatting_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = cpfCnpjValidator.isValid(VALID_CNPJ, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a CNPJ with formatting");
    }

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(4)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a valid CNPJ without formatting, then should return true")
    @Test
    void isValid_WhenValidCnpjWithoutFormatting_ThenShouldReturnTrue() {
        // Act
        boolean isValid = cpfCnpjValidator.isValid(VALID_CNPJ_NUMERIC, context);

        // Assert
        assertTrue(isValid, "isValid should return true for a valid CNPJ without formatting");
    }

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(5)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a null value, then should return true")
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
    @Order(6)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given an empty value, then should return true")
    @Test
    void isValid_WhenEmptyValue_ThenShouldReturnTrue() {
        // Act
        boolean isValid = cpfCnpjValidator.isValid("", context);

        // Assert
        assertTrue(isValid, "isValid should return true for an empty value");
    }

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(7)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given an invalid CPF, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|CPF inválido. O cálculo dos dígitos verificadores falhou.",
            "en_US|Invalid CPF. Check digit validation failed."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidCpf_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = cpfCnpjValidator.isValid(INVALID_CPF, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for an invalid CPF");
    }

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(8)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given an invalid CNPJ, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|CNPJ inválido. O cálculo dos dígitos verificadores falhou.",
            "en_US|Invalid CNPJ. Check digit validation failed."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidCnpj_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = cpfCnpjValidator.isValid(INVALID_CNPJ, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for an invalid CNPJ");
    }

    /**
     * Method test for
     * {@link CpfCnpjValidator#initialize(CpfCnpjValidation)}
     */
    @Order(9)
    @Tag(value = INITIALIZE)
    @DisplayName(INITIALIZE + " - Given a CpfCnpj annotation, then should initialize correctly")
    @Test
    void initialize_WhenCpfCnpjAnnotation_ThenShouldInitializeCorrectly() {
        // Arrange
        CpfCnpjValidation cpfCnpjValidation = mock(CpfCnpjValidation.class);

        // Act
        cpfCnpjValidator.initialize(cpfCnpjValidation);

        // Assert
        // No exception should be thrown
        assertDoesNotThrow(() -> cpfCnpjValidator.isValid(VALID_CPF, context),
                "initialize should not throw an exception");
    }

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(10)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a CPF with invalid length, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O CPF ou CNPJ deve conter 11 ou 14 dígitos, respectivamente.",
            "en_US|CPF or CNPJ must contain exactly 11 or 14 digits, respectively."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenCpfWithInvalidLength_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        String invalidLengthCpf = "1234567890"; // 10 digits instead of 11
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = cpfCnpjValidator.isValid(invalidLengthCpf, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a CPF with invalid length");
    }

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(11)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a CNPJ with invalid length, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O CPF ou CNPJ deve conter 11 ou 14 dígitos, respectivamente.",
            "en_US|CPF or CNPJ must contain exactly 11 or 14 digits, respectively."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenCnpjWithInvalidLength_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        String invalidLengthCnpj = "1234567890123"; // 13 digits instead of 14
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = cpfCnpjValidator.isValid(invalidLengthCnpj, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a CNPJ with invalid length");
    }

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(12)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a CPF with repeated digits, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|CPF inválido. O cálculo dos dígitos verificadores falhou.",
            "en_US|Invalid CPF. Check digit validation failed."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenCpfWithRepeatedDigits_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        String repeatedDigitsCpf = "11111111111"; // All digits are the same
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = cpfCnpjValidator.isValid(repeatedDigitsCpf, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a CPF with repeated digits");
    }

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(13)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a CNPJ with repeated digits, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|CNPJ inválido. O cálculo dos dígitos verificadores falhou.",
            "en_US|Invalid CNPJ. Check digit validation failed."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenCnpjWithRepeatedDigits_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        String repeatedDigitsCnpj = "11111111111111"; // All digits are the same
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = cpfCnpjValidator.isValid(repeatedDigitsCnpj, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a CNPJ with repeated digits");
    }

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(14)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a value with non-numeric characters, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|CPF ou CNPJ deve conter apenas caracteres numéricos, sem formatação ou caracteres especiais.",
            "en_US|CPF or CNPJ must contain only numeric characters, without formatting or special characters."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenValueWithNonNumericCharacters_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        String nonNumericValue = "123abc456789"; // Contains letters
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = cpfCnpjValidator.isValid(nonNumericValue, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a value with non-numeric characters");
    }

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(15)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a CPF with invalid check digits, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|CPF inválido. O cálculo dos dígitos verificadores falhou.",
            "en_US|Invalid CPF. Check digit validation failed."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenCpfWithInvalidCheckDigits_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        String invalidCheckDigitsCpf = "52998224799";
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = cpfCnpjValidator.isValid(invalidCheckDigitsCpf, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a CPF with invalid check digits");
    }

    /**
     * Method test for
     * {@link CpfCnpjValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(16)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a CNPJ with invalid check digits, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|CNPJ inválido. O cálculo dos dígitos verificadores falhou.",
            "en_US|Invalid CNPJ. Check digit validation failed."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenCnpjWithInvalidCheckDigits_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        String invalidCheckDigitsCnpj = "11222333000199";
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = cpfCnpjValidator.isValid(invalidCheckDigitsCnpj, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a CNPJ with invalid check digits");
    }
}
