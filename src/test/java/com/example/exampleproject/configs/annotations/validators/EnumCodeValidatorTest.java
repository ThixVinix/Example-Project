package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.MessageConfig;
import com.example.exampleproject.configs.annotations.EnumCodeValidation;
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
 * Tests for class {@link EnumCodeValidator}
 */
@SpringBootTest(classes = {MessageConfig.class, MessageUtils.class})
@Tag(value = "EnumCodeValidator_Tests")
@DisplayName("EnumCodeValidator Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class EnumCodeValidatorTest {

    /**
     * Test enum with getCode method for testing EnumCodeValidator
     */
    public enum TestCodeEnum {
        CODE_ONE(1),
        CODE_TWO(2),
        CODE_THREE(3);

        private final int code;

        TestCodeEnum(int code) {
            this.code = code;
        }

        @SuppressWarnings("unused")
        public Integer getCode() {
            return code;
        }
    }


    /**
     * Test enum without getCode method for testing error handling
     */
    private enum TestInvalidEnum {
        INVALID_ONE,
        INVALID_TWO,
        INVALID_THREE
    }

    private static final char CSV_DELIMITER = '|';
    private static final String IS_VALID = "isValid";
    private static final String INITIALIZE = "initialize";

    private Locale defaultLocale;

    private ConstraintValidatorContext context;

    @InjectMocks
    private EnumCodeValidator enumCodeValidator;

    @BeforeEach
    void setUp() {
        defaultLocale = LocaleContextHolder.getLocale();
        context = mock(ConstraintValidatorContext.class);
        EnumCodeValidation enumCodeValidation = mock(EnumCodeValidation.class);
        when(enumCodeValidation.enumClass()).thenAnswer(_ -> TestCodeEnum.class);
        enumCodeValidator.initialize(enumCodeValidation);
    }

    @AfterEach
    void tearDown() {
        LocaleContextHolder.setLocale(defaultLocale);
    }

    /**
     * Method test for
     * {@link EnumCodeValidator#isValid(Integer, ConstraintValidatorContext)}
     */
    @Order(1)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a valid enum code, then should return true")
    @Test
    void isValid_WhenValidEnumCode_ThenShouldReturnTrue() {
        // Act
        boolean isValid = enumCodeValidator.isValid(1, context);

        // Assert
        assertTrue(isValid, "isValid should return true for a valid enum code");
    }

    /**
     * Method test for
     * {@link EnumCodeValidator#isValid(Integer, ConstraintValidatorContext)}
     */
    @Order(2)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a null value, then should return true")
    @Test
    void isValid_WhenNullValue_ThenShouldReturnTrue() {
        // Act
        boolean isValid = enumCodeValidator.isValid(null, context);

        // Assert
        assertTrue(isValid, "isValid should return true for a null value");
    }

    /**
     * Method test for
     * {@link EnumCodeValidator#isValid(Integer, ConstraintValidatorContext)}
     */
    @Order(3)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given an invalid enum code, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O código 99 é inválido. Atualmente, os códigos válidos são: 1, 2, 3.",
            "en_US|The code 99 is invalid. Currently, the valid codes are: 1, 2, 3."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidEnumCode_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = enumCodeValidator.isValid(99, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for an invalid enum code");
    }

    /**
     * Method test for
     * {@link EnumCodeValidator#initialize(EnumCodeValidation)}
     */
    @Order(4)
    @Tag(value = INITIALIZE)
    @DisplayName(INITIALIZE + " - Given an enum without getCode method, then should handle error")
    @Test
    void initialize_WhenEnumWithoutGetCodeMethod_ThenShouldHandleError() {
        // Arrange
        EnumCodeValidation enumCodeValidation = mock(EnumCodeValidation.class);
        when(enumCodeValidation.enumClass()).thenAnswer(_ -> TestInvalidEnum.class);
        EnumCodeValidator localValidator = new EnumCodeValidator();

        // Act & Assert
        assertDoesNotThrow(() -> localValidator.initialize(enumCodeValidation),
                "initialize should not throw an exception when enum doesn't have getCode method");

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        boolean isValid = localValidator.isValid(1, context);
        assertFalse(isValid, "isValid should return false when enum doesn't have getCode method");
    }

    /**
     * Method test for
     * {@link EnumCodeValidator#isValid(Integer, ConstraintValidatorContext)}
     */
    @Order(5)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given hideValidOptions=true, then should hide valid codes in error message")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O código 99 é inválido.",
            "en_US|The code 99 is invalid."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenHideValidOptionsTrue_ThenShouldHideValidCodes(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        EnumCodeValidation enumCodeValidation = mock(EnumCodeValidation.class);
        when(enumCodeValidation.enumClass()).thenAnswer(_ -> TestCodeEnum.class);
        when(enumCodeValidation.hideValidOptions()).thenReturn(true);

        EnumCodeValidator localValidator = new EnumCodeValidator();
        localValidator.initialize(enumCodeValidation);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = localValidator.isValid(99, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(capturedMessage.contains("valid codes are"));
        assertFalse(capturedMessage.contains("1, 2, 3"));
        assertFalse(isValid, "isValid should return false for an invalid enum code");
    }
}
