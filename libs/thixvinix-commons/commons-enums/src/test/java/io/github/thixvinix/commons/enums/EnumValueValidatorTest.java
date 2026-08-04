package io.github.thixvinix.commons.enums;

import io.github.thixvinix.commons.i18n.testing.CommonsI18nExtension;
import io.github.thixvinix.commons.i18n.testing.TestLocale;
import io.github.thixvinix.commons.validation.testing.ConstraintViolationCapture;

import jakarta.validation.ConstraintValidatorContext;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link EnumValueValidator}.
 */
@Tag(value = "EnumValueValidator_Tests")
@DisplayName("EnumValueValidator Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(CommonsI18nExtension.class)
class EnumValueValidatorTest {

    /**
     * Test enum with getValue method for testing EnumValueValidator
     */
    public enum TestValueEnum {
        VALUE_ONE("one"),
        VALUE_TWO("two"),
        VALUE_THREE("three");

        private final String value;

        TestValueEnum(String value) {
            this.value = value;
        }

        @SuppressWarnings("unused")
        public String getValue() {
            return value;
        }
    }

    /**
     * Test enum without getValue method for testing fallback to the enum name
     */
    private enum TestNameEnum {
        NAME_ONE,
        NAME_TWO,
        NAME_THREE
    }

    private static final char CSV_DELIMITER = '|';
    private static final String IS_VALID = "isValid";

    private ConstraintValidatorContext context;
    private List<String> capturedMessages;
    private EnumValueValidator enumValueValidator;

    @BeforeEach
    void setUp() {
        ConstraintViolationCapture.Captured captured = ConstraintViolationCapture.mockContext();
        context = captured.context();
        capturedMessages = captured.messages();
        enumValueValidator = new EnumValueValidator();
    }

    @Order(1)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Parameterized Tests for Valid Cases")
    @ParameterizedTest(name = "Given value={0}, should return true")
    @ValueSource(strings = {"one", "ONE"})
    @NullSource
    void isValid_ValidCases_ShouldReturnTrue(String inputValue) {
        EnumValueValidation enumValueValidation = mock(EnumValueValidation.class);
        when(enumValueValidation.enumClass()).thenAnswer(inv -> TestValueEnum.class);

        enumValueValidator.initialize(enumValueValidation);

        boolean isValid = enumValueValidator.isValid(inputValue, context);

        assertTrue(isValid, String.format("isValid should return true for the input value: '%s'", inputValue));
    }

    @Order(3)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given an invalid enum value, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O valor invalid é inválido. Atualmente, os valores válidos são: one, three, two.",
            "en_US|The value invalid is invalid. Currently, the valid values are: one, three, two."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidEnumValue_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        TestLocale.set(languageTag);

        EnumValueValidation enumValueValidation = mock(EnumValueValidation.class);
        when(enumValueValidation.enumClass()).thenAnswer(inv -> TestValueEnum.class);
        enumValueValidator.initialize(enumValueValidation);

        boolean isValid = enumValueValidator.isValid("invalid", context);

        assertEquals(expectedMessage, lastMessage());
        assertFalse(isValid, "isValid should return false for an invalid enum value");
    }

    @Order(4)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a valid enum name when getValue is not available, then should return true")
    @Test
    void isValid_WhenValidEnumNameAndGetValueNotAvailable_ThenShouldReturnTrue() {
        EnumValueValidation enumValueValidation = mock(EnumValueValidation.class);
        when(enumValueValidation.enumClass()).thenAnswer(inv -> TestNameEnum.class);
        enumValueValidator.initialize(enumValueValidation);

        boolean isValid = enumValueValidator.isValid("NAME_ONE", context);

        assertTrue(isValid, "isValid should return true for a valid enum name when getValue is not available");
    }

    @Order(5)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given an invalid enum name when getValue is not available, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O valor INVALID é inválido. Atualmente, os valores válidos são: NAME_ONE, NAME_THREE, NAME_TWO.",
            "en_US|The value INVALID is invalid. Currently, the valid values are: NAME_ONE, NAME_THREE, NAME_TWO."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidEnumNameAndGetValueNotAvailable_ThenShouldReturnFalse(String languageTag,
                                                                                  String expectedMessage) {
        TestLocale.set(languageTag);

        EnumValueValidation enumValueValidation = mock(EnumValueValidation.class);
        when(enumValueValidation.enumClass()).thenAnswer(inv -> TestNameEnum.class);

        EnumValueValidator localValidator = new EnumValueValidator();
        localValidator.initialize(enumValueValidation);

        boolean isValid = localValidator.isValid("INVALID", context);

        assertEquals(expectedMessage, lastMessage());
        assertFalse(isValid,
                "isValid should return false for an invalid enum name when getValue is not available");
    }

    @Order(6)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given hideValidOptions=true, then should hide valid values in error message")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O valor invalid é inválido.",
            "en_US|The value invalid is invalid."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenHideValidOptionsTrue_ThenShouldHideValidValues(String languageTag, String expectedMessage) {
        TestLocale.set(languageTag);

        EnumValueValidation enumValueValidation = mock(EnumValueValidation.class);
        when(enumValueValidation.enumClass()).thenAnswer(inv -> TestValueEnum.class);
        when(enumValueValidation.hideValidOptions()).thenReturn(true);

        EnumValueValidator localValidator = new EnumValueValidator();
        localValidator.initialize(enumValueValidation);

        boolean isValid = localValidator.isValid("invalid", context);

        String capturedMessage = lastMessage();
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(capturedMessage.contains("valid values are"));
        assertFalse(capturedMessage.contains("one, three, two"));
        assertFalse(isValid, "isValid should return false for an invalid enum value");
    }

    private String lastMessage() {
        assertFalse(capturedMessages.isEmpty(), "No constraint violation was captured");
        return capturedMessages.get(capturedMessages.size() - 1);
    }
}
