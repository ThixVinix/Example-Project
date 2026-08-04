package io.github.thixvinix.commons.enums;

import io.github.thixvinix.commons.i18n.testing.CommonsI18nExtension;
import io.github.thixvinix.commons.i18n.testing.TestLocale;
import io.github.thixvinix.commons.validation.testing.ConstraintViolationCapture;

import jakarta.validation.ConstraintValidatorContext;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link EnumCodeValidator}.
 */
@Tag(value = "EnumCodeValidator_Tests")
@DisplayName("EnumCodeValidator Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(CommonsI18nExtension.class)
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

    private ConstraintValidatorContext context;
    private List<String> capturedMessages;
    private EnumCodeValidator enumCodeValidator;

    @BeforeEach
    void setUp() {
        ConstraintViolationCapture.Captured captured = ConstraintViolationCapture.mockContext();
        context = captured.context();
        capturedMessages = captured.messages();

        enumCodeValidator = new EnumCodeValidator();
        EnumCodeValidation enumCodeValidation = mock(EnumCodeValidation.class);
        when(enumCodeValidation.enumClass()).thenAnswer(inv -> TestCodeEnum.class);
        enumCodeValidator.initialize(enumCodeValidation);
    }

    @Order(1)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a valid enum code, then should return true")
    @Test
    void isValid_WhenValidEnumCode_ThenShouldReturnTrue() {
        assertTrue(enumCodeValidator.isValid(1, context), "isValid should return true for a valid enum code");
    }

    @Order(2)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a null value, then should return true")
    @Test
    void isValid_WhenNullValue_ThenShouldReturnTrue() {
        assertTrue(enumCodeValidator.isValid(null, context), "isValid should return true for a null value");
    }

    @Order(3)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given an invalid enum code, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O código 99 é inválido. Atualmente, os códigos válidos são: 1, 2, 3.",
            "en_US|The code 99 is invalid. Currently, the valid codes are: 1, 2, 3."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidEnumCode_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        TestLocale.set(languageTag);

        boolean isValid = enumCodeValidator.isValid(99, context);

        assertEquals(expectedMessage, lastMessage());
        assertFalse(isValid, "isValid should return false for an invalid enum code");
    }

    @Order(4)
    @Tag(value = INITIALIZE)
    @DisplayName(INITIALIZE + " - Given an enum without getCode method, then should handle error")
    @Test
    void initialize_WhenEnumWithoutGetCodeMethod_ThenShouldHandleError() {
        EnumCodeValidation enumCodeValidation = mock(EnumCodeValidation.class);
        when(enumCodeValidation.enumClass()).thenAnswer(inv -> TestInvalidEnum.class);
        EnumCodeValidator localValidator = new EnumCodeValidator();

        assertDoesNotThrow(() -> localValidator.initialize(enumCodeValidation),
                "initialize should not throw an exception when enum doesn't have getCode method");

        boolean isValid = localValidator.isValid(1, context);
        assertFalse(isValid, "isValid should return false when enum doesn't have getCode method");
    }

    @Order(5)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given hideValidOptions=true, then should hide valid codes in error message")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O código 99 é inválido.",
            "en_US|The code 99 is invalid."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenHideValidOptionsTrue_ThenShouldHideValidCodes(String languageTag, String expectedMessage) {
        TestLocale.set(languageTag);

        EnumCodeValidation enumCodeValidation = mock(EnumCodeValidation.class);
        when(enumCodeValidation.enumClass()).thenAnswer(inv -> TestCodeEnum.class);
        when(enumCodeValidation.hideValidOptions()).thenReturn(true);

        EnumCodeValidator localValidator = new EnumCodeValidator();
        localValidator.initialize(enumCodeValidation);

        boolean isValid = localValidator.isValid(99, context);

        String capturedMessage = lastMessage();
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(capturedMessage.contains("valid codes are"));
        assertFalse(capturedMessage.contains("1, 2, 3"));
        assertFalse(isValid, "isValid should return false for an invalid enum code");
    }

    private String lastMessage() {
        assertFalse(capturedMessages.isEmpty(), "No constraint violation was captured");
        return capturedMessages.get(capturedMessages.size() - 1);
    }
}
